package unicaferater;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import unicaferater.domain.database.Food;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ApplicationTests {

	@Test
	public void contextLoads() {
             Food food  = new Food();
        unicaferater.domain.common.Price price = new unicaferater.domain.common.Price();
        price.setName("Kevyesti");
        food.setName("porkkanakakku");
        food.setPrice(price);
        
        assertEquals(food.getPrice().getName(), "Kevyesti");
	}
        
      

}
