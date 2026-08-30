package hr.algebra.aisi.aisicamelroutes.route;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.processor.AuthHeaderProcessor;
import hr.algebra.aisi.aisicamelroutes.processor.RequestResponseLoggingProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAllShowsRoute extends RouteBuilder {

    @Autowired
    private AuthHeaderProcessor authHeaderProcessor;

    @Autowired
    private RequestResponseLoggingProcessor loggingProcessor;

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("[get-all-shows] Error: ${exception.message}")
                .setBody(simple("{\"error\": \"${exception.message}\"}"))
                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=get-all-shows-ERROR-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:getAllShows?period=15000&delay=3000")
                .routeId("get-all-shows")

                .setProperty("httpMethod", constant("GET"))
                .setProperty("targetEndpoint", constant(AppConfig.NETFLIX_API_BASE))
                .setProperty("requestBody", constant(null))

                .process(authHeaderProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Accept", constant("application/json"))
                .setBody(constant(null))

                .log("[get-all-shows] Calling GET ${exchangeProperty.targetEndpoint}")
                .to(AppConfig.NETFLIX_API_BASE + "?bridgeEndpoint=true")
                .convertBodyTo(String.class)

                .process(loggingProcessor)

                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=get-all-shows-${date:now:yyyyMMdd-HHmmss}.json")
                .to("direct:forwardToBroker")
                .log("[get-all-shows] Response saved to " + AppConfig.OUTPUT_DIR);
    }
}
