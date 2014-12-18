/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ActiveProfiles;
import unicaferater.Repository.RatingRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Rating;


/**
 *
 * @author tixtixti
 */
@ActiveProfiles("test")
@Transactional
public class RatingTest {

    Food food;
    Rating rating;
    unicaferater.domain.common.Price price;
      @Autowired
    RatingRepository ratingrepository;
      
    @Before
    public void setUp() {
        this.food = new Food();
        this.rating = new Rating();
        this.price = new unicaferater.domain.common.Price();
        price.setName("Kevyesti");

        food.setName("JaninSukat");
        food.setPrice(price);
        rating.setRating(1);
        Date date = new Date();
        rating.setDate(date);
        

    }
    
    @Test
    public void getSetTest() {
        Date date = new Date();
        assertEquals(rating.getRating(), 1);
        assertFalse(rating.getDate().before(date));
    }

}
