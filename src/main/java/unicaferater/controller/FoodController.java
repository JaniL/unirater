/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.Food;

/**
 *
 * @author chang
 */
@RequestMapping("*")
@Controller
public class FoodController {

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    /**
     * Listaa kaikki ruuat ja ravintolat omaan modeliin
     * @param model
     * @return
     * palauttaa indexi sivun
     */
    @RequestMapping(method = RequestMethod.GET)
    public String listFoods(Model model) {

        model.addAttribute("foods", foodRepo.findAll());
        System.out.println(restaurantRepo.findAll().size());
        model.addAttribute("restaurants", restaurantRepo.findAll());

        return "index";
    }

    /**
     * En tiedä mitä tekee
     * voisi varmaan tehdä jotain
     * tai olla jossain muualla.
     * @param food
     * @param bindRes
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String postFood(@ModelAttribute Food food, BindingResult bindRes) {
        if (food.getName() != null && food.getPrice() != null) {
            if (!bindRes.hasErrors()) {
                foodRepo.save(food);
            }
        }

        return "redirect:/foods";
    }

}
