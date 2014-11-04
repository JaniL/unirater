package wepaharkka;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Test
	public void contextLoads() {
             Food food  = new Food();
        food.setName("porkkanakakku");
        food.setPrice(Price.Kevyesti);
        
        assertEquals(food.getPrice(), Price.Kevyesti);
	}
        
      

}
