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
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
 
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
@Target( { TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordsNotEqualValidator.class)
@Documented
public @interface PasswordsNotEqual {
 
    String message() default "PasswordsNotEqual";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
    String passwordFieldName() default "";
 
    String passwordVerificationFieldName() default "";
}