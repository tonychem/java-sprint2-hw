package Server;

import JsonTaskBuilder.JsonTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.*;

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
        //если путь содержит только /tasks
        if (requestPath.equals("/tasks/") || requestPath.equals("/tasks")) {
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(manager.getPrioritizedTasks().toString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        // в противном случае анализируем, что следует в пути запроса после /tasks/ и делегируем запрос
        String[] splitRequestPath = requestPath.split("/");
        switch (splitRequestPath[2]) {
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
        if (exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(201, 0);
            InputStream is = exchange.getRequestBody();
            String jsonTask = new String(is.readAllBytes());
            manager.saveSubtask( (Subtask) JsonTask.readSubtask(jsonTask));
            exchange.close();
        }
    }

    private void epicHandler(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(201, 0);
            InputStream is = exchange.getRequestBody();
            String jsonTask = new String(is.readAllBytes());
            manager.saveEpic((Epic) JsonTask.readEpic(jsonTask));
            exchange.close();
        }
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(201, 0);
            InputStream is = exchange.getRequestBody();
            String jsonTask = new String(is.readAllBytes());
            manager.saveTask(JsonTask.readTask(jsonTask));
            exchange.close();
        }
    }
}
