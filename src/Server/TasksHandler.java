package Server;

import JsonTaskBuilder.JsonTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TasksHandler implements HttpHandler {
    private TaskManager manager;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        //если путь содержит только /tasks
        if (requestPath.equalsIgnoreCase("/tasks/") || requestPath.equalsIgnoreCase("/tasks")) {
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();

            for (Epic e : manager.getAllEpics()) {
                os.write(JsonTask.writeEpic(e).getBytes(CHARSET));
                os.write("\n".getBytes(CHARSET));
            }

            for (Subtask sub : manager.getAllSubtasks()) {
                os.write(JsonTask.writeSubtask(sub).getBytes(CHARSET));
                os.write("\n".getBytes(CHARSET));
            }

            for (Task task : manager.getAllTasks()) {
                os.write(JsonTask.writeTask(task).getBytes(CHARSET));
                os.write("\n".getBytes(CHARSET));
            }
            os.close();
            return;
        }
        // в противном случае анализируем, что следует в пути запроса после /tasks/ и делегируем запрос
        String[] splitRequestPath = requestPath.split("/");
        switch (splitRequestPath[2].toLowerCase()) {
            case "task":
                taskHandler(exchange);
                break;
            case "epic":
                epicHandler(exchange);
                break;
            case "subtask":
                subtaskHandler(exchange);
                break;
            default:
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
        }
    }

    private void subtaskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(201, 0);
                OutputStream os = exchange.getResponseBody();

                for (Subtask sub : manager.getAllSubtasks()) {
                    os.write(JsonTask.writeSubtask(sub).getBytes(CHARSET));
                }
                os.close();
                return;
            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveSubtask(JsonTask.readSubtask(jsonTask));
                exchange.close();
                return;
        }
    }

    private void epicHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(201, 0);
                OutputStream os = exchange.getResponseBody();

                for (Epic e : manager.getAllEpics()) {
                    os.write(JsonTask.writeEpic(e).getBytes(CHARSET));
                }
                os.close();
                return;
            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveEpic(JsonTask.readEpic(jsonTask));
                exchange.close();
                return;
        }
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(201, 0);
                OutputStream os = exchange.getResponseBody();

                for (Task task : manager.getAllTasks()) {
                    os.write(JsonTask.writeTask(task).getBytes(CHARSET));
                }
                os.close();
                return;
            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveTask(JsonTask.readTask(jsonTask));
                exchange.close();
                return;
        }

    }
}
