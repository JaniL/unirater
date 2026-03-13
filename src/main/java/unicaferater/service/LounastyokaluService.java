package unicaferater.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import unicaferater.Repository.*;
import unicaferater.domain.common.PriceValue;
import unicaferater.domain.database.*;
import unicaferater.domain.lounastyokalu.*;
import unicaferater.domain.lounastyokalu.MenuOfTheDay;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class LounastyokaluService {
    private static final String API_URL = "https://unicafe.fi/wp-json/swiss/v1";
    private static final String RESTAURANTS_ENDPOINT = "/restaurants/?lang=fi";
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private MenuOfTheDayRepository menuOfTheDayRepository;

    @Autowired
    private PriceRepository priceRepo;

    @Autowired
    private PriceValueRepository priceValueRepository;

    /**
     * Hakee Unicafen rajapinnasta ravintolat ja niiden id:t.
     * @return Palauttaa Unicafen ravintolat
     */
	public RestaurantsResponse fetchRestaurants(){		
		RestaurantFeedItem[] items = fetchRestaurantFeed();

        RestaurantsResponse result = new RestaurantsResponse();
        result.setStatus("OK");

        List<Restaurant> restaurants = new ArrayList<>();
        if (items != null) {
            for (RestaurantFeedItem item : items) {
                RestaurantResponse restaurantResponse = item.getMenuData();
                if (restaurantResponse == null) {
                    continue;
                }

                String name = restaurantResponse.getName();
                if (name == null || name.trim().isEmpty()) {
                    name = item.getTitle();
                }

                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                Restaurant restaurant = new Restaurant();
                restaurant.setName(name);
                if (restaurantResponse.getAreacode() != null) {
                    restaurant.setAreacode(restaurantResponse.getAreacode());
                }

                restaurants.add(restaurant);
            }
        }

        result.setData(restaurants);
        return result;
	}

    /**
     * Hakee Unicafen rajapinnasta ruokalistoja ravintolan id:n mukaan
     * @param id Ravintolan id Unicafen rajanpinnassa
     * @return Palauttaa pyydetyn ravintolan ruokalistat sekä informaatiota ravintolasta. (mm. aukioloajat)
     */
    public RestaurantResponse fetchRestaurant(Long id) {
        RestaurantFeedItem[] items = fetchRestaurantFeed();
        if (items == null) {
            return null;
        }

        for (RestaurantFeedItem item : items) {
            RestaurantResponse restaurantResponse = item.getMenuData();
            if (restaurantResponse != null && id.equals(restaurantResponse.getId())) {
                return restaurantResponse;
            }
        }

        return null;
    }

    /**
     * Tallentaa kaikki rajapinnasta saatavat ruokalistat tietokantaan.
     * @param restaurantsResponse Rajapinnasta saatava lista ravintoloista. Saadaan irti fetchRestaurants-metodilla.
     */
    public void saveAllToRepo(RestaurantsResponse restaurantsResponse) {
        RestaurantFeedItem[] items = fetchRestaurantFeed();
        if (items == null) {
            return;
        }

        for (RestaurantFeedItem item : items) {
            RestaurantResponse restaurantResponse = item.getMenuData();
            if (restaurantResponse == null) {
                continue;
            }

            String restaurantName = restaurantResponse.getName();
            if (restaurantName == null || restaurantName.trim().isEmpty()) {
                restaurantName = item.getTitle();
            }
            if (restaurantName == null || restaurantName.trim().isEmpty()) {
                continue;
            }

            Restaurant repoRes = restaurantRepository.findByName(restaurantName);
            if (repoRes == null) {
                repoRes = new Restaurant();
                repoRes.setName(restaurantName);
                if (restaurantResponse.getAreacode() != null) {
                    repoRes.setAreacode(restaurantResponse.getAreacode());
                }
                repoRes = restaurantRepository.save(repoRes);
            }

            List<unicaferater.domain.database.MenuOfTheDay> menus = repoRes.getMenus();
            if (menus == null) {
                menus = new ArrayList<>();
            }

            List<MenuOfTheDay> sourceMenus = restaurantResponse.getData();
            if (sourceMenus == null) {
                continue;
            }

            for (MenuOfTheDay menuOfTheDay : sourceMenus) {
                if (menuOfTheDay == null) {
                    continue;
                }

                Date date = menuOfTheDay.getDate();
                if (date == null) {
                    continue;
                }

                if (menuOfTheDayRepository.findByRestaurantAndDate(repoRes, date) != null) {
                    continue;
                }

                List<FoodDetails> menuFoods = menuOfTheDay.getData();
                if (menuFoods == null || menuFoods.isEmpty()) {
                    continue;
                }

                unicaferater.domain.database.MenuOfTheDay dbMenu = new unicaferater.domain.database.MenuOfTheDay();
                dbMenu.setRestaurant(repoRes);
                dbMenu.setDate(date);

                List<Food> foods = new ArrayList<>();
                for (FoodDetails foodDetails : menuFoods) {
                    if (foodDetails == null || foodDetails.getName() == null || foodDetails.getName().trim().isEmpty()) {
                        continue;
                    }

                    Food food = foodRepository.findByNameAndRestaurant(foodDetails.getName(), repoRes);
                    if (food == null) {
                        food = new Food();
                        food.setName(foodDetails.getName());
                        food.setLastSeenOnMenu(date);

                        unicaferater.domain.common.Price price = resolvePrice(foodDetails);
                        if (price != null) {
                            food.setPrice(price);
                        }

                        food.setRestaurant(repoRes);
                        food = foodRepository.save(food);
                    }

                    foods.add(food);
                }

                if (foods.isEmpty()) {
                    continue;
                }

                dbMenu.setMenu(foods);
                dbMenu = menuOfTheDayRepository.save(dbMenu);
                menus.add(dbMenu);
            }

            repoRes.setMenus(menus);
            restaurantRepository.save(repoRes);
        }
    }

    private RestaurantFeedItem[] fetchRestaurantFeed() {
        return restTemplate.getForObject(API_URL + RESTAURANTS_ENDPOINT, RestaurantFeedItem[].class);
    }

    private unicaferater.domain.common.Price resolvePrice(FoodDetails foodDetails) {
        if (foodDetails.getPrice() == null || foodDetails.getPrice().getName() == null) {
            return null;
        }

        unicaferater.domain.common.Price price = priceRepo.findByName(foodDetails.getPrice().getName());
        if (price != null) {
            return price;
        }

        PriceValue value = foodDetails.getPrice().getValue();
        if (value != null) {
            value = priceValueRepository.save(value);
            foodDetails.getPrice().setValue(value);
        }

        return priceRepo.save(foodDetails.getPrice());
    }

}
