/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.systemtesting;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unicaferater.Application;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.MenuOfTheDayRepository;
import unicaferater.Repository.PriceRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Restaurant;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class unicafeController {

    private final String API_URI = "/unicafe/saveall";
    @Autowired
    private RestaurantRepository restarepo;
    @Autowired
    private FoodRepository foodrepo;
    @Autowired
    private MenuOfTheDayRepository menyrepo;
    @Autowired
    private PriceRepository pricerepo;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void statusOk() throws Exception {
        mockMvc.perform(get(API_URI))
                .andExpect(status().isOk());
    }

    @Test
    public void workingRepositories() throws Exception {
         mockMvc.perform(get(API_URI))
                .andExpect(status().isOk());  
        assertFalse(restarepo.findAll().isEmpty());
        assertFalse(foodrepo.findAll().isEmpty());
        assertFalse(pricerepo.findAll().isEmpty());
        assertFalse(menyrepo.findAll().isEmpty());
    }
}
