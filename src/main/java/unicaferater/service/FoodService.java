/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicaferater.Repository.FoodRepository;
import unicaferater.domain.Food;

/**
 *
 * @author Neodyymi
 */
@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public String listAll() {
        String ret = "<p>";
        for (Food f : foodRepository.findAll()) {
            ret += "*" + f.getName();
            ret += " - " + f.getPrice();
            if (f.getRestaurant() != null) {
                ret += " - " + f.getRestaurant().getName();
            }
            ret += " - " + f.getAverage();
            ret += "<br>\n";
        }
        ret += "</p>";

        return ret;
    }
    
    public String listByName(String name) {
        String ret = "<p>";
        for (Food f : foodRepository.findAllByName(name)) {
            ret += "*" + f.getName();
            ret += " - " + f.getPrice();
            if (f.getRestaurant() != null) {
                ret += " - " + f.getRestaurant().getName();
            }
            ret += " - " + f.getAverage();
            ret += "<br>\n";
        }
        ret += "</p>";

        return ret;
    }
}
