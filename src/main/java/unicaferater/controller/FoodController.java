/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.controller;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.Food;
import unicaferater.domain.Rating;

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

    /**
     * Listaa kaikki ruuat ja ravintolat omaan modeliin
     *
     * @param model
     * @return palauttaa indexi sivun
     */
    @RequestMapping(method = RequestMethod.GET)
    public String listFoods(Model model) {

        model.addAttribute("foods", foodRepo.findAll());
        model.addAttribute("restaurants", restaurantRepo.findAll());

        return "index";
    }

    @RequestMapping(value = "/{restaurantUri}", method = RequestMethod.GET)
    public String listFoodsByRestaurant(Model model, @PathVariable String restaurantUri) {

        model.addAttribute("foods", foodRepo.findByRestaurant(restaurantRepo.findByUri(restaurantUri)));
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
