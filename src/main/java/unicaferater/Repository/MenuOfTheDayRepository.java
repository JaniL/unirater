/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unicaferater.domain.database.MenuOfTheDay;
import unicaferater.domain.database.Restaurant;

import java.util.Date;
import java.util.List;


public interface MenuOfTheDayRepository extends JpaRepository<MenuOfTheDay, Long> {

    /**
     * Hakee ruokalistoja ravintolan ja päivämäärän perusteella
     * @param restaurant Ravintola, jonka ruokalista halutaan
     * @param date Päivämäärä, jolta lista halutaan
     * @return Palauttaa ruokalistan pyydetystä ravintolasta määritetyllä päivämäärällä.
     */
    MenuOfTheDay findByRestaurantAndDate(Restaurant restaurant, Date date);

    /**
     * Hakee kaikki ruokalistat ravintolan perusteella
     * @param restaurant Ravintola, jonka ruokalistat halutaan
     * @return Palauttaa listan ravintolan ruokalistoista.
     */
    List<MenuOfTheDay> findByRestaurant(Restaurant restaurant);


}
