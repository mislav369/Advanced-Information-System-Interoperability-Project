package hr.algebra.aisi.aisicamelroutes.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestResponseLoggingProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingProcessor.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void process(Exchange exchange) {
        Integer httpStatus  = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        String  routeId     = exchange.getFromRouteId();
        String  httpMethod  = exchange.getProperty("httpMethod", String.class);
        String  endpoint    = exchange.getProperty("targetEndpoint", String.class);
        String  requestBody = exchange.getProperty("requestBody", String.class);
        String  responseBody = exchange.getIn().getBody(String.class);
        String  timestamp   = LocalDateTime.now().format(FORMATTER);

        log.info("== Route: {} | {} {} | HTTP {} ==", routeId, httpMethod, endpoint, httpStatus);

        String envelope = """
                {
                  "meta": {
                    "capturedAt": "%s",
                    "routeId": "%s",
                    "httpStatus": %d
                  },
                  "request": {
                    "method": "%s",
                    "endpoint": "%s",
                    "body": %s
                  },
                  "response": %s
                }
                """.formatted(
                timestamp, routeId, httpStatus != null ? httpStatus : 0,
                httpMethod, endpoint,
                requestBody == null ? "null" : requestBody.trim(),
                responseBody == null ? "null" : responseBody.trim());

        exchange.getIn().setBody(envelope);
    }
}
