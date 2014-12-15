/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import unicaferater.Application;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Rating;
import unicaferater.domain.database.Restaurant;

import javax.transaction.Transactional;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class RestaurantTest {

    @Autowired
    RestaurantRepository restaurantRepository;

    private Restaurant restaurant;

    @Before
    @Transactional
    public void setUp() {
        restaurant = new Restaurant();
        restaurant.setName("Päärakennus");
        restaurant.setUri("paarakennus");
    }

    @Test
    @Transactional
    public void getterSetterTest() {
        assertEquals(restaurant.getName(),"Päärakennus");
        assertEquals(restaurant.getUri(), "paarakennus");
    }

    @Test
    @Transactional
    public void databaseTest() {
        restaurantRepository.save(restaurant);
        Restaurant foo2 = restaurantRepository.findAll().get(0);
        assertEquals(restaurant.getName(), foo2.getName());
    }

    @Test
    @Transactional
    public void repoFindByNameTest() {
        restaurantRepository.save(restaurant);
        Restaurant foo2 = restaurantRepository.findByName("Päärakennus");
        assertEquals(restaurant.getName(), foo2.getName());
    }

    @Test
    @Transactional
    public void repoFindByUriTest() {
        restaurantRepository.save(restaurant);
        Restaurant foo2 = restaurantRepository.findByUri("paarakennus");
        assertEquals(restaurant.getUri(), foo2.getUri());
    }
}
