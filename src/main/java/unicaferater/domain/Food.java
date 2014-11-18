/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 */
@Entity
public class Food extends AbstractPersistable<Long> {
    
    @Column(unique = true)
    private String name;
    private Price price;
    @ManyToOne
    private Restaurant restaurant;
    
    @OneToMany(fetch=FetchType.LAZY)
    private List<Rating> ratings;
    
    
    public String getName() {
        return name;
    }

    /**
     * Laskee ruualle kaikkien arvosteluiden keskiarvon
     * @return
     * paluttaa double tyylisen vastauksen yhdellä desimaalilla
     */
    public double getAverage() {
        if (ratings == null) return 0;
        if (ratings.isEmpty()) return 0;
        
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

    
    
}
