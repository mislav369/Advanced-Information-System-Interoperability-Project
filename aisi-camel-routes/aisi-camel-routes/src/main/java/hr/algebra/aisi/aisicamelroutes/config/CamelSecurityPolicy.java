package hr.algebra.aisi.aisicamelroutes.config;

import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

@Configuration
public class CamelSecurityPolicy {

    @Bean("adminPolicy")
    public SpringSecurityAuthorizationPolicy adminPolicy(AuthenticationManager authenticationManager) {
        SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();
        policy.setAuthenticationManager(authenticationManager);
        policy.setAuthorizationManager(AuthorityAuthorizationManager.hasRole("ADMIN"));
        return policy;
    }
}
