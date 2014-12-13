/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unicaferater.annotation;

/**
 *
 * @author chang
 */
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
 
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
 
@Target( { TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordsNotEmptyValidator.class)
@Documented
public @interface PasswordsNotEmpty {
 
    String message() default "PasswordsNotEmpty";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
    String triggerFieldName() default "";
 
    String passwordFieldName() default "";
 
    String passwordVerificationFieldName() default "";
}
