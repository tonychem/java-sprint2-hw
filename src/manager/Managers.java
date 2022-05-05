package manager;

public class Managers {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
