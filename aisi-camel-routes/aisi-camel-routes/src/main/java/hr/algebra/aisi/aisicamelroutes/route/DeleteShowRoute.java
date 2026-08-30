package hr.algebra.aisi.aisicamelroutes.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.processor.AuthHeaderProcessor;
import hr.algebra.aisi.aisicamelroutes.processor.RequestResponseLoggingProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteShowRoute extends RouteBuilder {

    @Autowired
    private AuthHeaderProcessor authHeaderProcessor;

    @Autowired
    private RequestResponseLoggingProcessor loggingProcessor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEMP_SHOW = """
            {
              "showType": "MOVIE",
              "title": "Movie to delete",
              "director": "Mislav",
              "country": "Croatia",
              "dateAdded": "2025-05-01",
              "releaseYear": 2025,
              "rating": "PG",
              "duration": "90 min",
              "listedIn": "Movies",
              "description": "I will be deleted"
            }
            """;

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("[delete-show] Error: ${exception.message}")
                .setBody(simple("{\"error\": \"${exception.message}\"}"))
                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=delete-show-ERROR-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:deleteShow?period=60000&delay=10000")
                .routeId("delete-show")

                .setBody(constant(TEMP_SHOW))
                .process(authHeaderProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .log("[delete-show] Creating temporary show...")
                .to(AppConfig.NETFLIX_API_BASE + "?bridgeEndpoint=true")
                .convertBodyTo(String.class)

                .process(exchange -> {
                    String responseBody = exchange.getIn().getBody(String.class);
                    JsonNode data = objectMapper.readTree(responseBody).get("data");
                    Long createdId = data.get("id").asLong();
                    exchange.setProperty("createdShowId", createdId);
                })

                .log("[delete-show] Deleting show id=${exchangeProperty.createdShowId}")
                .process(authHeaderProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .setHeader(Exchange.HTTP_PATH, simple("/api/v1/netflix-shows/${exchangeProperty.createdShowId}"))
                .setBody(constant(null))
                .to(AppConfig.API_HOST + "?bridgeEndpoint=true")
                .convertBodyTo(String.class)

                .process(exchange -> {
                    exchange.setProperty("httpMethod", "DELETE");
                    exchange.setProperty("targetEndpoint",
                            AppConfig.NETFLIX_API_BASE + "/" + exchange.getProperty("createdShowId"));
                    exchange.setProperty("requestBody", null);
                })
                .process(loggingProcessor)

                .to("file:" + AppConfig.OUTPUT_DIR
                        + "?fileName=delete-show-id${exchangeProperty.createdShowId}-${date:now:yyyyMMdd-HHmmss}.json")

                .log("[delete-show] Temp show ${exchangeProperty.createdShowId} deleted.");
    }
}
