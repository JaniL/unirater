/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import unicaferater.domain.Restaurant;

/**
 *
 * @author chang
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findByName(String name);
}
