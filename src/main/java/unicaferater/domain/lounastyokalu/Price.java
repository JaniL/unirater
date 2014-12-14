package unicaferater.domain.lounastyokalu;

/**
 * Created by jani on 14.12.2014.
 */
public class Price {
    private String name;
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
