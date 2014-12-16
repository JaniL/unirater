/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import unicaferater.Repository.UserRepository;
import unicaferater.domain.User;
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

    @Autowired
    private UserRepository userRepo;

    /**
     * Listaa kaikki ruokalistat ja ravintolat omaan modeliin
     *
     * @param model
     * @return palauttaa indexi sivun
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listFoods(Model model) {
        model.addAttribute("menus", menuRepo.findAll());
        model.addAttribute("restaurants", restaurantRepo.findAll());

        return "index";
    }

    /**
     * Listaa pyydetyn ravintolan ruokalistat ja ravintolat
     *
     * @param model
     * @param restaurantUri
     * @return Palauttaa sivun, joka sisältää ravintolan ruokalistat sekä
     * ravintolat valikossa
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
     * Tekee POST pyynnön kyseisen Ruuan ID:hen Pitäisi ottaa Modelista Rating
     * arvio. Rating lisätään päivämäärä jollain arvostelu on annettu. lisätään
     * rating ruualle annettujen arvostelujen listaan. Tallenetaan kaikki.
     *
     * @param foodId
     * @param vote
     * @return HEP! Tällä hetkellä ohjaa meilahteen. Miten saataisiin ohjaamaan
     * sinne mistä on tullut? Tai jonnekin relevanttiin paikkan?
     */
    @RequestMapping(value = "rate/{foodId}/{vote}", method = RequestMethod.GET)
    public String postRating(@PathVariable Long foodId, @PathVariable int vote, HttpServletRequest request) {
        String ret = "/user/login";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = userRepo.findByEmail(auth.getName());
        if (u != null) {

            Food food = foodRepo.findOne(foodId);
            Rating rating = new Rating();
            if (vote == 0) {
                rating.setRating(-1);
            } else {
                rating.setRating(1);
            }

            long userId = u.getId();
//        long userId = 123;

            rating.setUserId(userId);
            Date date = new Date();
            rating.setDate(date);
            rating.setFood(food);

            List<Rating> ratingNewList = foodRepo.findOne(foodId).getRatings();
            Rating vanha = ratingRepo.findByUserIdAndFood(userId, food);
            if (vanha != null) {
                ratingNewList.remove(vanha);
            }
            ratingNewList.add(rating);
            ratingRepo.save(rating);
            food.setRatings(ratingNewList);
            food.getRatingResult(); // Että tallentaisi totaalin. Voi muuttaa sinne frontiinki.
            foodRepo.save(food);
            if (vanha != null) {
                ratingRepo.delete(vanha);
            }
            ret = (String) request.getHeader("Referer");
        }
        return "redirect:" + ret; // minne tän pitäs ohjata uudelleen. Voidaan laittaa palauttaa vaikka ratingin ravintolan kohdalle?
    }
}
