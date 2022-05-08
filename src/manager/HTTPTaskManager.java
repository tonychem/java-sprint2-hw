package manager;

import Client.KVTaskClient;
import JsonTaskBuilder.JsonTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;

public class HTTPTaskManager extends FileBackedTasksManager {

    private KVTaskClient kvTaskClient;

    public HTTPTaskManager(String URL) throws IOException, InterruptedException {
        super();
        kvTaskClient = new KVTaskClient(URL);
    }

    @Override
    protected void save() {
        for (Task t : getPrioritizedTasks()) {
            if (t.getType() == TaskType.SUBTASK) {
                try {
                    kvTaskClient.put(String.valueOf(t.getId()), JsonTask.writeSubtask((Subtask) t));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (t.getType() == TaskType.EPIC) {
                try {
                    kvTaskClient.put(String.valueOf(t.getId()), JsonTask.writeEpic((Epic) t));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    kvTaskClient.put(String.valueOf(t.getId()), JsonTask.writeTask(t));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
