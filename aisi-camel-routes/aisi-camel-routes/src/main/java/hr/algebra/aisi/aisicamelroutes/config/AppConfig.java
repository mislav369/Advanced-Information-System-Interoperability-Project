package hr.algebra.aisi.aisicamelroutes.config;

public class AppConfig {

    public static final String NETFLIX_API_BASE = "http://localhost:9090/api/v1/netflix-shows";
    public static final String AUTH_URL = "http://localhost:9090/auth/login";

    public static final String ORIGIN = "http://localhost:8082";

    public static final String USERNAME = "userone";
    public static final String PASSWORD = "P@ssw0rd";

    public static final String OUTPUT_DIR = "output/responses";
    public static final String API_HOST = "http://localhost:9090";

    public static final String RABBIT_EXCHANGE = "aisi.exchange";
    public static final String RABBIT_QUEUE = "aisi.queue";
    public static final String RABBIT_ROUTING_KEY = "rest.call";

    public static final String RABBIT_PRODUCER_URI =
            "spring-rabbitmq:" + RABBIT_EXCHANGE
                    + "?routingKey=" + RABBIT_ROUTING_KEY
                    + "&autoDeclare=true&arg.exchange.durable=true";

    public static final String RABBIT_CONSUMER_URI =
            "spring-rabbitmq:" + RABBIT_EXCHANGE
                    + "?queues=" + RABBIT_QUEUE
                    + "&routingKey=" + RABBIT_ROUTING_KEY
                    + "&autoDeclare=true&arg.queue.durable=true&arg.exchange.durable=true";

    public static final String KAFKA_TOPIC = "aisi-rest-calls";
}
