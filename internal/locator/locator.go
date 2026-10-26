// Package locator resolves the UniCafe restaurant nearest to a pair of
// coordinates. It is a port of RestaurantLocatorService: restaurant
// coordinates are not stored in the database but in a hardcoded table of
// known Helsinki campus locations, and distances are haversine.
package locator

import (
	"sync"

	"github.com/JaniL/unirater/internal/slug"
)

// FallbackURI is returned when no known location can be resolved,
// mirroring RestaurantLocatorService.FALLBACK_URI.
const FallbackURI = "exactum"

const earthRadiusMeters = 6371000.0

// GeoPoint is a WGS84 coordinate.
type GeoPoint struct {
	Latitude  float64
	Longitude float64
}

var (
	knownOnce sync.Once
	known     map[string]GeoPoint
)

// KnownLocations returns the hardcoded restaurant coordinates keyed by
// normalized URI or name. Multiple keys may map to the same point.
func KnownLocations() map[string]GeoPoint {
	knownOnce.Do(func() {
		known = map[string]GeoPoint{}

		add := func(lat, long float64, keys ...string) {
			point := GeoPoint{Latitude: lat, Longitude: long}
			for _, key := range keys {
				if normalized := slug.Key(key); normalized != "" {
					known[normalized] = point
				}
			}
		}

		add(60.188217, 24.9064114, "terkko")
		add(60.2230185, 25.0192946, "tahka")
		add(60.2262356, 25.0163981, "biokeskus", "biokeskus-2", "biokeskus-3")
		add(60.2273463, 25.012731, "infokeskus", "infokeskus-alakerra", "infokeskus-alakerta")
		add(60.1693996, 24.9407624, "myoha", "myoha-cafe-bar", "kaivopiha")
		add(60.1710677, 24.94779, "kaisa-talo", "well-kaisa-talo")
		add(60.2293081, 25.0219459, "viikuna")
		add(60.1729842, 24.9527106, "soc-kom", "sockom")
		add(60.1710892, 24.9469878, "serpens")
		add(60.1703421, 24.9502665, "rotunda")
		add(60.1702958, 24.9484087, "porthania", "porthania-opettajien-ravintola")
		add(60.2050081, 24.9621377, "physicum")
		add(60.1709, 24.9513, "topelias", "pesco-vege-topelias")
		add(60.1714, 24.9505, "olivia")
		add(60.1726583, 24.9492317, "metsatalo")
		add(60.1905362, 24.909113, "meilahti")
		add(60.2044631, 24.9615065, "exactum")
		add(60.205562, 24.9638146, "chemicum", "chemicum-opettajien-ravintola")
		add(60.1705956, 24.9478616, "cafe-portaali", "portaali")
	})

	return known
}

// ResolveLocation looks a restaurant up in the known locations, first by
// its URI, then by its name, mirroring RestaurantLocatorService
// #resolveLocation.
func ResolveLocation(uri, name string) (GeoPoint, bool) {
	locations := KnownLocations()

	if key := slug.Key(uri); key != "" {
		if point, ok := locations[key]; ok {
			return point, true
		}
	}
	if key := slug.Key(name); key != "" {
		if point, ok := locations[key]; ok {
			return point, true
		}
	}
	return GeoPoint{}, false
}

// Restaurant is the subset of a restaurant needed for locating.
type Restaurant struct {
	URI  string
	Name string
}

// FindClosest returns the URI of the restaurant nearest to the given
// longitude/latitude, or FallbackURI when none of the restaurants has a
// known location. It is a port of
// RestaurantLocatorService#findClosestRestaurantUri.
func FindClosest(restaurants []Restaurant, longitude, latitude float64) string {
	closestURI := ""
	closestDistance := maxFloat64

	for _, restaurant := range restaurants {
		point, ok := ResolveLocation(restaurant.URI, restaurant.Name)
		if !ok {
			continue
		}

		distance := HaversineMeters(latitude, longitude, point.Latitude, point.Longitude)
		if distance < closestDistance {
			closestDistance = distance
			closestURI = restaurant.URI
		}
	}

	if closestURI != "" {
		return closestURI
	}
	return FallbackURI
}

// HaversineMeters returns the great-circle distance between two
// coordinates in meters.
func HaversineMeters(latitudeA, longitudeA, latitudeB, longitudeB float64) float64 {
	latitudeDelta := toRadians(latitudeB - latitudeA)
	longitudeDelta := toRadians(longitudeB - longitudeA)
	latitudeARadians := toRadians(latitudeA)
	latitudeBRadians := toRadians(latitudeB)

	sinLatitude := sin(latitudeDelta / 2.0)
	sinLongitude := sin(longitudeDelta / 2.0)
	a := sinLatitude*sinLatitude +
		cos(latitudeARadians)*cos(latitudeBRadians)*sinLongitude*sinLongitude

	return earthRadiusMeters * 2.0 * atan2(sqrt(a), sqrt(1.0-a))
}

const maxFloat64 = 1.797693134862315708145274237317043567981e+308
