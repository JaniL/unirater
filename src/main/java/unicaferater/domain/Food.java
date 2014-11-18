/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 */
@Entity
public class Food extends AbstractPersistable<Long> {

//    @Column(unique = true)
    private String name;
    private Price price;
    @ManyToOne
    private Restaurant restaurant;

    @Temporal(TemporalType.DATE)
    private Date lastSeenOnMenu;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Rating> ratings;

    public Food() {
        this.ratings = new ArrayList();
    }

    public String getName() {
        return name;
    }

    /**
     * Laskee ruualle kaikkien arvosteluiden keskiarvon
     * @return
     * paluttaa double tyylisen vastauksen yhdellä desimaalilla
     */
    public double getAverage() {
        if (ratings == null) {
            return 0;
        }
        if (ratings.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return Math.round(sum / ratings.size());
    }

    public void setName(String name) {
        this.name = name;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Restaurant getRestaurant() {

        return restaurant;

    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Date getLastSeenOnMenu() {
        return lastSeenOnMenu;
    }

    public void setLastSeenOnMenu(Date lastSeenOnMenu) {
        this.lastSeenOnMenu = lastSeenOnMenu;
    }
    
    
    
    

}
