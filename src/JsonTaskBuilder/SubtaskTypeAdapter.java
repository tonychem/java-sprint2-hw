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

public class SubtaskTypeAdapter extends TypeAdapter<Subtask> {
    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("id").value(subtask.getId());

        jsonWriter.name("type").value(subtask.getType().toString());

        jsonWriter.name("name").value(subtask.getTitle());

        jsonWriter.name("description").value(subtask.getDescription());

        jsonWriter.name("status").value(subtask.getStatus().toString());

        jsonWriter.name("epic").value(subtask.getMyEpicReference().getId());

        jsonWriter.name("startTime");

        if (subtask.getStartTime() == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(subtask.getStartTime().toString());
        }

        jsonWriter.name("duration");

        if (subtask.getDuration() == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(subtask.getDuration().toString());
        }

        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader jsonReader) throws IOException {
        Subtask subtask = null;
        String name = null;
        String description = null;
        Status status = Status.NEW;
        Instant startTime = null;
        Duration duration = null;
        long dummyId = 0;
        Epic dummy = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName().toLowerCase();
            dummy = new Epic(null, null, new ArrayList<>());
            switch (fieldName) {
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "status":
                    status = Status.valueOf(jsonReader.nextString().toUpperCase());
                    break;
                case "epic":
                    dummyId = jsonReader.nextLong();
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
        subtask = new Subtask(name, description, status, startTime, duration);
        dummy.setId(dummyId);
        subtask.setMyEpicReference(dummy);

        return subtask;
    }
}
