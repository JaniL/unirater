package unicaferater.controller;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import unicaferater.service.UnicafeService;

/**
 *
 * @author Neodyymi
 */
@Controller
@RequestMapping("/unicafe")
@ResponseBody
public class UnicafeController {

    @Autowired
    private UnicafeService unicafeService;

    @RequestMapping(method = RequestMethod.GET)
    public String list() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        URL url = new URL("http://www.hyyravintolat.fi/rss/fin/11/");
        unicafeService.fetchInfo(url);

        String ret = unicafeService.getAll();
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String listOne(@PathVariable int id) throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        URL url = new URL("http://www.hyyravintolat.fi/rss/fin/" + id + "/");
        unicafeService.fetchInfo(url);

        String ret = unicafeService.getAll();
        return ret;
    }
    
    @RequestMapping(value = "saved", method = RequestMethod.GET)
    public String fromRepo() {
        
        String ret = unicafeService.listFoodsFromRepository();
        return ret;
    }
    
    @RequestMapping(value = "save/{id}", method = RequestMethod.GET)
    public String toRepo(@PathVariable int id) throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        URL url = new URL("http://www.hyyravintolat.fi/rss/fin/" + id + "/");
        unicafeService.fetchInfo(url);
        unicafeService.storeFoodsForWeek();
        
        return ("redirect:/unicafe/saved/");
    }
}
