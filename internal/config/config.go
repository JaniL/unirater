package config

import (
	"log"
	"os"
	"strings"
)

// Config carries the runtime configuration, read from the environment.
type Config struct {
	Port            string
	DatabaseURL     string
	SessionSecret   []byte
	SyncOnStartup   bool
	GeofenceEnabled bool
	TrustProxy      bool
}

// Load reads configuration from environment variables.
//
// PORT             - HTTP port, defaults to 8080.
// DATABASE_URL     - PostgreSQL connection string, required.
// SESSION_SECRET   - key used to sign session cookies, required in production;
// a random per-process key is generated when unset (sessions then do not
// survive restarts).
// SYNC_ON_STARTUP  - "true" fetches and saves all UniCafe menus at startup
// (parity with the old dev profile); otherwise menus are synced lazily on
// the first request that needs them.
// GEOFENCE_ENABLED - "true" restricts auth and write operations (login,
// registration, voting, manual menu sync) to Finnish IP addresses.
// Defaults to false.
// TRUST_PROXY      - "true" (default) takes the client address from
// X-Real-IP, which the Scaleway gateway overwrites on every request;
// "false" always uses the transport-level RemoteAddr.
func Load() Config {
	var cfg Config

	cfg.Port = os.Getenv("PORT")
	if cfg.Port == "" {
		cfg.Port = "8080"
	}

	cfg.DatabaseURL = os.Getenv("DATABASE_URL")
	if cfg.DatabaseURL == "" {
		log.Fatal("config: DATABASE_URL is required")
	}

	secret := os.Getenv("SESSION_SECRET")
	if secret == "" {
		log.Println("config: SESSION_SECRET unset, generating a random one (sessions will not survive restarts)")
		secret = randomSecret()
	}
	cfg.SessionSecret = []byte(secret)

	cfg.SyncOnStartup = boolEnv("SYNC_ON_STARTUP", false)
	cfg.GeofenceEnabled = boolEnv("GEOFENCE_ENABLED", false)
	cfg.TrustProxy = boolEnv("TRUST_PROXY", true)

	return cfg
}

// boolEnv reads a boolean environment variable, "1" and "true" (case
// insensitive) counting as true, with a default when unset.
func boolEnv(name string, defaultValue bool) bool {
	value := strings.TrimSpace(os.Getenv(name))
	if value == "" {
		return defaultValue
	}
	switch strings.ToLower(value) {
	case "1", "true", "yes", "on":
		return true
	case "0", "false", "no", "off":
		return false
	default:
		return defaultValue
	}
}

func randomSecret() string {
	b := make([]byte, 32)
	if _, err := randRead(b); err != nil {
		log.Fatalf("config: reading random bytes: %v", err)
	}
	return string(encodeHex(b))
}
