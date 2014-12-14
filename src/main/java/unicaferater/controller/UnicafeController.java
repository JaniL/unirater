package unicaferater.controller;

import com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Price;
import unicaferater.domain.database.Restaurant;
import unicaferater.domain.lounastyokalu.FoodDetails;
import unicaferater.domain.lounastyokalu.MenuOfTheDay;
import unicaferater.domain.lounastyokalu.RestaurantResponse;
import unicaferater.domain.lounastyokalu.RestaurantsResponse;
import unicaferater.service.LounastyokaluService;
import unicaferater.service.RestaurantService;
import unicaferater.service.UnicafeService;

import javax.transaction.Transactional;

/**
 *
 * @author Neodyymi
 */
@Controller
@RequestMapping("/unicafe")
@ResponseBody
public class UnicafeController {

    @Autowired
    private UnicafeService unicafeService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;
    
    @Autowired
    private LounastyokaluService lounastyokaluService;

    @RequestMapping(method = RequestMethod.GET)
    public String list() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        /* URL url = new URL("http://www.hyyravintolat.fi/rss/fin/11/");
        unicafeService.fetchInfo(url);

        String ret = unicafeService.getAll();
        return ret; */
    	
    	lounastyokaluService.fetchRestaurants();
        System.out.println("pow");
        RestaurantResponse restaurantResponse = lounastyokaluService.fetchRestaurant((long) 9);

        for (MenuOfTheDay menuOfTheDay : restaurantResponse.getData()) {
            System.out.println(menuOfTheDay.getDate());
            for (FoodDetails foodDetails : menuOfTheDay.getData()) {
                System.out.println(foodDetails.getName() + " (" + foodDetails.getPrice().getName() + ")");
            }

        }

        return "";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String listOne(@PathVariable int id) throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        URL url = new URL("http://www.hyyravintolat.fi/rss/fin/" + id + "/");
        unicafeService.fetchInfo(url);

        String ret = unicafeService.getAll();
        return ret;
    }

    @RequestMapping(value = "saved", method = RequestMethod.GET)
    public String fromRepo() {

        String ret = restaurantService.listAllFoodsAndRestaurants();
        return ret;
    }

    @RequestMapping(value = "save/{id}", method = RequestMethod.GET)
    public String toRepo(@PathVariable int id) throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        URL url = new URL("http://www.hyyravintolat.fi/rss/fin/" + id + "/");
        unicafeService.fetchInfo(url);
        unicafeService.storeFoodsForWeek();

        return ("Saved: " + unicafeService.getTitle());
    }

    @Transactional
    @RequestMapping(value = "saveall", method = RequestMethod.GET)
    public String allToRepo() throws IOException, IllegalArgumentException, FeedException {
        // return unicafeService.storeAll();

        RestaurantsResponse restaurantsResponse = lounastyokaluService.fetchRestaurants();
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

            RestaurantResponse restaurantResponse = lounastyokaluService.fetchRestaurant(id);


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

        return "saved things!";

    }
}
