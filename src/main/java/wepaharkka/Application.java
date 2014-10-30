package wepaharkka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wepaharkka.Profile.DevProfile;
import wepaharkka.Profile.ProdProfile;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import({DevProfile.class, ProdProfile.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
