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


    /**
     * Hakee ruuassa käytettävät aineosat
     * @return Palauttaa ruuassa käytettävät aineosat
     */
    public String getIngredients() {
        return ingredients;
    }

    /*
    public List<List<String>> getMeta() {
        return meta;
    }
    */

    /**
     * Hakee ruuan suomenkielisen nimen
     * @return Palauttaa ruuan suomenkielisen nimen
     */
    public String getName() {
        return name;
    }

    /**
     * Hakee ruuan ravintoarvot
     * @return Palauttaa ruuan ravintoarvot
     */
    public String getNutrition() {
        return nutrition;
    }

    /**
     * Hakee ruuan englanninkielisen nimen
     * @return Palauttaa ruuan englanninkielisen nimen
     */
    public String getName_en() {
        return name_en;
    }

    /**
     * Hakee ruuan ruotsinkielisen nimen
     * @return Palauttaa ruuan ruotsinkielisen nimen
     */
    public String getName_sv() {
        return name_sv;
    }

    /**
     * Asettaa ruuassa käytettävät aineosat
     * @param ingredients
     */
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    /*
    public void setMeta(List<List<String>> meta) {
        this.meta = meta;
    }
    */

    /**
     * Asettaa ruualle suomenkielisen nimen
     * @param name Ruuan suomenkielinen nimi
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Asettaa ruualle ruotsinkielisen nimen
     * @param name_en Ruuan englanninkielinen nimi
     */
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    /**
     * Asettaa ruualle englanninkielisen nimen
     * @param name_sv Ruuan englanninkielinen nimi
     */
    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    /**
     * Asettaa ruualle ravintoarvot
     * @param nutrition
     */
    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
}
