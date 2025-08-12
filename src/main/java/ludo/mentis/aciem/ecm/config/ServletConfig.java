package ludo.mentis.aciem.ecm.config;

import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;


@Configuration
public class ServletConfig {

    @Bean
    ServletContextInitializer servletContextInitializer() {
        // prevent Thymeleaf from appending the session id to resources
        return servletContext -> servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
    }

}
