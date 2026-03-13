package unicaferater.domain.lounastyokalu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jani on 9.12.2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
        if (date != null) {
            return date;
        }
        if (date_en != null) {
            return date_en;
        }
        return date_sv;
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
        return date_sv;
    }

    /**
     * Asettaa suomenkielisen päivämäärän
     * @param date
     */
    public void setDate(String date) {
        this.date = parseDate(date);
    }

    /**
     * Asettaa englanninkielisen päivämäärän
     * @param date_en
     */
    public void setDate_en(String date_en) {
        this.date_en = parseDate(date_en);
    }

    /**
     * Asettaa ruotsinkielisen päivämäärän
     * @param date_sv
     */
    public void setDate_sv(String date_sv) {
        this.date_sv = parseDate(date_sv);
    }

    /**
     * Asettaa viestin
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.");
        Matcher matcher = pattern.matcher(dateString);
        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTime();
        }

        String[] patterns = {"EEE dd.MM.", "EEE dd.MM", "dd.MM."};
        Locale[] locales = {Locale.ENGLISH, new Locale("fi", "FI"), new Locale("sv", "SE")};

        for (Locale locale : locales) {
            for (String patternString : patterns) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternString, locale);
                simpleDateFormat.setLenient(true);
                try {
                    Date parsedDate = simpleDateFormat.parse(dateString);
                    Calendar calendar = Calendar.getInstance();
                    Calendar parsedCalendar = Calendar.getInstance();
                    parsedCalendar.setTime(parsedDate);
                    calendar.set(Calendar.MONTH, parsedCalendar.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, parsedCalendar.get(Calendar.DAY_OF_MONTH));
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    return calendar.getTime();
                } catch (ParseException ignored) {
                }
            }
        }

        return null;
    }
}
