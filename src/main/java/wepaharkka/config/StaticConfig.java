package wepaharkka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class StaticConfig {
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		  registry.addResourceHandler("/static/**")
		    .addResourceLocations("classpath:/static/");
		}
}
