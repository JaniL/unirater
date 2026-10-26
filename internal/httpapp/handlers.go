package httpapp

import (
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"strconv"
	"strings"

	"github.com/JaniL/unirater/internal/accounts"
	"github.com/JaniL/unirater/internal/locator"
	"github.com/JaniL/unirater/internal/store"
	"github.com/JaniL/unirater/internal/unicafe"
)

// maxFormSize caps POST bodies.
const maxFormSize = 1 << 20

// handleHealth answers the container health check. Port of HealthController.
func (s *Server) handleHealth(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain; charset=utf-8")
	_, _ = w.Write([]byte("ok"))
}

// handleHome renders the geolocation landing page. Port of
// FoodController#locateUser.
func (s *Server) handleHome(w http.ResponseWriter, r *http.Request) {
	s.ensureSynced()

	restaurants, err := s.store.ListRestaurants(r.Context())
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		log.Printf("httpapp: listing restaurants: %v", err)
		return
	}

	s.renderPage(w, r, "paikannus.html", pageData{Restaurants: restaurants})
}

// handleRestaurant renders the menu page of one restaurant. Port of
// FoodController#listFoodsByRestaurant.
func (s *Server) handleRestaurant(w http.ResponseWriter, r *http.Request) {
	s.ensureSynced()

	uri := r.PathValue("restaurantUri")

	var (
		restaurant *store.Restaurant
		menus      []store.Menu
		err        error
	)

	switch uri {
	case "favicon", "":
		menus, err = s.store.AllMenus(r.Context())
		if err != nil {
			http.Error(w, "database error", http.StatusInternalServerError)
			log.Printf("httpapp: listing all menus: %v", err)
			return
		}
	default:
		found, err := s.store.RestaurantByURI(r.Context(), uri)
		if errors.Is(err, store.ErrNotFound) {
			restaurant = nil
		} else if err != nil {
			http.Error(w, "database error", http.StatusInternalServerError)
			log.Printf("httpapp: finding restaurant %q: %v", uri, err)
			return
		} else {
			restaurant = found
		}
		if restaurant != nil {
			menus, err = s.store.MenusByRestaurant(r.Context(), restaurant.ID)
			if err != nil {
				http.Error(w, "database error", http.StatusInternalServerError)
				log.Printf("httpapp: listing menus of %q: %v", uri, err)
				return
			}
		}
	}

	restaurants, err := s.store.ListRestaurants(r.Context())
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		log.Printf("httpapp: listing restaurants: %v", err)
		return
	}

	s.renderPage(w, r, "index.html", pageData{
		Restaurants: restaurants,
		Restaurant:  restaurant,
		Menus:       menus,
	})
}

// handleClosest answers the geolocation JSON endpoint. Port of
// FoodController#closestRestaurant.
func (s *Server) handleClosest(w http.ResponseWriter, r *http.Request) {
	longitude, err := strconv.ParseFloat(r.PathValue("longitude"), 64)
	if err != nil {
		http.Error(w, "bad longitude", http.StatusBadRequest)
		return
	}
	latitude, err := strconv.ParseFloat(r.PathValue("latitude"), 64)
	if err != nil {
		http.Error(w, "bad latitude", http.StatusBadRequest)
		return
	}

	restaurants, err := s.store.ListRestaurants(r.Context())
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		return
	}

	located := make([]locator.Restaurant, len(restaurants))
	for i, restaurant := range restaurants {
		located[i] = locator.Restaurant{URI: restaurant.URI, Name: restaurant.Name}
	}

	uri := locator.FindClosest(located, longitude, latitude)
	if strings.TrimSpace(uri) == "" {
		uri = locator.FallbackURI
	}

	w.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(w).Encode(map[string]string{"uri": uri})
}

// handleFindURI answers the URI lookup endpoint. Port of
// FoodController#etsiUri.
func (s *Server) handleFindURI(w http.ResponseWriter, r *http.Request) {
	name := r.PathValue("restaurantNimi")

	restaurant, err := s.store.RestaurantByName(r.Context(), name)
	if errors.Is(err, store.ErrNotFound) {
		w.Header().Set("Content-Type", "text/plain; charset=utf-8")
		_, _ = w.Write([]byte(""))
		return
	}
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "text/plain; charset=utf-8")
	_, _ = w.Write([]byte(restaurant.URI))
}

// handleRate records a vote and redirects back. Port of
// FoodController#postRating: vote 0 maps to -1, anything else to +1.
func (s *Server) handleRate(w http.ResponseWriter, r *http.Request) {
	foodID, err := strconv.ParseInt(r.PathValue("foodId"), 10, 64)
	if err != nil {
		http.Error(w, "bad food id", http.StatusBadRequest)
		return
	}
	vote, err := strconv.Atoi(r.PathValue("vote"))
	if err != nil {
		http.Error(w, "bad vote", http.StatusBadRequest)
		return
	}

	user := s.currentUser(r)
	if user == nil {
		http.Redirect(w, r, "/user/login", http.StatusFound)
		return
	}

	value := 1
	if vote == 0 {
		value = -1
	}

	if err := s.store.SetRating(r.Context(), user.ID, foodID, value); err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		log.Printf("httpapp: setting rating: %v", err)
		return
	}

	http.Redirect(w, r, refererOrDefault(r), http.StatusFound)
}

