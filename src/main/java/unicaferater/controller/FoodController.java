/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.MenuOfTheDayRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.MenuOfTheDay;
import unicaferater.domain.database.Rating;
import unicaferater.domain.database.Restaurant;

/**
 *
 * @author chang
 */
@RequestMapping("/*")
@Controller
public class FoodController {

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private MenuOfTheDayRepository menuRepo;

    /**
     * Listaa kaikki ruokalistat ja ravintolat omaan modeliin
     *
     * @param model
     * @return palauttaa indexi sivun
     */
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String listFoods(Model model) {
        model.addAttribute("menus", menuRepo.findAll());
        model.addAttribute("restaurants", restaurantRepo.findAll());

        return "index";
    }

    /**
     * Listaa pyydetyn ravintolan ruokalistat ja ravintolat
     * @param model
     * @param restaurantUri
     * @return Palauttaa sivun, joka sisältää ravintolan ruokalistat sekä ravintolat valikossa
     */
    @RequestMapping(value = "/{restaurantUri}", method = RequestMethod.GET)
    public String listFoodsByRestaurant(Model model, @PathVariable String restaurantUri) {

        List<MenuOfTheDay> menus;
        if (restaurantUri.equals("favicon") || restaurantUri.equals("")) {
            menus = menuRepo.findAll();
        } else {
            Restaurant restaurant = restaurantRepo.findByUri(restaurantUri);
            menus = menuRepo.findByRestaurant(restaurant);
        }
        model.addAttribute("menus", menus);
        model.addAttribute("restaurants", restaurantRepo.findAll());
        return "index";
    }

    /**
     * Tekee POST pyynnön kyseisen Ruuan ID:hen 
     * Pitäisi ottaa Modelista Rating arvio.
     * Rating lisätään päivämäärä jollain arvostelu on annettu. 
     * lisätään rating ruualle annettujen arvostelujen listaan.
     * Tallenetaan kaikki.
     * @param foodId
     * @param rating
     * @return HEP! Tällä hetkellä ohjaa meilahteen. 
     * Miten saataisiin ohjaamaan sinne mistä on tullut? 
     * Tai jonnekin relevanttiin paikkan?
     */
    @RequestMapping(value = "/{foodId}", method = RequestMethod.POST)
    public String postRating(@PathVariable Long foodId, @ModelAttribute Rating rating) {
        Food food = foodRepo.findOne(foodId);

        Date date = new Date();
        rating.setDate(date);
        ratingRepo.findAll().add(rating);
        
        List<Rating> ratingNewList =  foodRepo.findOne(foodId).getRatings();
        ratingNewList.add(rating);
        ratingRepo.save(rating);
        food.setRatings(ratingNewList);
        food.getRatingResult(); // Että tallentaisi totaalin. Voi muuttaa sinne frontiinki.
        foodRepo.save(food);
        return "redirect:/11"; // minne tän pitäs ohjata uudelleen. 
    }
}
