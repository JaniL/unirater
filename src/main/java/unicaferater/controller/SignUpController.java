/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.controller;

/**
 *
 * @author chang
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
@Controller
public class SignUpController {
 
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String redirectRequestToRegistrationPage() {
        return "redirect:/user/register";
    }
}