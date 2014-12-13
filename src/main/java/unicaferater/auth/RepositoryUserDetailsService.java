/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.auth;

/**
 *
 * @author chang
 */
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import unicaferater.Repository.UserRepository;
import unicaferater.domain.User;
 
public class RepositoryUserDetailsService implements UserDetailsService {
 
    private UserRepository repository;
 
    @Autowired
    public RepositoryUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username);
 
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
 
        UserDetails principal = UserDetails.getBuilder()
                .firstName(user.getFirstName())
                .id(user.getId())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .role(user.getRole())
                .socialSignInProvider(user.getSignInProvider())
                .username(user.getEmail())
                .build();
 
        return principal;
    }
}
