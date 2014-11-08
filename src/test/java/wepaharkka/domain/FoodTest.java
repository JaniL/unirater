/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.domain;

import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wepaharkka.Application;
import wepaharkka.Repository.FoodRepository;

/**
 *
 * @author tixtixti
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class FoodTest {

    @Autowired
    FoodRepository foodrepository;

    Food food;
    ArrayList<Rating> arv;

    @Before
    @Transactional
    public void setUp() {
        this.food = new Food();
        food.setName("porkkanakakku");
        food.setPrice(Price.Kevyesti);

        Rating arvostelut = new Rating();
        arvostelut.setRating(5);
        arv = new ArrayList();
        arv.add(arvostelut);
        food.setRatings(arv);
    }

    @Test
    @Transactional
    public void getterSetterTest() {
        assertEquals(food.getPrice(), Price.Kevyesti);
        assertEquals(food.getName(), "porkkanakakku");
        assertEquals(food.getRatings().get(0).getRating(), 5);
    }

    @Test
    @Transactional
    public void canHaveManyRatings() {
        Rating testi1 = new Rating();
        Rating testi2 = new Rating();
        testi1.setRating(2);
        testi2.setRating(3);

        arv.add(testi2);
        arv.add(testi1);

        assertEquals(food.getRatings().size(), 3);
    }

    //Alla tietokanta testei eivät toimi vielä. Pitäis toimia sit kun kannat on korjattu

    @Test
    @Transactional
    public void databaseTest() {
        foodrepository.save(food);
        Food foo2 = foodrepository.findAll().get(0);
        assertEquals(food.getName(), foo2.getName());
    }

    @Test
    @Transactional
    public void repoFindByNameTest() {
        foodrepository.save(food);
        Food foo2 = foodrepository.findByName("porkkanakakku");
        assertEquals(food.getName(), foo2.getName());
    }
    
}
