## Detailed instructions for starting project

1. Open docker desktop, engine is running
2. Start the infrastructure (databases, brokers, monitoring), From the backend repository root: docker compose up -d postgres rabbitmq kafka kafka-ui prometheus grafana
3. Set spring.jpa.hibernate.ddl-auto=create in the backend application.properties, run the backend once so it creates the tables then set it back to spring.jpa.hibernate.ddl-auto=none so the data is kept across restarts.
4. Run the Spring Boot backend. The backend exposes the Netflix Shows REST API /api/v1/netflix-shows
5. Open the JavaFX client project in IntelliJ and run it. Log in with userone / P@ssw0rd.
6. Open the Camel project (aisi-camel-routes) in IntelliJ. The configuration password is encrypted with Jasypt, so the master password must be passed as a VM option. In the run configuration, add to VM options: -Djasypt.encryptor.password=algebra-demo
7. Check the brokers and monitoring. RabbitMQ console: http://localhost:15672 (guest / guest). Kafka UI: http://localhost:8090 Prometheus: http://localhost:9091 Grafana: http://localhost:3000 (admin / admin)
8. Run the Camunda BPM environment. Open the BPMN diagrams and all .form files from the camunda/ folder in Camunda Modeler and deploy them to the local engine.