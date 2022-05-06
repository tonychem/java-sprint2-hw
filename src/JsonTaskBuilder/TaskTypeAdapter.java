package JsonTaskBuilder;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class TaskTypeAdapter extends TypeAdapter<Task> {
    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("id")
                .value(task.getId());

        jsonWriter.name("type")
                .value(task.getType().toString());

        jsonWriter.name("name")
                .value(task.getTitle());

        jsonWriter.name("description")
                .value(task.getDescription());

        jsonWriter.name("status")
                .value(task.getStatus().toString());

        jsonWriter.name("startTime")
                .value(task.getStartTime().toString());

        jsonWriter.name("duration")
                .value(task.getDuration().toString());

        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        String name = null;
        Status status = null;
        String description = null;
        Instant startTime = null;
        Duration duration = null;
        Task taskToReturn = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName().toLowerCase();

            switch (fieldName) {
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "status":
                    status = Status.valueOf(jsonReader.nextString().toUpperCase());
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "starttime":
                    startTime = Instant.ofEpochMilli(jsonReader.nextLong());
                    break;
                case "duration":
                    duration = Duration.ofMillis(jsonReader.nextLong());
                    break;
                default:
                    jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        taskToReturn = new Task(name, description, status, startTime, duration);

        return taskToReturn;
    }
}
