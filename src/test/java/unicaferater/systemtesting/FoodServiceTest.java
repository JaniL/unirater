/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.systemtesting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unicaferater.Repository.RestaurantRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import unicaferater.Application;

/**
 *
 * @author tixtixti
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class FoodServiceTest {

    private final String API_URI = "/";
    @Autowired
    private RestaurantRepository restarepo;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        mockMvc.perform(get("/unicafe/saveall"));
    }

    @Test
    public void statusOk() throws Exception {
        mockMvc.perform(get(API_URI))
                .andExpect(status().isOk());
    }
    @Test
    public void RestaurantUriWorking() throws Exception {
        mockMvc.perform(get("/chemicum"))
                .andExpect(status().isOk())
                 .andExpect(model().attributeExists("menus"))
                 .andExpect(model().attributeExists("restaurants"))
                ;

    }

}
