/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.domain.Food;

/**
 *
 * @author chang
 */
@Service
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepo;
    
    @Autowired
    private FoodRepository foodRepo;
    
    /**
     * Palauttaa parhaan arvion saaneen ruuan
     * @return
     */
    public Food getHighestRatedFood() {
        return getTopHighestRated(1).get(0);
    }
    
    /**
     * Hakee ruuat tietokannasta ja listaa ne paremmuusjärjestykseen
     * @param howMany
     * Kuinka monta tulosta halutaan
     * @return
     * lista ruuista.
     */
    public List<Food> getTopHighestRated(int howMany) {
        List<Food> foods = foodRepo.findAll();
        List<Food> highestRated = new ArrayList();
        
        Collections.sort(foods, new FoodComparator());
        
        for (int i = 0; i < howMany; i++) {
            highestRated.add(foods.get(i));
        }
        
        return highestRated;
        
    }
    
    
    
    
}
