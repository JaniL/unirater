/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import wepaharkka.Repository.FoodRepository;
import wepaharkka.Repository.RestaurantRepository;
import wepaharkka.domain.Food;

/**
 *
 * @author chang
 */
@RequestMapping("/foods")
@Controller
public class FoodController {
    
    @Autowired
    private FoodRepository foodRepo;
    
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @RequestMapping(method = RequestMethod.GET)
    public String listFoods(Model model) {
        
        model.addAttribute("foods", foodRepo.findAll());
        System.out.println("asdkaskdjhasljkdhasljkdas");
        System.out.println(restaurantRepo.findAll().size());
        model.addAttribute("restaurants", restaurantRepo.findAll());
        
        String list = "";
        for (Food food : foodRepo.findAll()) {
            list += " " + food.getName();
        }
        
        return "index";
    }
    
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
