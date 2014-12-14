/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.domain.database;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author chang
 
 */
@Entity
public class Restaurant extends AbstractPersistable<Long> {
    
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String uri;
    
    //@OneToMany(fetch = FetchType.LAZY)
    //private List<Food> foods;

    @OneToMany(fetch = FetchType.LAZY)
    private List<MenuOfTheDay> menus;


    private int areacode;
    
    /*public Restaurant() {
        foods = new ArrayList();
    }*/

    public List<MenuOfTheDay> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuOfTheDay> menus) {
        this.menus = menus;
    }

    public int getAreacode() {
		return areacode;
	}
    
    public void setAreacode(int areacode) {
		this.areacode = areacode;
	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setUri(name);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = Normalizer.normalize(uri.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }

    /*public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }*/

    @Override
    public String toString() {
        return this.getName() + "(" + 0 + " foods)";
    }
}
