package unicaferater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RatingRepository;

/**
 * Created by jani on 29.10.2014.
 */
@Controller
@RequestMapping("/index")
@ResponseBody
public class DefaultController {

    @Autowired
    FoodRepository foodRepo;
    @Autowired
    RatingRepository ratingRepo;

    @RequestMapping(method = RequestMethod.GET)
    public String lol() {
        System.out.println("do this pls");
        return "lol oon paras";

    }
}
