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
import unicaferater.Repository.MenuOfTheDayRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.MenuOfTheDay;
import unicaferater.domain.database.Restaurant;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class MenuOfTheDayTest {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    MenuOfTheDayRepository menuRepo;

    private Restaurant restaurant;
    private Food food;
    private List<Food> listOfFoods;
    private MenuOfTheDay menu;
    private Date date;

    @Before
    @Transactional
    public void setUp() {
        date = new Date();

        restaurant = new Restaurant();
        restaurant.setName("Päärakennus");
        restaurant.setUri("paarakennus");

        food = new Food();
        food.setName("Nakkikeitto");

        listOfFoods = new ArrayList<>();
        listOfFoods.add(food);


        menu = new MenuOfTheDay();
        menu.setRestaurant(restaurant);
        menu.setMenu(listOfFoods);
        menu.setDate(date);
    }

    @Test
    @Transactional
    public void getterSetterTest() {
        assertEquals(menu.getMenu(),listOfFoods);
        assertEquals(menu.getRestaurant(),restaurant);
        assertEquals(menu.getDate(),date);
    }

    @Test
    @Transactional
    public void databaseTest() {
        restaurant = restaurantRepository.save(restaurant);
        food = foodRepository.save(food);
        menuRepo.save(menu);
        MenuOfTheDay foo2 = menuRepo.findAll().get(0);
        assertEquals(menu.getMenu(), foo2.getMenu());
    }

    @Test
    @Transactional
    public void repoFindByRestaurantAndDateTest() {
        restaurant = restaurantRepository.save(restaurant);
        food = foodRepository.save(food);
        menuRepo.save(menu);
        MenuOfTheDay foo2 = menuRepo.findByRestaurantAndDate(restaurant,date);
        assertEquals(menu.getDate(), foo2.getDate());
    }

    @Test
    @Transactional
    public void repoFindByRestaurantTest() {
        restaurant = restaurantRepository.save(restaurant);
        food = foodRepository.save(food);
        menuRepo.save(menu);
        MenuOfTheDay foo2 = menuRepo.findByRestaurant(restaurant).get(0);
        assertEquals(menu.getDate(), foo2.getDate());
    }
}
