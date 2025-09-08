package ludo.mentis.aciem.ecm.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"ludo.mentis.aciem.ecm", "ludo.mentis.aciem.commons.web"})
@ConfigurationPropertiesScan
public class AppConfig {
}
