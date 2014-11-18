package unicaferater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.context.annotation.Import;

import unicaferater.Profile.DevProfile;
import unicaferater.Profile.ProdProfile;

@Configuration
@ComponentScan
@EnableWebMvc
@EnableAutoConfiguration
@Import({DevProfile.class, ProdProfile.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
