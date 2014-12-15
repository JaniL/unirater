/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import unicaferater.domain.database.Food;
import unicaferater.domain.database.Restaurant;

/**
 *
 * @author chang
 */
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * Hakee ruuan nimen perusteella
     * @param name Ruuan nimi, jonka perusteella ruoka haetaan tietokannasta
     * @return Palauttaa ruuan
     */
    Food findByName(String name);

    /**
     * Hakee tietokannasta ruuat, jolla on name-parametrissä määritelty nimi
     * @param name Ruuan nimi, jonka perusteella ruokia etsitään
     * @return Palauttaa listan ruuista, jotka täsmää kriteeriin
     */
    List<Food> findAllByName(String name);

    // Food findByNameAndRestaurant(String name, Restaurant restaurant);
    // List<Food> findByRestaurant(Restaurant restaurant);
}
