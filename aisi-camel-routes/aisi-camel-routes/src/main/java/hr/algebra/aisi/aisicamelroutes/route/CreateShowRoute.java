package hr.algebra.aisi.aisicamelroutes.route;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.processor.AuthHeaderProcessor;
import hr.algebra.aisi.aisicamelroutes.processor.RequestResponseLoggingProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateShowRoute extends RouteBuilder {

    @Autowired
    private AuthHeaderProcessor authHeaderProcessor;

    @Autowired
    private RequestResponseLoggingProcessor loggingProcessor;

    private static final String[] NEW_SHOWS = {
            """
        {
          "showType": "MOVIE",
          "title": "New Movie",
          "director": "Mislav",
          "country": "Croatia",
          "dateAdded": "2025-01-01",
          "releaseYear": 2025,
          "rating": "PG-13",
          "duration": "120 min",
          "listedIn": "Movies",
          "description": "Created movie"
        }
        """,
            """
        {
          "showType": "TV_SHOW",
          "title": "New Show",
          "director": "Mislav",
          "country": "Croatia",
          "dateAdded": "2025-02-01",
          "releaseYear": 2024,
          "rating": "TV",
          "duration": "2 Seasons",
          "listedIn": "Shows",
          "description": "Created show"
        }
        """
    };

    private int showIndex = 0;

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("[create-show] Error: ${exception.message}")
                .setBody(simple("{\"error\": \"${exception.message}\"}"))
                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=create-show-ERROR-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:createShow?period=30000&delay=5000")
                .routeId("create-show")

                .process(exchange -> {
                    String payload = NEW_SHOWS[showIndex % NEW_SHOWS.length];
                    showIndex++;
                    exchange.getIn().setBody(payload);
                    exchange.setProperty("httpMethod", "POST");
                    exchange.setProperty("targetEndpoint", AppConfig.NETFLIX_API_BASE);
                    exchange.setProperty("requestBody", payload);
                })

                .process(authHeaderProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))

                .log("[create-show] POST ${exchangeProperty.targetEndpoint}")
                .to(AppConfig.NETFLIX_API_BASE + "?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .process(loggingProcessor)

                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=create-show-${date:now:yyyyMMdd-HHmmss}.json")

                .log("[create-show] Response saved.");
    }
}