// refererOrDefault returns the Referer when it points at this site,
// mirroring the Java redirect-to-Referer behavior without the open
// redirect.
func refererOrDefault(r *http.Request) string {
	referer := r.Header.Get("Referer")
	if referer == "" {
		return "/"
	}
	ref, err := url.Parse(referer)
	if err != nil || ref.Host != r.Host || (ref.Scheme != "http" && ref.Scheme != "https") {
		return "/"
	}
	return ref.String()
}

// parseForm reads the request body with a size cap. It accepts both
// urlencoded and multipart forms.
func parseForm(w http.ResponseWriter, r *http.Request) error {
	r.Body = http.MaxBytesReader(w, r.Body, maxFormSize)
	return r.ParseForm()
}

// handleLoginPage renders the login form. Port of LoginController.
func (s *Server) handleLoginPage(w http.ResponseWriter, r *http.Request) {
	s.renderPage(w, r, "login.html", pageData{
		LoginError: r.URL.Query().Get("error") == "bad_credentials",
	})
}

// handleLoginAuthenticate processes the login form. Port of the Spring
// Security form-login filter.
func (s *Server) handleLoginAuthenticate(w http.ResponseWriter, r *http.Request) {
	if err := parseForm(w, r); err != nil {
		http.Error(w, "bad form", http.StatusBadRequest)
		return
	}
	if !s.verifyCSRF(r) {
		http.Error(w, "CSRF token mismatch", http.StatusForbidden)
		return
	}

	user, err := s.accounts.Authenticate(r.Context(),
		strings.TrimSpace(r.FormValue("username")), r.FormValue("password"))
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		return
	}
	if user == nil {
		http.Redirect(w, r, "/user/login?error=bad_credentials", http.StatusFound)
		return
	}

	if err := s.login(w, r, user.ID); err != nil {
		http.Error(w, "session error", http.StatusInternalServerError)
		return
	}
	http.Redirect(w, r, "/", http.StatusFound)
}

// handleLogout clears the session. Port of the Spring Security logout.
func (s *Server) handleLogout(w http.ResponseWriter, r *http.Request) {
	if err := s.logout(w, r); err != nil {
		http.Error(w, "session error", http.StatusInternalServerError)
		return
	}
	http.Redirect(w, r, "/", http.StatusFound)
}

// handleRegisterPage renders the registration form. Port of
// RegistrationController#showRegistrationForm (social sign-in dropped).
func (s *Server) handleRegisterPage(w http.ResponseWriter, r *http.Request) {
	s.renderPage(w, r, "registration.html", pageData{})
}

// handleRegister processes the registration form. Port of
// RegistrationController#registerUserAccount.
func (s *Server) handleRegister(w http.ResponseWriter, r *http.Request) {
	if err := parseForm(w, r); err != nil {
		http.Error(w, "bad form", http.StatusBadRequest)
		return
	}
	if !s.verifyCSRF(r) {
		http.Error(w, "CSRF token mismatch", http.StatusForbidden)
		return
	}

	form := accounts.RegistrationForm{
		Email:                strings.TrimSpace(r.FormValue("email")),
		FirstName:            strings.TrimSpace(r.FormValue("firstName")),
		LastName:             strings.TrimSpace(r.FormValue("lastName")),
		Password:             r.FormValue("password"),
		PasswordVerification: r.FormValue("passwordVerification"),
	}

	renderFailure := func(errs map[string]string) {
		s.renderPage(w, r, "registration.html", pageData{Form: form, Errors: errs})
	}

	user, err := s.accounts.Register(r.Context(), form)
	var validationErr accounts.ValidationError
	switch {
	case errors.As(err, &validationErr):
		renderFailure(validationErr)
		return
	case errors.Is(err, accounts.ErrDuplicateEmail):
		renderFailure(map[string]string{"email": "Sähköposti on jo käytössä."})
		return
	case err != nil:
		http.Error(w, "database error", http.StatusInternalServerError)
		log.Printf("httpapp: registering user: %v", err)
		return
	}

	if err := s.login(w, r, user.ID); err != nil {
		http.Error(w, "session error", http.StatusInternalServerError)
		return
	}
	http.Redirect(w, r, "/", http.StatusFound)
}

// handleSignup redirects to the registration page. Port of
// SignUpController.
func (s *Server) handleSignup(w http.ResponseWriter, r *http.Request) {
	http.Redirect(w, r, "/user/register", http.StatusFound)
}

// handleSaveAll forces a full menu sync. Port of UnicafeController#allToRepo.
func (s *Server) handleSaveAll(w http.ResponseWriter, r *http.Request) {
	items, err := s.client.FetchRestaurantFeed(r.Context())
	if err != nil {
		http.Error(w, "unicafe error", http.StatusBadGateway)
		log.Printf("httpapp: fetching unicafe feed: %v", err)
		return
	}
	if err := unicafe.SyncAll(r.Context(), items, s.store); err != nil {
		http.Error(w, "sync error", http.StatusInternalServerError)
		log.Printf("httpapp: syncing unicafe feed: %v", err)
		return
	}

	w.Header().Set("Content-Type", "text/plain; charset=utf-8")
	_, _ = fmt.Fprintln(w, "saved things!")
}

// handleStats reports the menu count. Port of UnicafeController#stats.
func (s *Server) handleStats(w http.ResponseWriter, r *http.Request) {
	count, err := s.store.CountMenus(r.Context())
	if err != nil {
		http.Error(w, "database error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "text/plain; charset=utf-8")
	_, _ = fmt.Fprintf(w, "Listoja löytyy: %d\n", count)
}
