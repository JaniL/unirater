package unicaferater.Profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import unicaferater.service.LounastyokaluService;

import javax.annotation.PostConstruct;

@Configuration
@Profile("dev")

public class DevProfile {

    @Autowired
    private LounastyokaluService lounastyokaluService;

    @PostConstruct
    public void init() {
        System.out.println("Haetaan ruokalistat..");
        lounastyokaluService.saveAllToRepo(lounastyokaluService.fetchRestaurants());
        System.out.println("Ruokalistat haettu!");
    }
    
//    @Autowired 
//    private FoodRepository foodRepo;
//    @Autowired 
//    private RatingRepository ratingRepo;
//    @Autowired
//    private RestaurantRepository restaurantRepo;
//    
//    @Transactional
//    @PostConstruct
//    public void init() {
//        Food food = new Food();
//        food.setName("asd");

//        
//        Food food2 = new Food();
//        food2.setName("Makarooni");

//        
//        Rating rating2 = new Rating();
//        rating2.setDate(new Date());
//        rating2.setRating(3);
//        
//        rating2 = ratingRepo.save(rating2);
//        
//        List<Rating> ratings2 = new ArrayList<>();
//        ratings2.add(rating2);
//        
//        food2.setRatings(ratings2);
//        foodRepo.save(food2);
//        
//        Rating rating = new Rating();
//        rating.setDate(new Date());
//        rating.setRating(5);
//
//        rating = ratingRepo.save(rating);
//
//        List<Rating> ratings = new ArrayList<>();
//        ratings.add(rating);
//        
//        food.setRatings(ratings);
//        
//        foodRepo.save(food);
//        
//        Restaurant resta = new Restaurant();
//        resta.setName("Exactum");
//        restaurantRepo.save(resta);
//        
//        Restaurant resta2 = new Restaurant();
//        resta2.setName("Chemicum");
//        restaurantRepo.save(resta2);
//        
//        
//        
//    }
//    
}
