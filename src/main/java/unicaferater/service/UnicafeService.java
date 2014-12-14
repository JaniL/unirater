package unicaferater.service;

import org.springframework.stereotype.Service;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import unicaferater.Repository.FoodRepository;
import unicaferater.Repository.RestaurantRepository;
import unicaferater.domain.database.Food;
import unicaferater.domain.database.Price;
import unicaferater.domain.database.Restaurant;

/**
 *
 * @author Neodyymi
 */
@Service
public class UnicafeService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private ArrayList<String> titles;
    private ArrayList<String> descriptions;

    private Restaurant restaurant;
    private String week;

    public UnicafeService() {
        this.titles = new ArrayList();
        this.descriptions = new ArrayList();
        this.restaurant = new Restaurant();
        this.week = null;

    }

    public void fetchInfo(URL url) throws IOException, IllegalArgumentException, FeedException { //Gets info from url and parses it into food items
        this.titles = new ArrayList();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        this.descriptions = new ArrayList();
        this.titles = new ArrayList();
        this.restaurant = new Restaurant();
        this.restaurant.setName(feed.getTitle());
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
        return this.getRestaurant() + " - " + this.getWeek();
    }

    public String getRestaurant() {
        return this.restaurant.getName();
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.mm.yyyy", new Locale("fi", "FI"));
        Date foodDate;
        try {
            foodDate = dateFormat.parse(this.getTitleForDay(i));
        } catch (ParseException ex) {
            foodDate = new Date();
        }
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
            food.setLastSeenOnMenu(foodDate);
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

                ret += "*" + f.getName();
                if (!f.getPrice().equals(Price.Makeasti)) {
                    ret += " - " + f.getPrice();
                }
                ret += "<br>\n";
            }
            ret += "</p>\n";
        }

        return ret;
    }

    @Transactional
    public void storeFoodsForWeek() { //Stores fetched restaurant foods into repository
        Restaurant restaurantTmp;
        if (restaurantRepository.findByName(this.restaurant.getName()) != null) {
            restaurantTmp = restaurantRepository.findByName(this.restaurant.getName());
        } else {
            restaurantTmp = restaurantRepository.save(this.restaurant);
        }
        List<Food> foodsTmp;
        if (restaurantTmp.getFoods() == null) {
            foodsTmp = new ArrayList();
        } else {
            foodsTmp = restaurantTmp.getFoods();
        }
        for (int i = 0; i < this.getTitles().size(); i++) {

            this.getTitleForDay(i);
            for (Food f : this.getFoodsForDay(i)) {
                if (!f.getPrice().equals(Price.Makeasti) && foodRepository.findByNameAndRestaurant(f.getName(), restaurantTmp) == null) {
                    f.setRestaurant(restaurantTmp);
                    f = foodRepository.save(f);
                    if (foodsTmp.isEmpty() || !foodsTmp.contains(f)) {
                        foodsTmp.add(f);
                    }
                }
            }
        }
        restaurantTmp.setFoods(foodsTmp);
        restaurantRepository.save(restaurantTmp);
    }

    public String storeAll() throws MalformedURLException, IOException { //Fetches and stores all restaurants
        for (int i = 1; i <= 40; i++) {

            try {
                this.fetchInfo(new URL("http://www.hyyravintolat.fi/rss/fin/" + i + "/"));
            } catch (IllegalArgumentException ex) {
                continue; //Invalid document, skip.
            } catch (FeedException ex) {
                continue; //Invalid document, skip.
            }

            this.storeFoodsForWeek();
        }
        return "Saved all";
    }
}
