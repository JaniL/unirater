/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.config;

/**
 *
 * @author chang
 */
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import unicaferater.auth.UserDetails;
import unicaferater.domain.User;
 
public class SecurityUtil {
 
    public static void logInUser(User user) {
        UserDetails userDetails = UserDetails.getBuilder()
                .firstName(user.getFirstName())
                .id(user.getId())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .role(user.getRole())
                .socialSignInProvider(user.getSignInProvider())
                .username(user.getEmail())
                .build();
 
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
