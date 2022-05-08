package Client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private static String API_KEY;
    private static final String SERVER_URI = "http://localhost:8078";
    private static final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient() throws IOException, InterruptedException {
        URI serverRegisterUri = URI.create(SERVER_URI + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverRegisterUri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_KEY = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(SERVER_URI + "/save/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(SERVER_URI + "/load/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
