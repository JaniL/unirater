/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import unicaferater.Application;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.UserRepository;
import unicaferater.auth.Role;
import unicaferater.auth.SocialMediaService;
import unicaferater.auth.UserDetails;

import javax.transaction.Transactional;

/**
 *
 * @author Timo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class UserTest {
        @Autowired
    UserRepository userrepository;
        
    String password;
    String firstname;
    String email;
    String lastname;
     Role role;
    SocialMediaService SMS;
    User user;
    Long id;
    UserDetails userDetails;
    UserDetails.Builder builder2;
    
    @Before
    public void setUp() {
        user = new User();
        this.password = "123445566";
        this.firstname ="Foo";
        this.lastname ="Bar";
        this.email ="foo.bar@helsinki.fi";
        this.id = (long)12;
        this.role = Role.ROLE_USER;
        this.SMS =SocialMediaService.FACEBOOK;
        
        
        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPassword(password);
        user.setRole(role);
        user.setSignInProvider(SMS);
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void hello() {
         assertTrue(user.getSignInProvider() == SMS);
         builder2.setFirstName(firstname);
         builder2.setLastName(lastname);
         builder2.setPassword(password);
         builder2.setId(id);

         assertEquals(builder2.getFirstName(), firstname);
         assertEquals(builder2.getLastName(), lastname);
        assertEquals(builder2.getPassword(),password);
         assertEquals(builder2.getId(),id);


     }
}
