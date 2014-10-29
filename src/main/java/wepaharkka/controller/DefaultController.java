package wepaharkka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jani on 29.10.2014.
 */

@Controller
@RequestMapping("*")
public class DefaultController {
    public String lol() {
        return "lol";
    }
}
