package Server;

import JsonTaskBuilder.JsonTaskIO;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler implements HttpHandler {
    private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/tasks/") || requestPath.equals("/tasks")) {
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(manager.getPrioritizedTasks().toString().getBytes(StandardCharsets.UTF_8));
            os.close();
        }

        if (exchange.getRequestMethod().equals("POST")) {
            if (requestPath.split("/")[2].equals("task")) {
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveTask(JsonTaskIO.read(jsonTask));
                exchange.close();
            }
        }
    }

    private void subtaskHandler(HttpExchange exchange) {

    }

    private void epicHandler(HttpExchange exchange) {

    }

    private void taskHandler(HttpExchange exchange) {

    }

    private void taskToJson(Task task) {
        JsonTaskIO.write(task);
    }
}
