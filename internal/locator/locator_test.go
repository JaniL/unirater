package locator

import (
	"math"
	"testing"
)

// Reference distance computed with an independent haversine
// implementation (vectors from the Exactum entrance).
var (
	exactum  = GeoPoint{Latitude: 60.2044631, Longitude: 24.9615065}
	physicum = GeoPoint{Latitude: 60.2050081, Longitude: 24.9621377}
)

func TestHaversineKnownDistance(t *testing.T) {
	got := HaversineMeters(exactum.Latitude, exactum.Longitude, physicum.Latitude, physicum.Longitude)

	// Independent check: dlat = 0.000545 deg = 60.6 m north,
	// dlon = 0.0006312 deg x cos(60.2045) = 34.8 m east,
	// hypotenuse = sqrt(60.6^2 + 34.8^2) = 69.9 m.
	if math.Abs(got-70) > 3 {
		t.Errorf("haversine(Exactum, Physicum) = %.1f m, want ~70 m", got)
	}
}

func TestHaversineZero(t *testing.T) {
	got := HaversineMeters(60.2, 24.9, 60.2, 24.9)
	if got != 0 {
		t.Errorf("haversine(p, p) = %f, want 0", got)
	}
}

func TestHaversineAntipodal(t *testing.T) {
	// Half the earth circumference: ~20015 km.
	got := HaversineMeters(0, 0, 0, 180)
	if math.Abs(got-20015000) > 50000 {
		t.Errorf("haversine antipodal = %.0f m, want ~20015000 m", got)
	}
}

func TestFindClosest(t *testing.T) {
	restaurants := []Restaurant{
		{URI: "physicum", Name: "Physicum"},
		{URI: "chemicum", Name: "Chemicum"},
	}

	// Standing in front of Physicum should pick Physicum.
	if got := FindClosest(restaurants, physicum.Longitude, physicum.Latitude); got != "physicum" {
		t.Errorf("FindClosest at Physicum = %q, want physicum", got)
	}
}

func TestFindClosestFallback(t *testing.T) {
	restaurants := []Restaurant{
		{URI: "unknown-place", Name: "Somewhere Unmapped"},
	}
	if got := FindClosest(restaurants, 24.9, 60.2); got != FallbackURI {
		t.Errorf("FindClosest unmapped = %q, want %q", got, FallbackURI)
	}
	if got := FindClosest(nil, 24.9, 60.2); got != FallbackURI {
		t.Errorf("FindClosest empty = %q, want %q", got, FallbackURI)
	}
}

func TestResolveLocationByURIAndName(t *testing.T) {
	if _, ok := ResolveLocation("exactum", ""); !ok {
		t.Error("ResolveLocation(exactum) missed")
	}
	if _, ok := ResolveLocation("", "Exactum"); !ok {
		t.Error("ResolveLocation(name=Exactum) missed")
	}
	if _, ok := ResolveLocation("no-such-uri", "No Such Restaurant"); ok {
		t.Error("ResolveLocation matched unknown restaurant")
	}
}

func TestKnownLocationsAliases(t *testing.T) {
	for _, key := range []string{"porthania", "porthania-opettajien-ravintola", "sockom", "soc-kom", "portaali", "cafe-portaali"} {
		if _, ok := KnownLocations()[key]; !ok {
			t.Errorf("known location %q missing", key)
		}
	}
}
