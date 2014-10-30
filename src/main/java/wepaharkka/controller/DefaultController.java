package wepaharkka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import wepaharkka.Repository.FoodRepository;

/**
 * Created by jani on 29.10.2014.
 */

@Controller
@RequestMapping("*")
@ResponseBody
public class DefaultController {
    
    @Autowired FoodRepository foodRepo;
    
    @RequestMapping(method = RequestMethod.GET)
    public String lol() {
        
        
        return foodRepo.findByName("asd").getName();
    }
    
    
}
