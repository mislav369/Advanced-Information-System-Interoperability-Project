package hr.algebra.aisi.aisicamelroutes.route;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SecureTriggerRoute extends RouteBuilder {

    private final SpringSecurityAuthorizationPolicy adminPolicy;

    public SecureTriggerRoute(@Qualifier("adminPolicy") SpringSecurityAuthorizationPolicy adminPolicy) {
        this.adminPolicy = adminPolicy;
    }

    @Override
    public void configure() {
        onException(CamelAuthorizationException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(403))
                .setBody(constant("{\"error\":\"forbidden\",\"reason\":\"ROLE_ADMIN required\"}"));

        from("platform-http:/camel/secure/trigger?httpMethodRestrict=POST")
                .routeId("secure-trigger")
                .policy(adminPolicy)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setBody(constant("{\"status\":\"accepted\",\"route\":\"secure-trigger\"}"));
    }
}
