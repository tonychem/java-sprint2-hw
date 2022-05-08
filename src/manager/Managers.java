package manager;

import java.io.IOException;

public class Managers {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }

    public static TaskManager getDefault(String URL) throws IOException, InterruptedException {
        return new HTTPTaskManager(URL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
