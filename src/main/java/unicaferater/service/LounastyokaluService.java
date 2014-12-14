package unicaferater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Price;
import unicaferater.domain.database.Restaurant;
import unicaferater.domain.lounastyokalu.FoodDetails;
import unicaferater.domain.lounastyokalu.MenuOfTheDay;
import unicaferater.domain.lounastyokalu.RestaurantResponse;
import unicaferater.domain.lounastyokalu.RestaurantsResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LounastyokaluService {
	private String API_URL = "http://messi.hyyravintolat.fi/publicapi";
	private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

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

        // System.out.println(result);

        return result;
    }

    public void saveAllToRepo(RestaurantsResponse restaurantsResponse) {
        for (Restaurant restaurant : restaurantsResponse.getData()) {
            // System.out.println("Käydään läpi ravintolan " + restaurant.getName() + " listat. (id " + restaurant.getId() + ")");
            Restaurant repoRes = restaurantRepository.findByName(restaurant.getName());

            Long id = restaurant.getId();

            if (repoRes == null) {
                repoRes = restaurantRepository.save(restaurant);
            }

            List<Food> foods;
            if (repoRes.getFoods() == null) {
                foods = new ArrayList<>();
            } else {
                foods = repoRes.getFoods();
            }

            RestaurantResponse restaurantResponse = fetchRestaurant(id);


            for (MenuOfTheDay menuOfTheDay : restaurantResponse.getData()) {
                Date date = menuOfTheDay.getDate();
                for (FoodDetails foodDetails : menuOfTheDay.getData()) {
                    Food food = new Food();

                    food.setName(foodDetails.getName());
                    food.setLastSeenOnMenu(date);

                    Price price = null;
                    String priceString = foodDetails.getPrice().getName();

                    System.out.println("PriceString: " + priceString);

                    if (priceString.equals("Edullisesti")) {
                        price = Price.Edullisesti;
                    } else if (priceString.equals("Maukkaasti")) {
                        price = Price.Maukkaasti;
                    } else if (priceString.equals("Kevyesti")) {
                        price = Price.Kevyesti;
                    } else if (priceString.equals("Makeasti")) {
                        price = Price.Makeasti;
                    }

                    System.out.println("Olio: " + price);

                    food.setPrice(price);

                    food.setRestaurant(repoRes);
                    foodRepository.save(food);

                    if (foods.isEmpty() || !foods.contains(food)) {
                        foods.add(food);
                    }
                }

            }
            repoRes.setFoods(foods);
            restaurantRepository.save(repoRes);



        }
    }
	
	
}
