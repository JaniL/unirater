package unicaferater.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
// @ComponentScan("my.root.package")
@EnableWebMvc
public class StaticConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println("jouaskdhsakjfsaehdfs");
		  registry.addResourceHandler("/static/**")
		    .addResourceLocations("classpath:/static/");
    }
}