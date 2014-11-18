package unicaferater;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import unicaferater.Application;
import unicaferater.domain.Food;
import unicaferater.domain.Price;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ApplicationTests {

	@Test
	public void contextLoads() {
             Food food  = new Food();
        food.setName("porkkanakakku");
        food.setPrice(Price.Kevyesti);
        
        assertEquals(food.getPrice(), Price.Kevyesti);
	}
        
      

}
