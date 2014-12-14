/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import unicaferater.domain.Food;

import unicaferater.domain.Rating;

/**
 *
 * @author chang
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {

}