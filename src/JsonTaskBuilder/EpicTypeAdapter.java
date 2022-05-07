package JsonTaskBuilder;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class EpicTypeAdapter extends TypeAdapter<Epic> {
    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("id").value(epic.getId());

        jsonWriter.name("type").value(epic.getType().toString());

        jsonWriter.name("name").value(epic.getTitle());

        jsonWriter.name("description").value(epic.getDescription());

        jsonWriter.name("status").value(epic.getStatus().toString());

        jsonWriter.name("subtasks");
        writeSubtasks(jsonWriter, epic.getMySubtasks());

        jsonWriter.name("startTime");

        if (epic.getStartTime() == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(epic.getStartTime().toString());
        }

        jsonWriter.name("duration");

        if (epic.getDuration() == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(epic.getDuration().toString());
        }

        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        String name = null;
        String description = null;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName().toLowerCase();

            switch (fieldName) {
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "subtasks":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        subtasks.add(readSubtask(jsonReader));
                    }
                    jsonReader.endArray();
                    break;
                default:
                    jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        epic = new Epic(name, description, subtasks).update();
        return epic;
    }

    private void writeSubtasks(JsonWriter writer, ArrayList<Subtask> subtaskList) throws IOException {
        writer.beginArray();

        for (Subtask subtask : subtaskList) {
            writeSubtask(writer, subtask);
        }

        writer.endArray();
    }

    private Subtask readSubtask(JsonReader reader) throws IOException {
        Subtask subtask = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String fieldName = reader.nextName().toLowerCase();
            subtask = new Subtask(null, null);
            switch (fieldName) {
                case "name":
                    subtask.setTitle(reader.nextString());
                    break;
                case "description":
                    subtask.setDescription(reader.nextString());
                    break;
                case "status":
                    subtask.setStatus(Status.valueOf(reader.nextString().toUpperCase()));
                    break;
                case "startTime":
                    subtask.setStartTime(Instant.ofEpochMilli(reader.nextLong()));
                    break;
                case "duration":
                    subtask.setDuration(Duration.ofMillis(reader.nextLong()));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return subtask;
    }

    private void writeSubtask(JsonWriter writer, Subtask subtask) throws IOException {
        writer.beginObject();

        writer.name("id").value(subtask.getId());

        writer.name("type").value(subtask.getType().toString());

        writer.name("name").value(subtask.getTitle());

        writer.name("description").value(subtask.getDescription());

        writer.name("status").value(subtask.getStatus().toString());

        writer.name("epic").value(subtask.getMyEpicReference().getId());

        writer.name("startTime");

        if (subtask.getStartTime() == null) {
            writer.nullValue();
        } else {
            writer.value(subtask.getStartTime().toString());
        }

        writer.name("duration");

        if (subtask.getDuration() == null) {
            writer.nullValue();
        } else {
            writer.value(subtask.getDuration().toString());
        }

        writer.endObject();
    }
}
