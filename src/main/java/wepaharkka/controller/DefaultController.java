package wepaharkka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jani on 29.10.2014.
 */
@Controller
@RequestMapping("/")
@ResponseBody
public class DefaultController {
    
    public String lol() {
        System.out.println("do this pls");
        return "lol oon paras";
    }
}
