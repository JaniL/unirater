/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.domain;

import java.util.Date;
import javax.persistence.ManyToOne;

/**
 *
 * @author chang
 */
public class Rating {
    
    private int arvostelu;
    @ManyToOne
    private Food ruoka;
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date date;

    public int getArvostelu() {
        return arvostelu;
    }

    public void setArvostelu(int arvostelu) {
        this.arvostelu = arvostelu;
    }

    public Food getRuoka() {
        return ruoka;
    }

    public void setRuoka(Food ruoka) {
        this.ruoka = ruoka;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
}