/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

import java.util.Comparator;

import unicaferater.domain.database.Food;

/**
 *
 * @author chang
 */
public class FoodComparator implements Comparator<Food> {

    @Override
    public int compare(Food first, Food second) {
        return (int) second.getRatingResult() - (int) first.getRatingResult();
    }

    
    
}
