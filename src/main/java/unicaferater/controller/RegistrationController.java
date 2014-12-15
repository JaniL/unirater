package unicaferater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;
 
import javax.validation.Valid;
import unicaferater.auth.RegistrationForm;
import unicaferater.config.SecurityUtil;
import unicaferater.domain.User;
import unicaferater.exception.DuplicateEmailException;
import unicaferater.service.UserService;
 
@Controller
@SessionAttributes("user")
public class RegistrationController {
 
    private UserService service;
 
    @Autowired
    public RegistrationController(UserService service) {
        this.service = service;
    }
 
    @RequestMapping(value ="/user/register", method = RequestMethod.GET)
    public String registerUserAccount(@Valid @ModelAttribute("user") RegistrationForm userAccountData,
                                      BindingResult result,
                                      WebRequest request) throws DuplicateEmailException {
        if (result.hasErrors()) {
            return "user/registrationForm";
        }
 
        User registered = createUserAccount(userAccountData, result);
 
        if (registered == null) {
            return "user/registrationForm";
        }
        SecurityUtil.logInUser(registered);
        ProviderSignInUtils.handlePostSignUp(registered.getEmail(), request);
 
        return "redirect:/";
    }
 
    private User createUserAccount(RegistrationForm userAccountData, BindingResult result) {
        User registered = null;
 
        try {
            registered = service.registerNewUserAccount(userAccountData);
        }
        catch (DuplicateEmailException ex) {
            addFieldError(
                    "user",
                    "email",
                    userAccountData.getEmail(),
                    "NotExist.user.email",
                    result);
        }
 
        return registered;
    }
 
    private void addFieldError(String objectName, String fieldName, String fieldValue,  String errorCode, BindingResult result) {
        FieldError error = new FieldError(
                objectName,
                fieldName,
                fieldValue,
                false,
                new String[]{errorCode},
                new Object[]{},
                errorCode
        );
 
        result.addError(error);
    }
}