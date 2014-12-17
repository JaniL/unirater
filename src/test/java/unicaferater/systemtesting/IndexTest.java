/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.systemtesting;

import com.google.inject.spi.Message;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unicaferater.Application;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Restaurant;

/**
 *
 * @author Timo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class IndexTest {

    private final String API_URI = "/";
    @Autowired
    private RestaurantRepository restarepo;
    
    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    public IndexTest() {
    }

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
    public void responseTypeHasModel() throws Exception {
        mockMvc.perform(get(API_URI))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("restaurants"));
    }
    
    @Test
    public void modelHasAttribute() throws Exception {
        MvcResult res = mockMvc.perform(get(API_URI))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("restaurants"))
                .andReturn();

        List<Restaurant> restaurants = (List) res.getModelAndView().getModel().get("restaurants");
        assertTrue(restaurants.isEmpty());
    }
}
