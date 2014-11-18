/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 */
@Entity
public class Rating extends AbstractPersistable<Long> {
    
    private int rating;

    
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date date;
    

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public Date getDate() {
        return date;
    }

    
    public void setDate(Date date) {
        this.date = date;
    }
    
    
}
