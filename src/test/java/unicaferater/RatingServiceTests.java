/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Rating;
import unicaferater.service.RatingService;

/**
 *
 * @author chang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class RatingServiceTests {
    
    @Autowired
    private FoodRepository foodRepo;
    
    @Autowired
    private RatingRepository ratingRepo;
    
    @Autowired
    private RatingService ratingService;
    
    
    @Before
    public void setup() {
        Food food = new Food();
        food.setName("Mansikkakakku");
        List<Rating> ratings = new ArrayList();
        Rating rating = new Rating();
        rating.setRating(2);
        rating = ratingRepo.save(rating);
        ratings.add(rating);
        food.setRatings(ratings);
        
        
        foodRepo.save(food);
        
        Food food2 = new Food();
        food2.setName("Makkarapiirakka");
        List<Rating> ratings2 = new ArrayList();
        Rating rating2 = new Rating();
        rating2.setRating(2);
        rating2 = ratingRepo.save(rating2);
        ratings2.add(rating2);
        food2.setRatings(ratings2);
        
        
        foodRepo.save(food2);
        
        Food food3 = new Food();
        food3.setName("Kanelipulla");
        List<Rating> ratings3 = new ArrayList();
        Rating rating3 = new Rating();
        rating3.setRating(4);
        rating3 = ratingRepo.save(rating3);
        ratings3.add(rating3);
        food3.setRatings(ratings3);
        
        
        foodRepo.save(food3);
    }
    
    
    @Test
    @Transactional
    public void getAverageTest() {
        Food food = new Food();
        List<Rating> ratings = new ArrayList();
        Rating rating = new Rating();
        rating.setRating(5);
        Rating rating2 = new Rating();
        rating2.setRating(2);
        rating = ratingRepo.save(rating);
        rating2 = ratingRepo.save(rating2);
        ratings.add(rating);
        ratings.add(rating2);
        food.setRatings(ratings);
        foodRepo.save(food);
        
        assertEquals(7, food.getRatingResult());
    }
    
    @Test
    @Transactional
    public void HighestRatingTest() {
        
        assertEquals(4, ratingService.getHighestRatedFood().getRatingResult(), 0.001);
    }
    
    @Test
    @Transactional
    public void topRatedReturnsCorrectAmount() {
        assertEquals(3, ratingService.getTopHighestRated(3).size());
    }
    
    @Test
    @Transactional
    public void getThreeTopRated() {
        List<Food> theBest = ratingService.getTopHighestRated(3);
        assertEquals(4, theBest.get(0).getRatingResult(), 0.001);
        assertEquals(2, theBest.get(1).getRatingResult(), 0.001);
        assertEquals(2, theBest.get(2).getRatingResult(), 0.001);
        
    }
    
}
