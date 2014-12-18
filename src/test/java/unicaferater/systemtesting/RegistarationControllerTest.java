/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.systemtesting;

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unicaferater.Application;
import unicaferater.Repository.RestaurantRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RegistarationControllerTest {

    private final String API_URI = "/user/register";


    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void isAPage() throws Exception{
        mockMvc.perform(get(API_URI))
                .andExpect(status().isOk());
    }
//    @Test
//    public void RegisterValidData() throws Exception {
//        mockMvc.perform(
//                post(API_URI)
//                .param("user-firstName", "John")
//                .param("user-lastName", "Doe")
//                .param("user-email", "john.doe@aol.com")
//                .param("user-password", "john1234")
//                .param("user-passwordVerification", "john1234")
//        )
//                .andExpect(redirectedUrl("/"));
////                .andExpect(flash().attribute("alertSuccess", "Account successfully created"));
//    }
}
