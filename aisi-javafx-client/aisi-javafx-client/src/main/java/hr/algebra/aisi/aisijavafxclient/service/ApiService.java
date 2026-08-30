package hr.algebra.aisi.aisijavafxclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.aisi.aisijavafxclient.model.NetflixShow;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ApiService {
    private static final String BASE_URL = "http://localhost:9090";
    private static final String ORIGIN = "http://localhost:8082";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean login(String username, String password) throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                Map.of("username", username, "password", password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .header("Origin", ORIGIN)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return false;
        }
        JsonNode data = objectMapper.readTree(response.body()).get("data");
        Session.setAccessToken(data.get("accessToken").asText());
        Session.setRefreshToken(data.get("refreshToken").asText());
        return true;
    }

    public List<NetflixShow> getAllShows() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/netflix-shows"))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return new ArrayList<>();
        }
        JsonNode data = objectMapper.readTree(response.body()).get("data");
        NetflixShow[] shows = objectMapper.treeToValue(data, NetflixShow[].class);
        return new ArrayList<>(Arrays.asList(shows));
    }

    public boolean createShow(NetflixShow show) throws Exception {
        String requestBody = objectMapper.writeValueAsString(show);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/netflix-shows"))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200 || response.statusCode() == 201;
    }

    public boolean updateShow(Long id, NetflixShow show) throws Exception {
        String requestBody = objectMapper.writeValueAsString(show);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/netflix-shows/" + id))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    public boolean deleteShow(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/netflix-shows/" + id))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .DELETE()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    public String createBackup() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/database/backup"))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }

        JsonNode data = objectMapper.readTree(response.body()).get("data");
        return data == null ? null : data.asText();
    }

    public boolean restoreBackup(String fileName) throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of("fileName", fileName));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/database/restore"))
                .header("Authorization", "Bearer " + Session.getAccessToken())
                .header("Origin", ORIGIN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
}
