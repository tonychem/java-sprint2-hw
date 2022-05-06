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
import java.util.Locale;

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

        jsonWriter.name("status")
                .value(task.getStatus().toString());

        jsonWriter.name("description")
                .value(task.getDescription());

        jsonWriter.name("epic");
        if (task.getType() == TaskType.SUBTASK) {
            jsonWriter.value(((Subtask) task).getMyEpicReference().getId());
        } else {
            jsonWriter.value("");
        }

        jsonWriter.name("startTime")
                .value(task.getStartTime().toString());

        jsonWriter.name("duration")
                .value(task.getDuration().toString());

        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        TaskType type = null;
        String name = null;
        Status status = null;
        String description = null;
        long epicId = 0;
        Instant startTime = null;
        Duration duration = null;
        Task taskToReturn = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName().toLowerCase();
            switch (fieldName) {
                case "id":
                    jsonReader.nextString(); //пропускаем строку, назначение id делегируется менеджеру
                    break;
                case "type":
                    type = TaskType.valueOf(jsonReader.nextString().toUpperCase());
                    break;
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "status":
                    status = Status.valueOf(jsonReader.nextString().toUpperCase());
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "epic":
                    epicId = jsonReader.nextLong();
                    break;
                case "startTime":
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

        switch (type) {
            case TASK:
                taskToReturn = new Task(name, description, status, startTime, duration);
                break;
            case SUBTASK:
                taskToReturn = new Subtask(name, description, status, startTime, duration);

                // при десериализации подзадание не знает свои эпики, поэтому присваиваем эпик-заглушку
                // для сохранения его ID
                Epic dummy = new Epic(null, null, null);
                dummy.setId(epicId);
                ((Subtask) taskToReturn).setMyEpicReference(dummy);
                break;
            case EPIC:
                taskToReturn = new Epic(name, description, new ArrayList<>());

                // Через Reflection API установим приватные поля в эпике
                try {
                    Field statusField = Task.class.getDeclaredField("status");
                    statusField.setAccessible(true);
                    statusField.set(taskToReturn, status);

                    Field startTimeField = Epic.class.getDeclaredField("startTime");
                    startTimeField.setAccessible(true);
                    startTimeField.set(taskToReturn, startTime);

                    Field durationField = Epic.class.getDeclaredField("duration");
                    durationField.setAccessible(true);
                    durationField.set(taskToReturn, duration);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
        return taskToReturn;
    }
}
