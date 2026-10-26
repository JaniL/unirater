package httpapp

import (
	"crypto/rand"
	"crypto/subtle"
	"encoding/hex"
)

func randRead(b []byte) (int, error) { return rand.Read(b) }

func hexEncode(b []byte) string { return hex.EncodeToString(b) }

func subtleEqual(a, b string) bool {
	return subtle.ConstantTimeCompare([]byte(a), []byte(b)) == 1
}
