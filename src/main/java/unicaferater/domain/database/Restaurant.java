/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain.database;

import java.text.Normalizer;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 
 */
@Entity
public class Restaurant extends AbstractPersistable<Long> {

    /**
     * Ravintolan nimi
     */
//    @Column(unique = true)
    private String name;

    /**
     * Ravintolan SEO-ystävällinen nimi
     * URL-osoitetta varten
     */
//    @Column(unique = true)
    private String uri;
    
    //@OneToMany(fetch = FetchType.LAZY)
    //private List<Food> foods;

    /**
     * Ravintolan ruokalistat
     */

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<MenuOfTheDay> menus;


    private int areacode;
    
    /*public Restaurant() {
        foods = new ArrayList();
    }*/

    /**
     * @return Palauttaa listan ruokalistoista
     */
    public List<MenuOfTheDay> getMenus() {
        return menus;
    }

    /**
     * Asettaa listan ruokalistoista
     * @param menus Lista ruokalistoista
     */
    public void setMenus(List<MenuOfTheDay> menus) {
        this.menus = menus;
    }

    public int getAreacode() {
		return areacode;
	}
    
    public void setAreacode(int areacode) {
		this.areacode = areacode;
	}

    /**
     * @return Palauttaa ravintolan nimen
     */
    public String getName() {
        return name;
    }

    /**
     * Asettaa ravintolan nimen.
     * Asettaa myös samalla ravintolan SEO-ystävällisen nimen.
     * @param name Ravintolan nimi
     */
    public void setName(String name) {
        this.name = name;
        setUri(name);
    }

    /**
     * @return Palauttaa ravintolan SEO-ystävällisen nimen
     */
    public String getUri() {
        return uri;
    }

    /**
     * Asettaa ravintolan SEO-ystävällisen nimen.
     * @param name Ravintolan nimi alkuperäisessä muodossa
     */
    public void setUri(String name) {
        this.uri = Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }

    /* public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    } */

    @Override
    public String toString() {
        return this.getName() + "(" + 0 + " foods)";
    }
}
