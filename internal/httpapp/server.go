package httpapp

import (
	"bytes"
	"context"
	"embed"
	"fmt"
	"html/template"
	"io/fs"
	"log"
	"net/http"
	"sync"

	"github.com/gorilla/sessions"

	"github.com/JaniL/unirater/internal/accounts"
	"github.com/JaniL/unirater/internal/store"
	"github.com/JaniL/unirater/internal/unicafe"
)

//go:embed templates/*.html
var templateFS embed.FS

//go:embed static
var staticFS embed.FS

// Server is the UniRater HTTP application.
type Server struct {
	store    *store.Store
	sessions sessions.Store
	accounts *accounts.Service
	client   *unicafe.Client
	tpl      *template.Template
	static   http.Handler

	geofenceEnabled bool
	trustProxy      bool

	syncOnce sync.Once
}

// New builds the server. When geofenceEnabled is set, auth and write
// operations are restricted to Finnish client addresses (feature flag
// GEOFENCE_ENABLED; off by default).
func New(sessionSecret []byte, st *store.Store, acc *accounts.Service, client *unicafe.Client, geofenceEnabled, trustProxy bool) (*Server, error) {
	tpl, err := template.ParseFS(templateFS, "templates/*.html")
	if err != nil {
		return nil, fmt.Errorf("httpapp: parsing templates: %w", err)
	}

	// The embed directive stores the assets under static/; re-root the
	// FS so StripPrefix("/static/") resolves /static/css/x.css to
	// static/css/x.css.
	staticRoot, err := fs.Sub(staticFS, "static")
	if err != nil {
		return nil, fmt.Errorf("httpapp: opening embedded static root: %w", err)
	}

	return &Server{
		store:           st,
		sessions:        newSessionStore(sessionSecret),
		accounts:        acc,
		client:          client,
		tpl:             tpl,
		static:          http.StripPrefix("/static/", http.FileServerFS(staticRoot)),
		geofenceEnabled: geofenceEnabled,
		trustProxy:      trustProxy,
	}, nil
}

// authState is injected into every page.
type authState struct {
	Authenticated bool
	Email         string
}

// pageData is the view model shared by all templates.
type pageData struct {
	Auth        authState
	CSRF        string
	Restaurants []store.Restaurant
	Restaurant  *store.Restaurant
	Menus       []store.Menu
	LoginError  bool
	Form        accounts.RegistrationForm
	Errors      map[string]string
}

// Routes returns the fully wired HTTP handler.
func (s *Server) Routes() http.Handler {
	mux := http.NewServeMux()

	mux.HandleFunc("GET /health", s.handleHealth)

	// The geofence middleware (feature flag GEOFENCE_ENABLED, off by
	// default) restricts auth and write operations to Finnish client
	// addresses. Loopback and private addresses always pass, so local
	// development keeps working unchanged.
	gate := func(next http.HandlerFunc) http.HandlerFunc {
		if !s.geofenceEnabled {
			return next
		}
		return s.requireFinland(next)
	}

	mux.HandleFunc("GET /{$}", s.handleHome)
	mux.HandleFunc("GET /json/closest/{longitude}/{latitude}", s.handleClosest)
	mux.HandleFunc("GET /json/etsiUri/{restaurantNimi}", s.handleFindURI)
	mux.HandleFunc("GET /rate/{foodId}/{vote}", gate(s.handleRate))
	mux.HandleFunc("GET /{restaurantUri}", s.handleRestaurant)

	mux.HandleFunc("GET /user/login", s.handleLoginPage)
	mux.HandleFunc("POST /user/login/authenticate", gate(s.handleLoginAuthenticate))
	mux.HandleFunc("GET /user/logout", s.handleLogout)
	mux.HandleFunc("POST /user/logout", s.handleLogout)
	mux.HandleFunc("GET /user/register", s.handleRegisterPage)
	mux.HandleFunc("POST /user/register", gate(s.handleRegister))
	mux.HandleFunc("GET /signup", s.handleSignup)

	mux.HandleFunc("GET /unicafe/saveall", gate(s.handleSaveAll))
	mux.HandleFunc("GET /unicafe/stats", s.handleStats)

	mux.Handle("GET /static/", s.static)

	return mux
}

// renderPage fills the shared view model fields (auth, CSRF) and renders
// the named template, writing a 500 on template failure.
func (s *Server) renderPage(w http.ResponseWriter, r *http.Request, name string, data pageData) {
	if user := s.currentUser(r); user != nil {
		data.Auth = authState{Authenticated: true, Email: user.Email}
	}
	if data.CSRF == "" {
		token, err := s.csrfToken(w, r)
		if err != nil {
			http.Error(w, "session error", http.StatusInternalServerError)
			return
		}
		data.CSRF = token
	}

	var buf bytes.Buffer
	if err := s.tpl.ExecuteTemplate(&buf, name, data); err != nil {
		http.Error(w, "template error", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	_, _ = w.Write(buf.Bytes())
}

// ensureSynced runs the UniCafe menu sync at most once per server
// process, mirroring the startup sync of the old dev profile, but
// triggered lazily so serverless cold starts stay cheap.
func (s *Server) ensureSynced() {
	s.syncOnce.Do(func() {
		ctx := context.Background()
		items, err := s.client.FetchRestaurantFeed(ctx)
		if err != nil {
			log.Printf("httpapp: lazy menu sync skipped: %v", err)
			return
		}
		if err := unicafe.SyncAll(ctx, items, s.store); err != nil {
			log.Printf("httpapp: lazy menu sync failed: %v", err)
		}
	})
}
