package httpapp

import (
	"net/http"

	"github.com/gorilla/sessions"

	"github.com/JaniL/unirater/internal/store"
)

const (
	sessionName    = "unirater_session"
	sessionUserID  = "user_id"
	sessionCSRFKey = "csrf"
)

// newSessionStore builds the cookie-backed session store.
func newSessionStore(secret []byte) sessions.Store {
	cookie := sessions.NewCookieStore(secret)
	cookie.Options.HttpOnly = true
	cookie.Options.Path = "/"
	cookie.Options.MaxAge = 0 // session cookie, cleared on browser exit
	cookie.Options.SameSite = http.SameSiteLaxMode
	return cookie
}

// session returns the request's session.
func (s *Server) session(r *http.Request) (*sessions.Session, error) {
	return s.sessions.Get(r, sessionName)
}

// currentUser resolves the logged in user, or nil.
func (s *Server) currentUser(r *http.Request) *store.User {
	sess, err := s.session(r)
	if err != nil {
		return nil
	}
	raw, ok := sess.Values[sessionUserID]
	if !ok {
		return nil
	}
	id, ok := raw.(int64)
	if !ok {
		return nil
	}

	user, err := s.store.UserByID(r.Context(), id)
	if err != nil {
		return nil
	}
	return user
}

// login stores the user id in the session and persists it.
func (s *Server) login(w http.ResponseWriter, r *http.Request, userID int64) error {
	sess, err := s.session(r)
	if err != nil {
		return err
	}
	sess.Values[sessionUserID] = userID
	return sess.Save(r, w)
}

// logout clears the session.
func (s *Server) logout(w http.ResponseWriter, r *http.Request) error {
	sess, err := s.session(r)
	if err != nil {
		return err
	}
	sess.Options.MaxAge = -1
	delete(sess.Values, sessionUserID)
	return sess.Save(r, w)
}

// csrfToken returns (creating lazily) the per-session CSRF token.
func (s *Server) csrfToken(w http.ResponseWriter, r *http.Request) (string, error) {
	sess, err := s.session(r)
	if err != nil {
		return "", err
	}
	if token, ok := sess.Values[sessionCSRFKey].(string); ok && token != "" {
		return token, nil
	}

	buf := make([]byte, 32)
	if _, err := randRead(buf); err != nil {
		return "", err
	}
	token := hexEncode(buf)
	sess.Values[sessionCSRFKey] = token
	if err := sess.Save(r, w); err != nil {
		return "", err
	}
	return token, nil
}

// verifyCSRF checks the _csrf form field against the session token.
func (s *Server) verifyCSRF(r *http.Request) bool {
	sess, err := s.session(r)
	if err != nil {
		return false
	}
	token, _ := sess.Values[sessionCSRFKey].(string)
	if token == "" {
		return false
	}
	return subtleEqual(token, r.FormValue("_csrf"))
}
