package httpapp

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

// TestStaticServed guards the embedded static FS wiring: /static/<path>
// must resolve to the files under internal/httpapp/static (the embed
// directive keeps them under static/, so the FS must be re-rooted with
// fs.Sub before StripPrefix).
func TestStaticServed(t *testing.T) {
	s, err := New([]byte("test"), nil, nil, nil, false, true)
	if err != nil {
		t.Fatalf("New: %v", err)
	}
	routes := s.Routes()

	cases := []struct {
		path        string
		code        int
		contentType string
	}{
		{"/static/css/bootstrap.min.css", http.StatusOK, "text/css"},
		{"/static/css/bootstrap.css", http.StatusOK, "text/css"},
		{"/static/js/main.js", http.StatusOK, "text/javascript"},
		{"/static/fonts/glyphicons-halflings-regular.woff", http.StatusOK, "font/woff"},
		{"/static/css/missing.css", http.StatusNotFound, ""},
	}
	for _, c := range cases {
		r := httptest.NewRequest(http.MethodGet, c.path, nil)
		w := httptest.NewRecorder()
		routes.ServeHTTP(w, r)

		if w.Code != c.code {
			t.Errorf("%s: status = %d, want %d", c.path, w.Code, c.code)
			continue
		}
		if c.code == http.StatusOK {
			if !strings.HasPrefix(w.Header().Get("Content-Type"), c.contentType) {
				t.Errorf("%s: content-type = %q, want prefix %q", c.path, w.Header().Get("Content-Type"), c.contentType)
			}
			if w.Body.Len() == 0 {
				t.Errorf("%s: empty body", c.path)
			}
		}
	}

	// Path traversal is neutralized by the stdlib: unclean paths get a
	// redirect to their cleaned form instead of being served.
	r := httptest.NewRequest(http.MethodGet, "/static/css/../../etc/passwd", nil)
	w := httptest.NewRecorder()
	routes.ServeHTTP(w, r)
	if w.Code != http.StatusMovedPermanently &&
		w.Code != http.StatusTemporaryRedirect &&
		w.Code != http.StatusPermanentRedirect {
		t.Errorf("traversal: status = %d, want a cleaning redirect", w.Code)
	}
	if strings.Contains(w.Body.String(), "root:") {
		t.Error("traversal: served a file outside the static root")
	}
}
