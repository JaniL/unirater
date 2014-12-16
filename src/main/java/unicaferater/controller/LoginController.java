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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unicaferater.auth.UserDetails;

import java.nio.file.attribute.UserPrincipal;

@Controller
public class LoginController {
 
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(value="/testiasd",method=RequestMethod.GET)
    public String testi() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getCredentials());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getDetails());

        System.out.println((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        UserDetails customUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(customUser.getId());
        return "lol";
    }
}