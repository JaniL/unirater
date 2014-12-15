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

    @ManyToOne
    private Restaurant restaurant;

    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToMany
    private List<Food> menu;

    public MenuOfTheDay() {
        menu = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<Food> getMenu() {
        return menu;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMenu(List<Food> menu) {
        this.menu = menu;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
