package geofence

import (
	"net/http"
	"net/http/httptest"
	"net/netip"
	"testing"
)

func mustAddr(t *testing.T, s string) netip.Addr {
	t.Helper()
	addr, err := netip.ParseAddr(s)
	if err != nil {
		t.Fatalf("parsing %q: %v", s, err)
	}
	return addr
}

func TestIsFinnishIP(t *testing.T) {
	cases := []struct {
		ip   string
		want bool
	}{
		// Finnish ranges present in the embedded snapshot.
		{"217.169.64.10", true},
		{"217.169.78.1", true}, // inside 217.169.64.0/20
		{"130.232.0.1", true},  // University of Helsinki range
		{"128.214.0.1", true},  // CSC / Funet
		{"193.166.0.1", true},
		{"2001:708::1", true}, // Funet IPv6
		// Not Finnish.
		{"8.8.8.8", false},
		{"1.1.1.1", false},
		{"193.99.144.85", false}, // German
		{"2606:4700::1", false},  // Cloudflare US
	}
	for _, c := range cases {
		if got := IsFinnishIP(mustAddr(t, c.ip)); got != c.want {
			t.Errorf("IsFinnishIP(%s) = %v, want %v", c.ip, got, c.want)
		}
	}
}

func TestIsPrivate(t *testing.T) {
	cases := []struct {
		ip   string
		want bool
	}{
		{"127.0.0.1", true},
		{"::1", true},
		{"10.0.0.5", true},
		{"192.168.1.1", true},
		{"172.16.0.9", true},
		{"169.254.1.1", true},
		{"fd00::1", true},
		{"8.8.8.8", false},
		{"217.169.64.10", false},
	}
	for _, c := range cases {
		if got := IsPrivate(mustAddr(t, c.ip)); got != c.want {
			t.Errorf("IsPrivate(%s) = %v, want %v", c.ip, got, c.want)
		}
	}
}

func TestClientIPTrustedProxy(t *testing.T) {
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	r.Header.Set(realIPHeader, "217.169.64.10")
	r.RemoteAddr = "192.0.2.1:1234"

	if got := ClientIP(r, true); got.String() != "217.169.64.10" {
		t.Errorf("ClientIP(trust) = %v, want 217.169.64.10", got)
	}
	if got := ClientIP(r, false); got.String() != "192.0.2.1" {
		t.Errorf("ClientIP(no trust) = %v, want 192.0.2.1", got)
	}
}

func TestClientIPFallsBackOnGarbageHeader(t *testing.T) {
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	r.Header.Set(realIPHeader, "not-an-ip")
	r.RemoteAddr = "198.51.100.7:999"

	if got := ClientIP(r, true); got.String() != "198.51.100.7" {
		t.Errorf("ClientIP(garbage header) = %v, want 198.51.100.7", got)
	}
}

func TestClientIPv6(t *testing.T) {
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	r.Header.Set(realIPHeader, "2001:708::10")
	if got := ClientIP(r, true); got.String() != "2001:708::10" {
		t.Errorf("ClientIP(v6) = %v, want 2001:708::10", got)
	}
}

func TestAllowed(t *testing.T) {
	cases := []struct {
		name   string
		header string
		remote string
		want   bool
	}{
		{"finnish via proxy", "217.169.64.10", "10.0.0.1:1", true},
		{"foreign via proxy", "8.8.8.8", "10.0.0.1:1", false},
		{"loopback direct", "", "127.0.0.1:5555", true},
		{"private direct", "", "192.168.1.5:4444", true},
		{"foreign direct", "", "8.8.8.8:1234", false},
		{"finnish direct", "", "130.232.9.1:1234", true},
		{"unparseable remote fails closed", "", ":not-an-addr", false},
	}
	for _, c := range cases {
		r := httptest.NewRequest(http.MethodGet, "/", nil)
		if c.header != "" {
			r.Header.Set(realIPHeader, c.header)
		}
		r.RemoteAddr = c.remote
		if got := Allowed(r, true); got != c.want {
			t.Errorf("%s: Allowed = %v, want %v", c.name, got, c.want)
		}
	}
}

func TestAllowedNoProxyUsesRemoteAddr(t *testing.T) {
	r := httptest.NewRequest(http.MethodGet, "/", nil)
	r.Header.Set(realIPHeader, "217.169.64.10") // spoofed by client
	r.RemoteAddr = "8.8.8.8:4321"
	if Allowed(r, false) {
		t.Error("no-trust mode honored spoofed X-Real-IP")
	}
}
