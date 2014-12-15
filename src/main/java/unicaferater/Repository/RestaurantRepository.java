/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import unicaferater.domain.database.Restaurant;

/**
 *
 * @author chang
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Hakee ravintolan tietokannasta nimen perusteella
     * @param name Ravintolan nimi
     * @return Palauttaa ravintolan
     */
    Restaurant findByName(String name);

    /**
     * Hakee ravintolan tietokannasta ravintolan SEO-ystävällisen nimen perusteella
     * @param uri Ravintolan SEO-ystävällinen nimi
     * @return Palauttaa ravintolan
     */
    Restaurant findByUri(String uri);
}
