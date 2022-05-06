package Server;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private String path;
    private TaskManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        path = System.getProperty("user.dir") + "\\src\\Server\\TaskManagerFile.csv";
        manager = Managers.getFileBackedTasksManager(path);
    }

    public static void main(String[] args) {
        try {
            new HttpTaskServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        server.createContext("/tasks", new TasksHandler(manager));
        server.start();
    }
}
