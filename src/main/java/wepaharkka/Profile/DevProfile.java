package wepaharkka.Profile;

import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaharkka.Repository.FoodRepository;
import wepaharkka.Repository.RatingRepository;
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;
import wepaharkka.domain.Rating;

@Configuration
@Profile(value = {"dev", "default"})

public class DevProfile {
    
    @Autowired FoodRepository foodRepo;
    @Autowired RatingRepository ratingRepo;
    
    
    @PostConstruct
    public void init() {
        Food food = new Food();
        food.setName("asd");
        food.setPrice(Price.Kevyesti);
        
        Rating rating = new Rating();
        rating.setDate(new Date());
        rating.setRating(5);
        
        rating.setFood(food);
        food.setRating(rating);
        
        foodRepo.save(food);
        ratingRepo.save(rating);
    }
    
}
