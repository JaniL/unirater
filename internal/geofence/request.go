package geofence

import (
	"fmt"
	"net"
	"net/http"
	"net/netip"
	"strings"
)

// realIPHeader is set by the Scaleway serverless gateway, which
// overwrites any client-supplied value.
const realIPHeader = "X-Real-IP"

// ClientIP resolves the originating client address of a request.
//
// When trustProxy is set, X-Real-IP wins (the platform guarantees it
// reflects the caller); otherwise, or when the header is absent or
// unparsable, the transport-level RemoteAddr is used.
func ClientIP(r *http.Request, trustProxy bool) netip.Addr {
	if trustProxy {
		if value := strings.TrimSpace(r.Header.Get(realIPHeader)); value != "" {
			if addr, err := netip.ParseAddr(value); err == nil {
				return addr
			}
		}
	}
	return remoteAddr(r)
}

// remoteAddr extracts the address part of RemoteAddr (host:port).
func remoteAddr(r *http.Request) netip.Addr {
	host, _, err := net.SplitHostPort(r.RemoteAddr)
	if err != nil {
		host = r.RemoteAddr
	}
	addr, err := netip.ParseAddr(host)
	if err != nil {
		return netip.Addr{}
	}
	return addr
}

// Allowed reports whether a request may perform auth or write
// operations: loopback and private addresses always pass (local
// development, container-to-container traffic), public addresses must
// be Finnish.
func Allowed(r *http.Request, trustProxy bool) bool {
	addr := ClientIP(r, trustProxy)
	if !addr.IsValid() {
		// No address to judge (e.g. an empty RemoteAddr); fail closed.
		return false
	}
	if IsPrivate(addr) {
		return true
	}
	return IsFinnishIP(addr)
}

// DescribeIP renders the client address for logs.
func DescribeIP(r *http.Request, trustProxy bool) string {
	addr := ClientIP(r, trustProxy)
	if !addr.IsValid() {
		return "unknown"
	}
	return fmt.Sprintf("%s", addr)
}
