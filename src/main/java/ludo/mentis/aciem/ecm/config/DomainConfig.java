package ludo.mentis.aciem.ecm.config;

import java.util.Optional;

import org.hibernate.cfg.MappingSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@EntityScan("ludo.mentis.aciem.ecm.domain")
@EnableJpaRepositories("ludo.mentis.aciem.ecm.repos")
@EnableJpaAuditing//(auditorAwareRef="auditorProvider")
@EnableTransactionManagement
public class DomainConfig {

    @Bean
    HibernatePropertiesCustomizer jsonFormatMapper(final ObjectMapper objectMapper) {
        return properties -> properties.put(MappingSettings.JSON_FORMAT_MAPPER, new JacksonJsonFormatMapper(objectMapper));
    }

    /*
    @Bean
    AuditorAware<User> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(User.class::cast);
    }*/
}
