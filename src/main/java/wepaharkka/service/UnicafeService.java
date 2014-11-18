package wepaharkka.service;

import org.springframework.stereotype.Service;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import wepaharkka.Repository.FoodRepository;
import wepaharkka.domain.Food;
import wepaharkka.domain.Price;

/**
 *
 * @author Neodyymi
 */
@Service
public class UnicafeService {

    private FoodRepository foodRepository;

    private ArrayList<String> titles;
    private ArrayList<String> descriptions;

    private String restaurant;
    private String week;

    public UnicafeService() {
        this.titles = new ArrayList();
        this.descriptions = new ArrayList();
        this.restaurant = null;
        this.week = null;

    }

    public void fetchInfo(URL url) throws IOException, IllegalArgumentException, FeedException { //Gets info from url and parses it into food items
        this.titles = new ArrayList();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        this.descriptions = new ArrayList();
        this.titles = new ArrayList();
        this.restaurant = feed.getTitle();
        this.week = feed.getDescriptionEx().getValue();;
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
        return this.restaurant + " - " + this.week;
    }

    public String getRestaurant() {
        return this.restaurant;
    }

    public String getWeek() {
        return this.week;
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
        for (int j = 0; j < ret.size(); j++) {
            Food food = new Food();
            food.setName(ret.get(j));
            if (prices.size() == j) {
                break;
            }
            if (prices.get(j).contains("Maukkaasti")) {
                food.setPrice(Price.Maukkaasti);
            } else if (prices.get(j).contains("Edullisesti")) {
                food.setPrice(Price.Edullisesti);
            } else if (prices.get(j).contains("Kevyesti")) {
                food.setPrice(Price.Kevyesti);
            } else {
                food.setPrice(Price.Makeasti);
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

    public void storeFoodsForWeek() { //not ready
        for (int i = 0; i < this.getTitles().size(); i++) {

            this.getTitleForDay(i);
            for (Food f : this.getFoodsForDay(i)) {
                f.getName();
                f.getPrice();
            }

        }
    }

}
