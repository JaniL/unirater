/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.Food;
import unicaferater.domain.Restaurant;

/**
 *
 * @author Neodyymi
 */
@Service
public class RestaurantService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public String listFoodsFromOne(Long id) {
        Restaurant restaurant = restaurantRepository.findOne(id);
        String ret = new String();
        ret += "<br>\n";
        ret += restaurant.getName();
        ret += "<br>\n";
        ret += "<p>";
        for (Food f : restaurant.getFoods()) {
            ret += "*" + f.getName();
            ret += " - " + f.getPrice();
            ret += "<br>\n";
        }
        ret += "</p>";
        return ret;
    }

    public String listAllFoodsAndRestaurants() {
        String ret = new String();
        int i = 1;
        for (Restaurant r : restaurantRepository.findAll()) {

            ret += "<br>\n";
            ret += i + ". " + r.getName();
            i++;
            ret += "<br>\n";
            ret += "<p>";
            for (Food f : r.getFoods()) {
                ret += "*" + f.getName();
                ret += " - " + f.getPrice();
                ret += "<br>\n";
            }
            ret += "</p>";
            ret += "<hr>";
        }
        ret += "<p>";
        for (Food f : foodRepository.findAll()) {

            ret += "*" + f.getName();
            ret += " - " + f.getPrice();
            if (f.getRestaurant() != null) {
                ret += " - " + f.getRestaurant().getName();
            }
            ret += " - " + f.getRatingResult();
            ret += "<br>\n";
        }
        ret += "</p>";

        return ret;

    }

}
