package unicaferater.domain;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
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
