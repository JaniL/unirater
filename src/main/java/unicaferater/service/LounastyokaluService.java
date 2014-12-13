package unicaferater.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import unicaferater.domain.RestaurantResponse;
import unicaferater.domain.RestaurantsResponse;


@Service
public class LounastyokaluService {
	private String API_URL = "http://messi.hyyravintolat.fi/publicapi";
	private RestTemplate restTemplate = new RestTemplate();


    /**
     * Hakee Unicafen rajapinnasta ravintolat ja niiden id:t.
     * @return Palauttaa Unicafen ravintolat
     */
	public RestaurantsResponse fetchRestaurants(){		
		RestaurantsResponse result = restTemplate.getForObject(API_URL + "/restaurants", RestaurantsResponse.class);
		
		
		System.out.println(result);
		return result;
		
	}

    /**
     * Hakee Unicafen rajapinnasta ruokalistoja ravintolan id:n mukaan
     * @param id Ravintolan id Unicafen rajanpinnassa
     * @return Palauttaa pyydetyn ravintolan ruokalistat sekä informaatiota ravintolasta. (mm. aukioloajat)
     */
    public RestaurantResponse fetchRestaurant(Long id) {
        RestaurantResponse result = restTemplate.getForObject(API_URL + "/restaurant/" + id, RestaurantResponse.class);

        System.out.println(result);

        return result;
    }
	
	
}
