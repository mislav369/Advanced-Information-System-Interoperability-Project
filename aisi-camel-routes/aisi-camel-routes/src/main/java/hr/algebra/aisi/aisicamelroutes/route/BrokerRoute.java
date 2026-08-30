package hr.algebra.aisi.aisicamelroutes.route;

import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import hr.algebra.aisi.aisicamelroutes.config.CryptoConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BrokerRoute extends RouteBuilder {

    private final CryptoDataFormat aesFormat;

    public BrokerRoute(@Qualifier(CryptoConfig.AES_FORMAT) CryptoDataFormat aesFormat) {
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {

        from("direct:forwardToBroker")
                .routeId("broker-producer")
                .log("[broker-producer] Forwarding REST call to RabbitMQ and Kafka")
                .to("kafka:" + AppConfig.KAFKA_TOPIC)
                .convertBodyTo(byte[].class)
                .marshal(aesFormat)
                .to(AppConfig.RABBIT_PRODUCER_URI);

        from(AppConfig.RABBIT_CONSUMER_URI)
                .routeId("rabbit-consumer")
                .unmarshal(aesFormat)
                .convertBodyTo(String.class)
                .log("[rabbit-consumer] Received from RabbitMQ: ${body}");

        from("kafka:" + AppConfig.KAFKA_TOPIC + "?groupId=aisi-group")
                .routeId("kafka-consumer")
                .convertBodyTo(String.class)
                .log("[kafka-consumer] Received from Kafka: ${body}");
    }
}