/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

/**
 *
 * @author chang
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicaferater.Repository.UserRepository;
import unicaferater.auth.RegistrationForm;
import unicaferater.domain.User;
import unicaferater.exception.DuplicateEmailException;
 
@Service
public class RepositoryUserService implements UserService {
 
    private PasswordEncoder passwordEncoder;
 
    private UserRepository repository;
 
    @Autowired
    public RepositoryUserService(PasswordEncoder passwordEncoder, UserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }
 
    @Transactional
    @Override
    public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException {
        if (emailExist(userAccountData.getEmail())) {
            throw new DuplicateEmailException("The email address: " + userAccountData.getEmail() + " is already in use.");
        }
 
        String encodedPassword = encodePassword(userAccountData);
 
        User.Builder user = User.getBuilder()
                .email(userAccountData.getEmail())
                .firstName(userAccountData.getFirstName())
                .lastName(userAccountData.getLastName())
                .password(encodedPassword);
 
        if (userAccountData.isSocialSignIn()) {
            user.signInProvider(userAccountData.getSignInProvider());
        }
 
        User registered = user.build();
 
        return repository.save(registered);
    }
 
    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);
 
        if (user != null) {
            return true;
        }
 
        return false;
    }
 
    private String encodePassword(RegistrationForm dto) {
        String encodedPassword = null;
 
        if (dto.isNormalRegistration()) {
            encodedPassword = passwordEncoder.encode(dto.getPassword());
        }
 
        return encodedPassword;
    }
}