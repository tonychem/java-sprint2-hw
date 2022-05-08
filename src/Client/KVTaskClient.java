package Client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String API_KEY;
    private String SERVER_URL;
    private final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String SERVER_URL) throws IOException, InterruptedException {
        this.SERVER_URL = SERVER_URL;
        URI serverRegisterUri = URI.create(SERVER_URL + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serverRegisterUri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_KEY = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(SERVER_URL + "/save/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(SERVER_URL + "/load/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
