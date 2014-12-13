/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.Repository;

/**
 *
 * @author chang
 */
import org.springframework.data.jpa.repository.JpaRepository;
import unicaferater.domain.User;
 
public interface UserRepository extends JpaRepository<User, Long> {
 
    public User findByEmail(String email);
}
