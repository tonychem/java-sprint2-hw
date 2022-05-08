package Server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private String path;
    private TaskManager manager;
    private final static String HTTP_MANAGER_SERVER_URL = "http://localhost:8078";

    public HttpTaskServer() throws IOException, InterruptedException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        //Работа с HttpTaskServer идет во временный файл, удаляемый по окончании работы
        path = System.getProperty("user.dir") + "\\src\\Server";
        File temporaryFileForStorage = File.createTempFile("TaskManagerFile", ".csv", new File(path));
        temporaryFileForStorage.deleteOnExit();
        manager = Managers.getDefault(HTTP_MANAGER_SERVER_URL);
    }

    public static void main(String[] args) {
        try {
            new KVServer().start();
            new HttpTaskServer().start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        server.createContext("/tasks", new TasksHandler(manager));
        server.start();
    }
}
