package httpapp

import (
	"log"
	"net/http"

	"github.com/JaniL/unirater/internal/geofence"
)

// blockedMessage is shown when the geofence rejects a request.
const blockedMessage = "Pääsy sallittu vain Suomen IP-osoitteista."

// requireFinland wraps auth and write handlers so that only requests
// from Finnish client addresses pass. The X-Real-IP header is trusted
// (TRUST_PROXY) because the Scaleway serverless gateway overwrites it
// on every request; the header cannot be spoofed through the platform.
func (s *Server) requireFinland(next http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if !geofence.Allowed(r, s.trustProxy) {
			log.Printf("httpapp: geofence blocked %s %s from %s",
				r.Method, r.URL.Path, geofence.DescribeIP(r, s.trustProxy))
			http.Error(w, blockedMessage, http.StatusForbidden)
			return
		}
		next(w, r)
	}
}
