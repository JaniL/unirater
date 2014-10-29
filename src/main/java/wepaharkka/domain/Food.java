/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.domain;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 */
public class Food extends AbstractPersistable<Long> {
    
    @Column(unique = true)
    private String name;
    private Price price;
    @OneToMany
    private Rating rating;

    public Food(String name, Price price, Rating rating) {
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public Food() {
        
    }
    
    
    public String getName() {
        return name;
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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
    
    

   
    
}
