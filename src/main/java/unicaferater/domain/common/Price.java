package unicaferater.domain.common;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by jani on 14.12.2014.
 */

@Entity
public class Price extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String name;

    @OneToOne
    private PriceValue value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriceValue getValue() {
        return value;
    }

    public void setValue(PriceValue value) {
        this.value = value;
    }
}
