// Package slug generates the SEO friendly URI names used in restaurant
// URLs. It is a port of the normalization done by
// Restaurant#setUri and RestaurantLocatorService#normalizeKey in the
// original Java application.
package slug

import (
	"strings"
	"unicode"

	"golang.org/x/text/unicode/norm"
)

// URI returns the SEO friendly URI for a restaurant name: lowercased,
// decomposed to NFD form, diacritics stripped and every run of
// non-alphanumeric characters replaced with a single dash.
// This mirrors Restaurant#setUri in the Java application.
func URI(name string) string {
	return collapseDashes(dashNonAlnum(stripMarks(norm.NFD.String(strings.ToLower(name)))))
}

// Key returns the normalized lookup key used by the restaurant locator:
// like URI but with leading and trailing dashes trimmed. A value that
// normalizes to nothing yields "".
func Key(value string) string {
	return strings.Trim(URI(value), "-")
}

func stripMarks(s string) string {
	return strings.Map(func(r rune) rune {
		if unicode.Is(unicode.Mn, r) {
			return -1
		}
		return r
	}, s)
}

func dashNonAlnum(s string) string {
	return strings.Map(func(r rune) rune {
		if isAlnum(r) {
			return r
		}
		return '-'
	}, s)
}

func collapseDashes(s string) string {
	for strings.Contains(s, "--") {
		s = strings.ReplaceAll(s, "--", "-")
	}
	return s
}

func isAlnum(r rune) bool {
	return ('a' <= r && r <= 'z') ||
		('A' <= r && r <= 'Z') ||
		('0' <= r && r <= '9')
}
