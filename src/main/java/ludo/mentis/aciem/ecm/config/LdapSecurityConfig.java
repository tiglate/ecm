package ludo.mentis.aciem.ecm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class LdapSecurityConfig {

    @Bean
    LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource, @Value("${LDAP_BASE}") String ldapBase) {
        var groupSearchBase = "ou=Groups";
        var authorities = new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
        authorities.setGroupSearchFilter("(member=uid={1}," + ldapBase + ")");
        return authorities;
    }

    @Bean
    AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource,
                                                LdapAuthoritiesPopulator authorities) {
        var factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchBase("ou=People");
        factory.setUserSearchFilter("(uid={0})");
        factory.setLdapAuthoritiesPopulator(authorities);
        return factory.createAuthenticationManager();
    }

    @Bean
    SecurityFilterChain formsSecurityConfigFilterChain(final HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(csrf ->
                        csrf.ignoringRequestMatchers("/actuator/**", "/oauth/**", "/api/**"))
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/login",
                                        "/static/**",
                                        "/webjars/**",
                                        "/oauth/**",
                                        "/actuator/**",
                                        "/favicon.ico",
                                        "/api/**",
                                        "/error/**").permitAll()
                                .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?loginError=true"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logoutSuccess=true")
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login?loginRequired=true")))
                .build();
    }
}
