// Package geofence restricts auth and write operations to requests made
// from Finnish IP addresses.
//
// The client IP is taken from the X-Real-IP header, which Scaleway's
// serverless gateway overwrites on every request (see the Serverless
// Containers HTTP headers reference), making it safe to trust behind the
// platform proxy. Direct connections (local development) fall back to
// RemoteAddr.
//
// The Finland address space is embedded as aggregated CIDR snapshots from
// the ipverse/country-ip-blocks repository (RIR delegation data). Run
// "make gen-fi" to refresh them.
package geofence

import (
	"bufio"
	_ "embed"
	"net/netip"
	"strings"
	"sync"
)

//go:embed data/fi-ipv4.txt
var fiIPv4Data string

//go:embed data/fi-ipv6.txt
var fiIPv6Data string

var (
	prefixesOnce sync.Once
	prefixes     []netip.Prefix
)

// parseCIDRs parses a snapshot file: one CIDR per line, # comments.
func parseCIDRs(data string) []netip.Prefix {
	var out []netip.Prefix
	scanner := bufio.NewScanner(strings.NewReader(data))
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}
		if prefix, err := netip.ParsePrefix(line); err == nil {
			out = append(out, prefix)
		}
	}
	return out
}

// FIPrefixes returns the parsed Finnish address space, once per process.
func FIPrefixes() []netip.Prefix {
	prefixesOnce.Do(func() {
		prefixes = append(parseCIDRs(fiIPv4Data), parseCIDRs(fiIPv6Data)...)
	})
	return prefixes
}

// IsFinnishIP reports whether the address belongs to a Finnish prefix.
func IsFinnishIP(addr netip.Addr) bool {
	if !addr.IsValid() {
		return false
	}
	addr = addr.Unmap()
	for _, prefix := range FIPrefixes() {
		if prefix.Contains(addr) {
			return true
		}
	}
	return false
}

// IsPrivate reports whether the address is loopback or private
// (RFC 1918/4193, link-local). Such addresses never leave the host or
// LAN and are always allowed.
func IsPrivate(addr netip.Addr) bool {
	if !addr.IsValid() {
		return false
	}
	addr = addr.Unmap()
	return addr.IsLoopback() ||
		addr.IsPrivate() ||
		addr.IsLinkLocalUnicast()
}
