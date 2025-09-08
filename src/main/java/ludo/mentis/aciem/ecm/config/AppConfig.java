package ludo.mentis.aciem.ecm.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;


@Configuration
@ComponentScan({"ludo.mentis.aciem.ecm", "ludo.mentis.aciem.commons.web"})
@ConfigurationPropertiesScan
public class AppConfig {

    @Bean
    UriBuilder uriBuilder() {
        return new DefaultUriBuilderFactory().builder();
    }
}
