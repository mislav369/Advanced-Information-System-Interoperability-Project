package hr.algebra.aisi.aisicamelroutes.processor;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.service.AuthTokenService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthHeaderProcessor implements Processor {

    @Autowired
    private AuthTokenService authTokenService;

    @Override
    public void process(Exchange exchange) throws Exception {
        String token = authTokenService.fetchToken();
        exchange.getIn().setHeader("Authorization", "Bearer " + token);
        exchange.getIn().setHeader("Origin", AppConfig.ORIGIN);
    }
}
