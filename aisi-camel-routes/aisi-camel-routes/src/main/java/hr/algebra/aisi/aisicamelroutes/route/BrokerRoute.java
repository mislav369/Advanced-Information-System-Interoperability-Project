package hr.algebra.aisi.aisicamelroutes.route;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BrokerRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:forwardToBroker")
                .routeId("broker-producer")
                .log("[broker-producer] Forwarding REST call to RabbitMQ and Kafka")
                .to(AppConfig.RABBIT_PRODUCER_URI)
                .to("kafka:" + AppConfig.KAFKA_TOPIC);

        from(AppConfig.RABBIT_CONSUMER_URI)
                .routeId("rabbit-consumer")
                .convertBodyTo(String.class)
                .log("[rabbit-consumer] Received from RabbitMQ: ${body}");

        from("kafka:" + AppConfig.KAFKA_TOPIC + "?groupId=aisi-group")
                .routeId("kafka-consumer")
                .convertBodyTo(String.class)
                .log("[kafka-consumer] Received from Kafka: ${body}");
    }
}