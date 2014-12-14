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

    Food findByName(String name);
    List<Food> findAllByName(String name);
    Food findByNameAndRestaurant(String name, Restaurant restaurant);
    List<Food> findByRestaurant(Restaurant restaurant);
}
