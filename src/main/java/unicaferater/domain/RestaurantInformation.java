package unicaferater.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jani on 9.12.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantInformation {
    /**
     * Ravintolan osoite
     */
    private String address;

    //private Bistro bistro;
    // business

    /**
     * Kaupunki
     */
    private String city;

    /**
     * Ravintolan kuvaus suomeksi, englanniksi ja ruotsiksi
     */
    private String description;
    private String description_en;
    private String description_sv;

    /**
     * Ei tietoa.
     */
    private String ilta;

    // private String lounas;

    /**
     * Ravintolan nimi
     */
    private String restaurant;

    /**
     * Postinumero?
     */
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

    /**
     * Hakee ravintolan osoitteen
     * @return Palauttaa ravintolan osoitteen
     */
    public String getAddress() {
        return address;
    }

    /**
     * Hakee ravintolan kaupungin
     * @return Palauttaa ravintolan kaupungin
     */
    public String getCity() {
        return city;
    }

    /**
     * Hakee ravintolan suomenkielisen kuvauksen
     * @return Palauttaa ravintolan suomenkielisen kuvauksen
     */
    public String getDescription() {
        return description;
    }

    /**
     * Hakee ravintolan englanninkielisen kuvauksen
     * @return Palauttaa ravintolan englanninkielisen kuvauksen
     */
    public String getDescription_en() {
        return description_en;
    }

    /**
     * Hakee ravintolan ruotsinkielisen kuvauksen
     * @return Palauttaa ravintolan ruotsinkielisen kuvauksen
     */
    public String getDescription_sv() {
        return description_sv;
    }

    public String getIlta() {
        return ilta;
    }

    /**
     * Hakee ravintolan nimen
     * @return Palauttaa ravintolan nimen
     */
    public String getRestaurant() {
        return restaurant;
    }

    /**
     * Hakee ravintolan postinumeron
     * @return Palauttaa ravintolan postinumeron
     */
    public String getZip() {
        return zip;
    }

    /**
     * Asettaa ravintolan postinumeron
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Asettaa ravintolan kaupungin
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Asettaa ravintolan suomenkielisen kuvauksen
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Asettaa ravintolan englanninkielisen kuvauksen
     * @param description_en
     */
    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    /**
     * Asettaa ravintolan ruotsinkielisen kuvauksen
     * @param description_sv
     */
    public void setDescription_sv(String description_sv) {
        this.description_sv = description_sv;
    }

    public void setIlta(String ilta) {
        this.ilta = ilta;
    }

    /**
     * Asettaa ravintolan nimen
     * @param restaurant
     */
    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Asettaa ravintolan postinumeron
     * @param zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }
}
