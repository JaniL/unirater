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
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;
import wepaharkka.domain.Rating;

@Configuration
@Profile(value = {"dev"})

public class DevProfile {
    
    @Autowired 
    private FoodRepository foodRepo;
    @Autowired 
    private RatingRepository ratingRepo;
    
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
        
        
        
    }
    
}
