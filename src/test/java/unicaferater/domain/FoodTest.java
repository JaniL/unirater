/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import unicaferater.Application;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.PriceRepository;
import unicaferater.Repository.RatingRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Rating;

/**
 *
 * @author tixtixti
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
@ActiveProfiles("test")
public class FoodTest {

    @Autowired
    FoodRepository foodrepository;
      @Autowired
    RatingRepository ratingrepository;
    @Autowired
    PriceRepository priceRepository;

    Food food;
    ArrayList<Rating> arv;

    unicaferater.domain.common.Price price;

    @Before
    //@Transactional
    public void setUp() {
        this.price = new unicaferater.domain.common.Price();
        price.setName("Kevyesti");
        price = priceRepository.save(price);

        this.food = new Food();
        food.setName("porkkanakakku");
        food.setPrice(price);

        Rating arvostelut = new Rating();
        arvostelut.setRating(2);
        arv = new ArrayList();
        arv.add(arvostelut);
        food.setRatings(arv);
    }

    @Test
    //@Transactional
    public void getterSetterTest() {
        assertEquals(food.getPrice(), price);
        assertEquals(food.getName(), "porkkanakakku");
        assertEquals(food.getRatingResult(), 2);
    }

    @Test
    //@Transactional
    public void canHaveManyRatings() {
        Rating testi1 = new Rating();
        Rating testi2 = new Rating();
        testi1.setRating(2);
        testi2.setRating(1);

        arv.add(testi2);
        arv.add(testi1);

        assertEquals(food.getRatings().size(), 3);
    }

    @Test
    //@Transactional
    public void databaseTest() {
        foodrepository.save(food);
        Food foo2 = foodrepository.findAll().get(0);
        assertEquals(food.getName(), foo2.getName());
    }

    @Test
    //@Transactional
    public void repoFindByNameTest() {
        foodrepository.save(food);
        Food foo2 = foodrepository.findByName("porkkanakakku");
        assertEquals(food.getName(), foo2.getName());
    }
    
    /*   @Test
    //@Transactional
    public void calculateTotalRight() {
        foodrepository.save(food);
        Food foo2 = foodrepository.findByName("porkkanakakku");
         Rating testi1 = new Rating();
        Rating testi2 = new Rating();
        testi1.setRating(2);
        testi2.setRating(1);

        arv.add(testi2);
        arv.add(testi1);
        foo2.getRatingResult();
        assertEquals(5, foo2.getTotal());
    } */
}
