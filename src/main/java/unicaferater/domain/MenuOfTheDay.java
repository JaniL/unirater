package unicaferater.domain;

import java.util.List;

/**
 * Created by jani on 9.12.2014.
 */
public class MenuOfTheDay {
    private List<FoodDetails> data;

    private String date;
    private String date_en;
    private String date_sv;

    private String message;

    public List<FoodDetails> getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<FoodDetails> data) {
        this.data = data;
    }

    public String getDate_en() {
        return date_en;
    }

    public String getDate_sv() {
        return date_sv;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate_en(String date_en) {
        this.date_en = date_en;
    }

    public void setDate_sv(String date_sv) {
        this.date_sv = date_sv;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
