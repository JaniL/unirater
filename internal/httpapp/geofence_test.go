package httpapp

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestRequireFinlandGate(t *testing.T) {
	s := &Server{geofenceEnabled: true, trustProxy: true}

	called := false
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) { called = true })

	cases := []struct {
		name string
		ip   string
		code int
	}{
		{"finnish passes", "217.169.64.10", http.StatusOK},
		{"foreign blocked", "8.8.8.8", http.StatusForbidden},
		{"foreign (DE) blocked", "193.99.144.85", http.StatusForbidden},
	}

	for _, c := range cases {
		called = false
		r := httptest.NewRequest(http.MethodGet, "/rate/1/1", nil)
		r.Header.Set("X-Real-IP", c.ip)
		w := httptest.NewRecorder()

		s.requireFinland(next).ServeHTTP(w, r)

		if w.Code != c.code {
			t.Errorf("%s: status = %d, want %d", c.name, w.Code, c.code)
		}
		if (w.Code == http.StatusOK) != called {
			t.Errorf("%s: next called = %v, want %v", c.name, called, w.Code == http.StatusOK)
		}
		if c.code == http.StatusForbidden && !strings.Contains(w.Body.String(), blockedMessage) {
			t.Errorf("%s: body missing message: %q", c.name, w.Body.String())
		}
	}
}

func TestRequireFinlandLoopbackPasses(t *testing.T) {
	s := &Server{geofenceEnabled: true, trustProxy: true}

	called := false
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) { called = true })

	// No X-Real-IP: RemoteAddr from httptest is 192.0.2.1 (TEST-NET),
	// which is public but not Finnish, so blocked...
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	w := httptest.NewRecorder()
	s.requireFinland(next).ServeHTTP(w, r)
	if called {
		t.Error("non-FI TEST-NET address passed the gate")
	}

	// ...but loopback passes.
	r = httptest.NewRequest(http.MethodGet, "/", nil)
	r.RemoteAddr = "127.0.0.1:12345"
	w = httptest.NewRecorder()
	s.requireFinland(next).ServeHTTP(w, r)
	if !called {
		t.Error("loopback address did not pass the gate")
	}
}

func TestGateWiringDisabledFlag(t *testing.T) {
	// With the flag off, Routes must register the plain handlers: the
	// gate closure returns next unchanged.
	s := &Server{geofenceEnabled: false}
	called := false
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) { called = true })

	gate := func(f http.HandlerFunc) http.HandlerFunc {
		if !s.geofenceEnabled {
			return f
		}
		return s.requireFinland(f)
	}

	gate(next).ServeHTTP(httptest.NewRecorder(), httptest.NewRequest(http.MethodGet, "/", nil))
	if !called {
		t.Error("flag off: next handler not called")
	}
}

func TestGateWiringEnabledFlag(t *testing.T) {
	s := &Server{geofenceEnabled: true, trustProxy: true}
	called := false
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) { called = true })

	gate := func(f http.HandlerFunc) http.HandlerFunc {
		if !s.geofenceEnabled {
			return f
		}
		return s.requireFinland(f)
	}

	// Foreign address through the gate: blocked, next not called.
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	r.Header.Set("X-Real-IP", "8.8.8.8")
	gate(next).ServeHTTP(httptest.NewRecorder(), r)
	if called {
		t.Error("flag on: foreign address reached next handler")
	}
}
