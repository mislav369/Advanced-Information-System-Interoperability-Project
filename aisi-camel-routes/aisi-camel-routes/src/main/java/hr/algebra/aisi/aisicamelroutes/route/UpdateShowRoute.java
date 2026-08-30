package hr.algebra.aisi.aisicamelroutes.route;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.processor.AuthHeaderProcessor;
import hr.algebra.aisi.aisicamelroutes.processor.RequestResponseLoggingProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateShowRoute extends RouteBuilder {

    @Autowired
    private AuthHeaderProcessor authHeaderProcessor;

    @Autowired
    private RequestResponseLoggingProcessor loggingProcessor;

    private static final Object[][] UPDATES = {
            {1L, """
             {
               "showType": "MOVIE",
               "title": "New Movie 2nd edition",
               "director": "Mislav",
               "country": "Croatia",
               "dateAdded": "2025-03-01",
               "releaseYear": 2025,
               "rating": "PG",
               "duration": "100 min",
               "listedIn": "Movies",
               "description": "Updated movie"
             }
             """},
            {2L, """
             {
               "showType": "TV_SHOW",
               "title": "New Show 2nd edition",
               "director": "Mislav",
               "country": "Croatia",
               "dateAdded": "2025-04-01",
               "releaseYear": 2023,
               "rating": "TV-14",
               "duration": "3 Seasons",
               "listedIn": "Shows",
               "description": "Updated show"
             }
             """}
    };

    private int updateIndex = 0;

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("[update-show] Error: ${exception.message}")
                .setBody(simple("{\"error\": \"${exception.message}\"}"))
                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=update-show-ERROR-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:updateShow?period=45000&delay=7000")
                .routeId("update-show")

                .process(exchange -> {
                    Object[] pair = UPDATES[updateIndex % UPDATES.length];
                    updateIndex++;
                    Long showId = (Long) pair[0];
                    String payload = (String) pair[1];
                    exchange.getIn().setBody(payload);
                    exchange.setProperty("showId", showId);
                    exchange.setProperty("httpMethod", "PUT");
                    exchange.setProperty("targetEndpoint", AppConfig.NETFLIX_API_BASE + "/" + showId);
                    exchange.setProperty("requestBody", payload);
                })

                .process(authHeaderProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .setHeader(Exchange.HTTP_PATH, simple("/api/v1/netflix-shows/${exchangeProperty.showId}"))

                .log("[update-show] PUT id=${exchangeProperty.showId}")
                .to(AppConfig.API_HOST + "?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .process(loggingProcessor)

                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=update-show-id${exchangeProperty.showId}-${date:now:yyyyMMdd-HHmmss}.json")
                .to("direct:forwardToBroker")
                .log("[update-show] Show ${exchangeProperty.showId} updated.");
    }
}
