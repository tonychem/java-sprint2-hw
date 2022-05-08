package HttpManagerTest;

import JsonTaskBuilder.JsonTask;
import Server.HttpTaskServer;
import Server.KVServer;
import manager.HTTPTaskManager;
import manager.TaskManagerTest;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class HttpManagerTest {
    private HttpClient httpClient;
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    private static final String KV_SERVER_URL = "http://localhost:8078";

    private static Task task1;
    private static Epic epicEmpty;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Epic epicWithSubtasks;

    @BeforeAll
    public static void loadTasks() {
        task1 = new Task("task1", "task1");
        epicEmpty = new Epic("epicEmpty", "test", new ArrayList<>());
        subtask1 = new Subtask("Subtask1", "test", Status.IN_PROGRESS, Instant.EPOCH, Duration.ofSeconds(100));
        subtask2 = new Subtask("Subtask2", "test", Status.DONE, Instant.ofEpochSecond(120), Duration.ofSeconds(100));
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        epicWithSubtasks = new Epic("epicWithSubtasks", "test", subtasks);
    }

    @BeforeEach
    public void init() {
        httpClient = HttpClient.newHttpClient();
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void terminate() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    //TODO wont work
    public void saveTaskTestNormal() throws IOException, InterruptedException {
        URI taskURI = URI.create("http://localhost:8080/tasks/task");
        URI taskFromKVServer = URI.create("http://localhost:8078/load/" + "1" + "/?API_KEY=DEBUG");

        String task1json = JsonTask.writeTask(task1);

        HttpRequest requestToPublish = HttpRequest.newBuilder()
                .uri(taskURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task1json))
                .build();
        HttpRequest getRequestFromKVServer = HttpRequest.newBuilder()
                .uri(taskFromKVServer)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> responseFromHttp = httpClient.send(requestToPublish, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseFromKV = httpClient.send(getRequestFromKVServer, HttpResponse.BodyHandlers.ofString());

        Assertions.assertTrue(responseFromKV.body().equals(task1json));
    }

}
