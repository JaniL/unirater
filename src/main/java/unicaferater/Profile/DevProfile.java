package unicaferater.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.Food;
import unicaferater.domain.Price;
import unicaferater.domain.Rating;
import unicaferater.domain.Restaurant;

@Configuration
@Profile(value = {"dev"})

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
        
        Rating rating = new Rating();
        rating.setDate(new Date());
        rating.setRating(5);

        rating = ratingRepo.save(rating);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating);
        
        rating.setFood(food);
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
