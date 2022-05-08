package Server;

import JsonTaskBuilder.JsonTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler implements HttpHandler {
    private final TaskManager manager;
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

            for (Task t : manager.getPrioritizedTasks()) {
                if (t.getType() == TaskType.EPIC) {
                    os.write((JsonTask.writeEpic((Epic) t) + "\n").getBytes(CHARSET));
                } else if ((t.getType() == TaskType.TASK)) {
                    os.write((JsonTask.writeSubtask((Subtask) t) + "\n").getBytes(CHARSET));
                } else {
                    os.write((JsonTask.writeTask(t) + "\n").getBytes(CHARSET));
                }
            }
            os.close();
            return;

        } else if (requestPath.endsWith("/tasks/history")) {
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            for (Task t : manager.getHistoryManager().getHistory()) {
                if (t.getType() == TaskType.TASK) {
                    os.write((JsonTask.writeTask(t) + "\n").getBytes(CHARSET));
                } else if (t.getType() == TaskType.EPIC) {
                    os.write((JsonTask.writeEpic((Epic) t) + "\n").getBytes(CHARSET));
                } else {
                    os.write((JsonTask.writeSubtask((Subtask) t) + "\n").getBytes(CHARSET));
                }
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
                return;
        }
    }

    private void subtaskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String queryString = exchange.getRequestURI().getQuery();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(200, 0);
                //если параметр запроса пуст - вернуть все субтаски
                if (queryString == null) {
                    OutputStream os = exchange.getResponseBody();

                    for (Subtask sub : manager.getAllSubtasks()) {
                        os.write((JsonTask.writeSubtask(sub) + "\n").getBytes(CHARSET));
                    }
                    os.close();

                } else if (!exchange.getRequestURI().getPath().contains("subtask/epic")) {
                    long id;
                    //если в параметр запроса передан пустой id или нечисловое значение
                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    Subtask subtask = (Subtask) manager.getTaskByID(id);
                    if (subtask == null) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    } else {
                        OutputStream os = exchange.getResponseBody();
                        os.write(JsonTask.writeSubtask(subtask).getBytes(CHARSET));
                        os.close();
                    }
                } else {
                    long id;
                    //если в параметр запроса передан пустой id или нечисловое значение
                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    Optional<Epic> epicOptional = manager.getAllEpics().parallelStream()
                            .filter(x -> x.getId() == id)
                            .findAny();

                    if (epicOptional.isPresent()) {
                        OutputStream os = exchange.getResponseBody();

                        for (Subtask subtask : epicOptional.get().getMySubtasks()) {
                            os.write((JsonTask.writeSubtask(subtask) + "\n").getBytes(CHARSET));
                        }
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                    }
                }
                return;

            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveSubtask(JsonTask.readSubtask(jsonTask));
                exchange.close();
                return;

            case "DELETE":
                if (queryString == null) {
                    exchange.sendResponseHeaders(200, 0);
                    manager.eraseSubtasks();
                } else {
                    long id;
                    //если в параметр запроса передан пустой id или нечисловое значение
                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    manager.removeSubtask(id);
                    exchange.sendResponseHeaders(200, 0);
                }
                exchange.close();
                return;
        }
    }

    private void epicHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String queryString = exchange.getRequestURI().getQuery();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(200, 0);
                if (queryString == null) {
                    OutputStream os = exchange.getResponseBody();

                    for (Epic e : manager.getAllEpics()) {
                        os.write((JsonTask.writeEpic(e) + "\n").getBytes(CHARSET));
                    }
                    os.close();
                } else {
                    long id;

                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    Epic epic = (Epic) manager.getTaskByID(id);
                    if (epic == null) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    } else {
                        OutputStream os = exchange.getResponseBody();
                        os.write(JsonTask.writeEpic(epic).getBytes(CHARSET));
                        os.close();
                    }
                }
                return;

            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveEpic(JsonTask.readEpic(jsonTask));
                exchange.close();
                return;

            case "DELETE":
                if (queryString == null) {
                    exchange.sendResponseHeaders(200, 0);
                    manager.eraseEpics();
                } else {
                    long id;
                    //если в параметр запроса передан пустой id или нечисловое значение
                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    manager.removeEpic(id);
                    exchange.sendResponseHeaders(200, 0);
                }
                exchange.close();
                return;
        }
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String queryString = exchange.getRequestURI().getQuery();

        switch (requestMethod) {
            case "GET":
                exchange.sendResponseHeaders(200, 0);
                if (queryString == null) {
                    OutputStream os = exchange.getResponseBody();

                    for (Task task : manager.getAllTasks()) {
                        os.write((JsonTask.writeTask(task) + "\n").getBytes(CHARSET));
                    }
                    os.close();
                } else {
                    long id;

                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    Task task = manager.getTaskByID(id);
                    if (task == null) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    } else {
                        OutputStream os = exchange.getResponseBody();
                        os.write(JsonTask.writeTask(task).getBytes(CHARSET));
                        os.close();
                    }
                }
                return;

            case "POST":
                exchange.sendResponseHeaders(201, 0);
                InputStream is = exchange.getRequestBody();
                String jsonTask = new String(is.readAllBytes());
                manager.saveTask(JsonTask.readTask(jsonTask));
                exchange.close();
                return;

            case "DELETE":
                if (queryString == null) {
                    exchange.sendResponseHeaders(200, 0);
                    manager.eraseTasks();
                } else {
                    long id;
                    //если в параметр запроса передан пустой id или нечисловое значение
                    try {
                        id = Long.parseLong(queryString.split("id=")[1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exchange.close();
                        return;
                    }

                    manager.removeTask(id);
                    exchange.sendResponseHeaders(200, 0);
                }
                exchange.close();
                return;
        }
    }
}
