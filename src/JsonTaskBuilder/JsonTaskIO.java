package JsonTaskBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Task;

public class JsonTaskIO {
    private static Gson gson = new GsonBuilder()
                                        .serializeNulls()
                                        .setPrettyPrinting()
                                        .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                                        .create();

    public static void write(Task t) {
        gson.toJson(t);
    }

    public static Task read(String json) {
        return gson.fromJson(json, Task.class);
    }

}
