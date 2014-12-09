package unicaferater.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jani on 9.12.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantInformation {
    private String address;
    //private Bistro bistro;
    // business

    private String city;
    private String description;
    private String description_en;
    private String description_sv;
    private String ilta;
    // private String lounas;

    private String restaurant;
    private String zip;

    /* public Bistro getBistro() {
        return bistro;
    }

    public void setBistro(Bistro bistro) {
        this.bistro = bistro;
    } */

    /*
    public String getLounas() {
        return lounas;
    }

    public void setLounas(String lounas) {
        this.lounas = lounas;
    }
    */

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getDescription_en() {
        return description_en;
    }

    public String getDescription_sv() {
        return description_sv;
    }

    public String getIlta() {
        return ilta;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getZip() {
        return zip;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public void setDescription_sv(String description_sv) {
        this.description_sv = description_sv;
    }

    public void setIlta(String ilta) {
        this.ilta = ilta;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
