/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.Profile;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author chang
 */
@Configuration
@Profile(value = {"test", "default"})
public class TestProfile {
    
    
    @PostConstruct
    public void init() { 
        System.out.println("loloonparas");
    }
}
