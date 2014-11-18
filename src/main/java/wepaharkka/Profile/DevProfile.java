package wepaharkka.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import wepaharkka.Repository.FoodRepository;
import wepaharkka.Repository.RatingRepository;
import wepaharkka.Repository.RestaurantRepository;
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;
import wepaharkka.domain.Rating;
import wepaharkka.domain.Restaurant;

@Configuration
@Profile(value = {"dev", "default"})

public class DevProfile {
    
    @Autowired 
    private FoodRepository foodRepo;
    @Autowired 
    private RatingRepository ratingRepo;
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @Transactional
    @PostConstruct
    public void init() {
        Food food = new Food();
        food.setName("asd");
        food.setPrice(Price.Kevyesti);
        
        Food food2 = new Food();
        food.setName("Makarooni");
        food.setPrice(Price.Maukkaasti);
        
        Rating rating = new Rating();
        rating.setDate(new Date());
        rating.setRating(5);

        rating = ratingRepo.save(rating);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating);
        
        food.setRatings(ratings);
        
        foodRepo.save(food);
        
        Restaurant resta = new Restaurant();
        resta.setName("Exactum");
        restaurantRepo.save(resta);
        
        Restaurant resta2 = new Restaurant();
        resta2.setName("Chemicum");
        restaurantRepo.save(resta2);
        
        
        
    }
    
}
