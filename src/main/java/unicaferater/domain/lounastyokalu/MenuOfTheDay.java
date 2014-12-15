package unicaferater.domain.lounastyokalu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private Date date;
    private Date date_en;
    private Date date_sv;

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
    public Date getDate() {
        return date_en;
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
    public Date getDate_en() {
        return date_en;
    }

    /**
     * Hakee ruotsinkielisen päivämäärän
     * @return Palauttaa ruotsinkielisen päivämäärän
     */
    public Date getDate_sv() {
        return date_en;
    }

    /**
     * Asettaa suomenkielisen päivämäärän
     * @param date
     */
    public void setDate(String date) {

    }

    /**
     * Asettaa englanninkielisen päivämäärän
     * @param date_en
     */
    public void setDate_en(String date_en) {
        //this.date_en = date_en;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd.MM yyyy", Locale.ENGLISH);
        try {
            this.date_en = simpleDateFormat.parse(date_en + " " + Calendar.getInstance().get(Calendar.YEAR));
            // this.date_en.
        } catch (ParseException e) {
            System.out.println("ei onnistunut");
            System.out.println(e);
            this.date_en = new Date();
        }
    }

    /**
     * Asettaa ruotsinkielisen päivämäärän
     * @param date_sv
     */
    public void setDate_sv(String date_sv) {

    }

    /**
     * Asettaa viestin
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
