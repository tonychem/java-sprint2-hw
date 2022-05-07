package JsonTaskBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

/**
 * Утилитный класс, который читает и пишет json-файлы, кастомизированные под TaskManager
 */
public class JsonTask {

    public static String writeTask(Task t) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                .serializeNulls()
                .create();

        return gson.toJson(t, Task.class);
    }

    public static String writeSubtask(Subtask subtask) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Subtask.class, new SubtaskTypeAdapter())
                .serializeNulls()
                .create();

        return gson.toJson(subtask, Subtask.class);
    }

    public static String writeEpic(Epic epic) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, new EpicTypeAdapter())
                .serializeNulls()
                .create();

        return gson.toJson(epic, Epic.class);
    }

    public static Task readTask(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                .create();

        return gson.fromJson(json, Task.class);
    }

    public static Subtask readSubtask(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, new SubtaskTypeAdapter())
                .create();

        return gson.fromJson(json, Subtask.class);
    }

    public static Epic readEpic(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Epic.class, new EpicTypeAdapter())
                .create();

        return gson.fromJson(json, Epic.class);
    }

}
