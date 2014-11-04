/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaharkka.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaharkka.domain.Food;

/**
 *
 * @author chang
 */
public interface FoodRepository extends JpaRepository<Food, Long> {
    Food findByName(String name);
}
