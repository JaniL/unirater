package unicaferater.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import unicaferater.Repository.*;
import unicaferater.domain.common.PriceValue;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Restaurant;
import unicaferater.domain.lounastyokalu.*;


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
		RestaurantsResponse result = restTemplate.getForObject(API_URL + "/restaurants", RestaurantsResponse.class);
		
		
		// System.out.println(result);

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

    /**
     * Tallentaa kaikki rajapinnasta saatavat ruokalistat tietokantaan.
     * @param restaurantsResponse Rajapinnasta saatava lista ravintoloista. Saadaan irti fetchRestaurants-metodilla.
     */
    public void saveAllToRepo(RestaurantsResponse restaurantsResponse) {
        for (Restaurant restaurant : restaurantsResponse.getData()) {
            // System.out.println("Käydään läpi ravintolan " + restaurant.getName() + " listat. (id " + restaurant.getId() + ")");
            Restaurant repoRes = restaurantRepository.findByName(restaurant.getName());

            Long id = restaurant.getId();

            if (repoRes == null) {
                repoRes = restaurantRepository.save(restaurant);
            }

            //List<Food> foods;
            // if (repoRes.getFoods() == null) {
            //    foods = new ArrayList<>();
            // } else {
            //    foods = repoRes.getFoods();
            // }

            // System.out.println("Lisätään ravintolan " + repoRes.getName() + " ruokalistat.");

            RestaurantResponse restaurantResponse = fetchRestaurant(id);

            List<unicaferater.domain.database.MenuOfTheDay> menus = repoRes.getMenus();
            if (menus == null) {
                menus = new ArrayList<>();
            }


            for (MenuOfTheDay menuOfTheDay : restaurantResponse.getData()) {
                Date date = menuOfTheDay.getDate();

                if (menuOfTheDayRepository.findByRestaurantAndDate(repoRes,date) != null) {
                    // jos päivän lista löytyy jo,
                    // niin listaa ei lisätä uudelleen

                    // System.out.println("Lista löytyy jo, ei lisätä.");
                    continue;
                }

                if (menuOfTheDay.getData().size() == 0) {
                    // jos lista on tyhjä,
                    // niin tyhjää listaa ei lisätä

                    // System.out.println("Lista on tyhjä, ei lisätä.");
                    continue;
                }

                unicaferater.domain.database.MenuOfTheDay dbMenu = new unicaferater.domain.database.MenuOfTheDay();
                dbMenu.setRestaurant(repoRes);
                dbMenu.setDate(date);

                List<Food> foods = new ArrayList<>();
                for (FoodDetails foodDetails : menuOfTheDay.getData()) {

                    Food food = foodRepository.findByName(foodDetails.getName());

                    if (food == null) {
                        food = new Food();

                        food.setName(foodDetails.getName());
                        food.setLastSeenOnMenu(date);

                        unicaferater.domain.common.Price price = null;
                        String priceString = foodDetails.getPrice().getName();

                        // System.out.println("PriceString: " + priceString);


                        // System.out.println("Olio: " + price);

                        price = priceRepo.findByName(foodDetails.getPrice().getName());
                        if (price == null) {
                            PriceValue value = foodDetails.getPrice().getValue();
                            value = priceValueRepository.save(value);
                            foodDetails.getPrice().setValue(value);
                            price = priceRepo.save(foodDetails.getPrice());
                        }
                        food.setPrice(price);

                        food.setRestaurant(repoRes);
                        foodRepository.save(food);
                    }




                    // if (foods.isEmpty() || !foods.contains(food)) {
                    foods.add(food);
                    // }
                }
                dbMenu.setMenu(foods);
                dbMenu = menuOfTheDayRepository.save(dbMenu);
                menus.add(dbMenu);
            }

            // repoRes.setFoods(foods);
            repoRes.setMenus(menus);
            repoRes = restaurantRepository.save(repoRes);


        }
    }
	
	
}
