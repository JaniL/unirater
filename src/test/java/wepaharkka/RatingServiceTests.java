/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wepaharkka.Repository.FoodRepository;
import wepaharkka.Repository.RatingRepository;
import wepaharkka.domain.Food;
import wepaharkka.domain.Rating;
import wepaharkka.service.RatingService;

/**
 *
 * @author chang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RatingServiceTests {
    
    @Autowired
    private FoodRepository foodRepo;
    
    @Autowired
    private RatingRepository ratingRepo;
    
    @Autowired
    private RatingService ratingService;
    
    
    @Test
    @Transactional
    public void getAverageTest() {
        Food food = new Food();
        List<Rating> ratings = new ArrayList();
        Rating rating = new Rating();
        rating.setRating(5);
        Rating rating2 = new Rating();
        rating2.setRating(3);
        rating = ratingRepo.save(rating);
        rating2 = ratingRepo.save(rating2);
        ratings.add(rating);
        ratings.add(rating2);
        food.setRatings(ratings);
        foodRepo.save(food);
        
        assertEquals(4.0, food.getAverage(), 0.001);
    }
    
//    @Test
//    @Transactional
//    public void HighestRatingTest() {
//        Food food = new Food();
//        List<Rating> ratings = new ArrayList();
//        Rating rating = new Rating();
//        rating.setRating(5);
//        rating = ratingRepo.save(rating);
//        ratings.add(rating);
//        food.setRatings(ratings);
//        
//        
//        foodRepo.save(food);
//        
//        Food food2 = new Food();
//        List<Rating> ratings2 = new ArrayList();
//        Rating rating2 = new Rating();
//        rating2.setRating(3);
//        rating2 = ratingRepo.save(rating2);
//        ratings2.add(rating2);
//        food2.setRatings(ratings2);
//        
//        
//        foodRepo.save(food2);
//        
//        Food food3 = new Food();
//        List<Rating> ratings3 = new ArrayList();
//        Rating rating3 = new Rating();
//        rating3.setRating(4);
//        rating3 = ratingRepo.save(rating3);
//        ratings3.add(rating3);
//        food3.setRatings(ratings3);
//        
//        
//        foodRepo.save(food3);
//        
//        assertEquals(food, ratingService.getHighestRatedFood());
//    }
    
}
