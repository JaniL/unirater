package unicafe

import (
	"regexp"
	"strconv"
	"strings"
	"time"
)

// The API date fields look like "4.11.", occasionally like
// "Mon 04.11" or similar. The original application parsed them with a
// (\d{1,2})\.(\d{1,2})\. regex pinned to the current year, with locale
// aware fallbacks; we keep the same behavior.
var (
	primaryDate   = regexp.MustCompile(`(\d{1,2})\.(\d{1,2})\.`)
	secondaryDate = regexp.MustCompile(`(\d{1,2})\.(\d{1,2})`)
)

// ParseDate parses a date string from the UniCafe API into a midnight
// timestamp in the current year. ok is false when the string carries no
// recognizable date.
func ParseDate(dateString string) (time.Time, bool) {
	trimmed := strings.TrimSpace(dateString)
	if trimmed == "" {
		return time.Time{}, false
	}

	if m := primaryDate.FindStringSubmatch(trimmed); m != nil {
		return dateFromParts(m[1], m[2]), true
	}
	if m := secondaryDate.FindStringSubmatch(trimmed); m != nil {
		return dateFromParts(m[1], m[2]), true
	}

	return time.Time{}, false
}

func dateFromParts(day, month string) time.Time {
	d, _ := strconv.Atoi(day)
	mo, _ := strconv.Atoi(month)
	year := time.Now().Year()

	// time.Date normalizes out-of-range parts the same way the lenient
	// Java Calendar did (e.g. month 13 rolls into next January).
	return time.Date(year, time.Month(mo), d, 0, 0, 0, 0, time.Local)
}
