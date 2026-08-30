package hr.algebra.aisi.aisicamelroutes.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.aisi.aisicamelroutes.config.AppConfig;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class AuthTokenService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;

    public synchronized String getToken() throws Exception {
        if (token == null) {
            token = fetchToken();
        }
        return token;
    }

        public String fetchToken() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                Map.of("username", AppConfig.USERNAME, "password", AppConfig.PASSWORD));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.AUTH_URL))
                .header("Content-Type", "application/json")
                .header("Origin", AppConfig.ORIGIN)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed with status " + response.statusCode());
        }

        JsonNode data = objectMapper.readTree(response.body()).get("data");
        return data.get("accessToken").asText();
    }
}
