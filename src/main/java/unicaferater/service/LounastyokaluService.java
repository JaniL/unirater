package unicaferater.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import unicaferater.domain.RestaurantResponse;
import unicaferater.domain.RestaurantsResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class LounastyokaluService {
	private String API_URL = "http://messi.hyyravintolat.fi/publicapi";
	private RestTemplate restTemplate = new RestTemplate();
	
	public RestaurantsResponse fetchRestaurants(){		
		RestaurantsResponse result = restTemplate.getForObject(API_URL + "/restaurants", RestaurantsResponse.class);
		
		
		System.out.println(result);
		return result;
		
	}

    public RestaurantResponse fetchRestaurant(Long id) {
        RestaurantResponse result = restTemplate.getForObject(API_URL + "/restaurant/" + id, RestaurantResponse.class);

        System.out.println(result);

        return result;
    }
	
	
}
