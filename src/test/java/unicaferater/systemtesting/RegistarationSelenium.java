/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.systemtesting;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.SpringApplication;
import unicaferater.Application;

/**
 *
 * @author Timo
 */
public class RegistarationSelenium {

    private final String API_URI = "http://localhost:8080/user/register";

    private WebDriver driver;

    @Before
    public void setUp() {
        // käynnistää Spring-sovelluksen -- JSP-kirjastojen lataaminen
        // tällä hetkellä kuitenkin buginen, joten ajetaan testit 
        // manuaalisesti käynnistettyyn sovellukseen

        SpringApplication.run(Application.class);
        this.driver = new HtmlUnitDriver();
    }

//    @Test
//    public void seleniumTest() {
//
//        driver.get(API_URI);
//
//        
//        WebElement element = driver.findElement(By.id("user-firstName"));
//        element.sendKeys("Bob");
////        element.submit();
//        
//        WebElement element2 = driver.findElement(By.id("user-lastName"));
//        element2.sendKeys("Brown");
////        element2.submit();
//        
//        WebElement element3 = driver.findElement(By.id("user-email"));
//        element3.sendKeys("BobBrown@cs.helsinki.gov");
////        element3.submit();
//        
//        
//        WebElement element4 = driver.findElement(By.id("form-group-password"));
//        element4.sendKeys("mitentaatoimiiiiiii");
////        element4.submit();
//        
//        WebElement element5 = driver.findElement(By.id("user-passwordVerification"));
//        element5.sendKeys("mitentaatoimiiiiiii");
////        element5.submit();
//        
//        driver.findElement(By.className("btn btn-default")).click();
//        
//        
//        assertEquals(driver.getTitle(), "Unicafe Rater");
//
//    }
}
