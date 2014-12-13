/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.service;

import unicaferater.auth.RegistrationForm;
import unicaferater.domain.User;
import unicaferater.exception.DuplicateEmailException;

/**
 *
 * @author chang
 */
public interface UserService {
 
    public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException;
}