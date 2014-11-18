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

import org.springframework.test.context.ActiveProfiles;


/**
 *
 * @author tixtixti
 */
@ActiveProfiles("test")
public class RatingTest {

    Food food;
    Rating rating;

    @Before
    @Transactional
    public void setUp() {
        this.food = new Food();
        this.rating = new Rating();
        
        food.setName("JaninSukat");
        food.setPrice(Price.Makeasti);
        rating.setRating(5);
        Date date = new Date();
        rating.setDate(date);
        

    }
    
    @Test
    @Transactional
    public void getSetTest() {
        Date date = new Date();
        assertEquals(rating.getRating(), 5);
        assertFalse(rating.getDate().before(date));
    }
}
