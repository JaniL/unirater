package config

import (
	"crypto/rand"
	"encoding/hex"
)

func randRead(b []byte) (int, error) { return rand.Read(b) }

func encodeHex(b []byte) []byte {
	out := make([]byte, hex.EncodedLen(len(b)))
	hex.Encode(out, b)
	return out
}
