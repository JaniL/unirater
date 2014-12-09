package unicaferater.domain;

import java.util.List;

/**
 * Created by jani on 9.12.2014.
 */
public class MenuOfTheDay {
    /**
     * Listan ruuat
     */
    private List<FoodDetails> data;

    /**
     * Listan päivämäärä suomeksi, englanniksi ja ruotsiksi.
     */
    private String date;
    private String date_en;
    private String date_sv;

    /**
     * Viesti. Ei tietoa mistä tässä on kysymys.
     */
    private String message;

    /**
     * Hakee listan ruuista
     * @return Palauttaa listan ruuista
     */
    public List<FoodDetails> getData() {
        return data;
    }

    /**
     * Hakee listan suomenkielisen päivämäärän
     * @return Palauttaa listan suomenkielisen päivämäärän
     */
    public String getDate() {
        return date;
    }

    /**
     * Hakee viestin
     * @return Palauttaa viestin
     */
    public String getMessage() {
        return message;
    }

    /**
     * Asettaa ruokalistan
     * @param data
     */
    public void setData(List<FoodDetails> data) {
        this.data = data;
    }

    /**
     * Hakee englanninkielisen päivämäärän
     * @return Palauttaa englanninkielisen päivämäärän
     */
    public String getDate_en() {
        return date_en;
    }

    /**
     * Hakee ruotsinkielisen päivämäärän
     * @return Palauttaa ruotsinkielisen päivämäärän
     */
    public String getDate_sv() {
        return date_sv;
    }

    /**
     * Asettaa suomenkielisen päivämäärän
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Asettaa englanninkielisen päivämäärän
     * @param date_en
     */
    public void setDate_en(String date_en) {
        this.date_en = date_en;
    }

    /**
     * Asettaa ruotsinkielisen päivämäärän
     * @param date_sv
     */
    public void setDate_sv(String date_sv) {
        this.date_sv = date_sv;
    }

    /**
     * Asettaa viestin
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
