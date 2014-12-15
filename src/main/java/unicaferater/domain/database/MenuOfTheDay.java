package unicaferater.domain.database;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jani on 14.12.2014.
 */

@Entity
public class MenuOfTheDay extends AbstractPersistable<Long> {

    /**
     * Ravintola, jonka ruokalistaa määritetään
     */
    @ManyToOne
    private Restaurant restaurant;

    /**
     * Ruokalistan päivämäärä
     */
    @Temporal(TemporalType.DATE)
    private Date date;

    /**
     * Lista ruuista
     */
    @OneToMany
    private List<Food> menu;

    public MenuOfTheDay() {
        menu = new ArrayList<>();
    }

    /**
     * @return Palauttaa ruokalistan päivämäärän
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return Palauttaa ruokalistan ravintolan
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * @return Palauttaa listan ruuista
     */
    public List<Food> getMenu() {
        return menu;
    }

    /**
     * Asettaa ruokalistan päivämäärän
     * @param date Ruokalistan päivämäärä
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Asettaa listan ruuista
     * @param menu Lista ruuista
     */
    public void setMenu(List<Food> menu) {
        this.menu = menu;
    }

    /**
     * Asettaa ruokalistan ravintolan
     * @param restaurant Ravintola, jonka ruokalistaa määritetään
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
