package unicaferater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Restaurant;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantLocatorService {
    private static final String FALLBACK_URI = "exactum";
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private static final Map<String, GeoPoint> KNOWN_LOCATIONS = createKnownLocations();

    @Autowired
    private RestaurantRepository restaurantRepository;

    public String findClosestRestaurantUri(double longitude, double latitude) {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        String closestRestaurantUri = null;
        double closestDistance = Double.MAX_VALUE;

        for (Restaurant restaurant : restaurants) {
            GeoPoint location = resolveLocation(restaurant);
            if (location == null) {
                continue;
            }

            double distance = haversineMeters(latitude, longitude, location.latitude, location.longitude);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestRestaurantUri = restaurant.getUri();
            }
        }

        if (closestRestaurantUri != null && !closestRestaurantUri.trim().isEmpty()) {
            return closestRestaurantUri;
        }

        return FALLBACK_URI;
    }

    private GeoPoint resolveLocation(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        String uriKey = normalizeKey(restaurant.getUri());
        if (uriKey != null) {
            GeoPoint uriLocation = KNOWN_LOCATIONS.get(uriKey);
            if (uriLocation != null) {
                return uriLocation;
            }
        }

        String nameKey = normalizeKey(restaurant.getName());
        if (nameKey != null) {
            return KNOWN_LOCATIONS.get(nameKey);
        }

        return null;
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return null;
        }

        String normalized = Normalizer.normalize(value.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");

        if (normalized.isEmpty()) {
            return null;
        }

        return normalized;
    }

    private static double haversineMeters(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        double latitudeDelta = Math.toRadians(latitudeB - latitudeA);
        double longitudeDelta = Math.toRadians(longitudeB - longitudeA);
        double latitudeAInRadians = Math.toRadians(latitudeA);
        double latitudeBInRadians = Math.toRadians(latitudeB);

        double sinLatitude = Math.sin(latitudeDelta / 2.0);
        double sinLongitude = Math.sin(longitudeDelta / 2.0);
        double a = sinLatitude * sinLatitude
                + Math.cos(latitudeAInRadians) * Math.cos(latitudeBInRadians) * sinLongitude * sinLongitude;
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS_METERS * c;
    }

    private static Map<String, GeoPoint> createKnownLocations() {
        Map<String, GeoPoint> locations = new HashMap<>();

        addLocation(locations, 60.188217, 24.9064114, "terkko");
        addLocation(locations, 60.2230185, 25.0192946, "tahka");
        addLocation(locations, 60.2262356, 25.0163981, "biokeskus", "biokeskus-2", "biokeskus-3");
        addLocation(locations, 60.2273463, 25.012731, "infokeskus", "infokeskus-alakerta");
        addLocation(locations, 60.1693996, 24.9407624, "myoha", "myoha-cafe-bar", "kaivopiha");
        addLocation(locations, 60.1710677, 24.94779, "kaisa-talo", "well-kaisa-talo");
        addLocation(locations, 60.2293081, 25.0219459, "viikuna");
        addLocation(locations, 60.1729842, 24.9527106, "soc-kom", "sockom");
        addLocation(locations, 60.1703421, 24.9502665, "rotunda");
        addLocation(locations, 60.1702958, 24.9484087, "porthania", "porthania-opettajien-ravintola");
        addLocation(locations, 60.2050081, 24.9621377, "physicum");
        addLocation(locations, 60.1709, 24.9513, "topelias", "pesco-vege-topelias");
        addLocation(locations, 60.1714, 24.9505, "olivia");
        addLocation(locations, 60.1726583, 24.9492317, "metsatalo");
        addLocation(locations, 60.1905362, 24.909113, "meilahti");
        addLocation(locations, 60.2044631, 24.9615065, "exactum");
        addLocation(locations, 60.205562, 24.9638146, "chemicum", "chemicum-opettajien-ravintola");
        addLocation(locations, 60.1705956, 24.9478616, "cafe-portaali");

        return locations;
    }

    private static void addLocation(Map<String, GeoPoint> locations, double latitude, double longitude, String... keys) {
        GeoPoint location = new GeoPoint(latitude, longitude);
        for (String key : keys) {
            String normalizedKey = normalizeKey(key);
            if (normalizedKey != null) {
                locations.put(normalizedKey, location);
            }
        }
    }

    private static class GeoPoint {
        private final double latitude;
        private final double longitude;

        private GeoPoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
