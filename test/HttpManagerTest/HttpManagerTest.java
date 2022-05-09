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
        epicEmpty = new Epic("epicEmpty", "test", new ArrayList<>()).update();
        subtask1 = new Subtask("Subtask1", "test", Status.IN_PROGRESS, Instant.EPOCH, Duration.ofSeconds(100));
        subtask2 = new Subtask("Subtask2", "test", Status.DONE, Instant.ofEpochSecond(120), Duration.ofSeconds(100));
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        epicWithSubtasks = new Epic("epicWithSubtasks", "test", subtasks).update();
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

//    @AfterEach
//    public void terminate() {
//        httpTaskServer.stop();
//        kvServer.stop();
//    }

    @Test
    //TODO wont work
    public void saveAndRetrieveTaskTestNormal() throws IOException, InterruptedException {
        URI taskURI = URI.create("http://localhost:8080/tasks/task");
        URI epicURI = URI.create("http://localhost:8080/tasks/epic");

        String task1Json = JsonTask.writeTask(task1);
        String epicEmptyJson = JsonTask.writeEpic(epicEmpty);
        String epicWithSubtasksJson = JsonTask.writeEpic(epicWithSubtasks);
        String subtask1Json = JsonTask.writeSubtask(subtask1);
        String subtask2Json = JsonTask.writeSubtask(subtask2);

        // id = 1
        HttpRequest requestToPublishTask1Json = HttpRequest.newBuilder()
                .uri(taskURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        // id = 2
        HttpRequest requestToPublishEpicEmptyJson = HttpRequest.newBuilder()
                .uri(epicURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicEmptyJson))
                .build();

        // id = 3, 4, 5
        HttpRequest requestToPublishEpicWithSubtasksJson = HttpRequest.newBuilder()
                .uri(epicURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicWithSubtasksJson))
                .build();

        // публикуем задания в HttpTaskManager-e
        httpClient.send(requestToPublishTask1Json, HttpResponse.BodyHandlers.ofString());
        httpClient.send(requestToPublishEpicEmptyJson, HttpResponse.BodyHandlers.ofString());
        httpClient.send(requestToPublishEpicWithSubtasksJson, HttpResponse.BodyHandlers.ofString());

        // формируем несколько запросов на получение заданий
        URI URIGetEpicWithSubtasks = URI.create("http://localhost:8078/load/" + "5" + "?API_KEY=DEBUG");
        HttpRequest requestToGetEpicWithSubtasksJson = HttpRequest.newBuilder()
                .uri(URIGetEpicWithSubtasks)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> responseTask1 = httpClient.send(requestToGetEpicWithSubtasksJson,
                HttpResponse.BodyHandlers.ofString());

        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> allTasksResponseTest = httpClient.send(r, HttpResponse.BodyHandlers.ofString());

        Assertions.assertTrue("epicWithSubtasks".equals(JsonTask.readEpic(responseTask1.body()).getTitle()));
    }

}
