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


public interface MenuOfTheDayRepository extends JpaRepository<MenuOfTheDay, Long> {
    MenuOfTheDay findByRestaurantAndDate(Restaurant restaurant, Date date);

}
