package wepaharkka.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;

/**
 *
 * @author Neodyymi
 */
@Service
public class UnicafeService {

    private RestTemplate restTemplate;
    private SyndFeedInput input;
    private SyndFeed feed;
    private URL url;
    private ArrayList<String> titles;
    private ArrayList<String> descriptions;

    private String title;

    public UnicafeService() {
        this.restTemplate = new RestTemplate();
        String urli = "http://www.hyyravintolat.fi/rss/fin/";
        this.url = null;
        this.input = new SyndFeedInput();
        this.feed = null;
        this.titles = new ArrayList();
        this.descriptions = new ArrayList();
        this.title = null;

    }

    public void fetchInfo(URL url) throws IOException, IllegalArgumentException, FeedException {
        this.url = url;
        this.titles = new ArrayList();
        this.feed = input.build(new XmlReader(url));
        this.title = this.feed.getTitle() + "::" + feed.getDescriptionEx().getValue();;
        for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {

            this.titles.add(entry.getTitle());
            this.descriptions.add(entry.getDescription().getValue());
        }
    }

    public ArrayList<String> getTitles() {//Date info
        return this.titles;
    }

    public ArrayList<String> getDescriptions() {//List of raw food data food descriptions
        return this.descriptions;
    }

    public String getTitle() {//Name of restaurant
        return this.title;
    }
    
    public String getTitleForDay(int i) {//Date info for specific day 0=monday, 1=tuesday ... 6=sunday
        return this.getTitles().get(i);
    }

    public ArrayList<Food> getFoodsForDay(int i) {//List of foods for day 0=monday, 1=tuesday ... 6=sunday
        ArrayList<String> ret = new ArrayList();
        String[] lines = this.getDescriptions().get(i).split("<span class=\"meal\">");
        ArrayList<Food> foods = new ArrayList();
        ArrayList<String> prices = new ArrayList();
        for (String l : lines) {
            if (l.contains("<span class=\"priceinfo\">")) {
                String h = new String();
                
                prices.add(l.substring(l.indexOf("<span class=\"priceinfo\">"), l.indexOf("</span>", l.indexOf("<span class=\"priceinfo\">"))) + h);
                
            }
        }
        for (String l : lines) {
            if (l.contains("</span>")) {
                String h = new String();
                ret.add(l.substring(0, l.indexOf("</span>")));
                
            }
        }
        for(int j = 0;j < ret.size();j++) {
            Food food = new Food();
            food.setName(ret.get(j));
            if(prices.get(j).contains("Maukkaasti")) {
                food.setPrice(Price.Maukkaasti);
            } else if(prices.get(j).contains("Edullisesti")) {
                food.setPrice(Price.Edullisesti);
            }else if(prices.get(j).contains("Kevyesti")) {
                food.setPrice(Price.Kevyesti);
            }
            foods.add(food);
        }
        return foods;
    }
    public String getAll() {//Silly toString type thing

        String ret = new String();
        ret += "<br>\n";
        ret += this.getTitle();
        ret += "<br>\n";
        for (int i = 0; i < this.getTitles().size(); i++) {

            ret += "<p>" + this.getTitleForDay(i) + ":<br>\n";
            for (Food f : this.getFoodsForDay(i)) {
                ret += "*" + f.getName() + " - " + f.getPrice() + "<br>\n";
            }
            ret += "</p>\n";
        }

        return ret;
    }

}
