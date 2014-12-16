package unicaferater.domain.database;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;
import unicaferater.domain.User;

/**
 *
 * @author chang
 */
@Entity
public class Rating extends AbstractPersistable<Long> {

    private int rating;
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date date;
    
    private Long userId;
    
    @ManyToOne
    private Food food;

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
