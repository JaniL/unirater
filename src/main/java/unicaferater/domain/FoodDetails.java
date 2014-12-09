package unicaferater.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by jani on 9.12.2014.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodDetails {
    private String ingredients;
    // private List<List<String>> meta;

    private String name;
    private String name_en;
    private String name_sv;

    private String nutrition;

    // private HashMap<String, > price


    public String getIngredients() {
        return ingredients;
    }

    /*
    public List<List<String>> getMeta() {
        return meta;
    }
    */

    public String getName() {
        return name;
    }

    public String getNutrition() {
        return nutrition;
    }

    public String getName_en() {
        return name_en;
    }

    public String getName_sv() {
        return name_sv;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    /*
    public void setMeta(List<List<String>> meta) {
        this.meta = meta;
    }
    */

    public void setName(String name) {
        this.name = name;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
}
