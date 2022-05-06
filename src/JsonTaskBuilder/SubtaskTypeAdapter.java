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

public class SubtaskTypeAdapter extends TypeAdapter<Subtask> {
    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("id")
                .value(subtask.getId());

        jsonWriter.name("type")
                .value(subtask.getType().toString());

        jsonWriter.name("name")
                .value(subtask.getTitle());

        jsonWriter.name("description")
                .value(subtask.getDescription());

        jsonWriter.name("status")
                .value(subtask.getStatus().toString());

        jsonWriter.name("epic")
                .value(subtask.getMyEpicReference().getId());

        jsonWriter.name("startTime")
                .value(subtask.getStartTime().toString());

        jsonWriter.name("duration")
                .value(subtask.getDuration().toString());

        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader jsonReader) throws IOException {
        Subtask subtask = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName();
            subtask = new Subtask(null, null);
            switch (fieldName) {
                case "name":
                    subtask.setTitle(jsonReader.nextString());
                    break;
                case "description":
                    subtask.setDescription(jsonReader.nextString());
                    break;
                case "status":
                    subtask.setStatus(Status.valueOf(jsonReader.nextString()));
                    break;
                case "epic":
                    // Через эпик-пустышку
                    Epic dummy = new Epic(null, null, null);
                    dummy.setId(jsonReader.nextInt());
                    subtask.setMyEpicReference(dummy);
                case "startTime":
                    subtask.setStartTime(Instant.ofEpochMilli(jsonReader.nextLong()));
                    break;
                case "duration":
                    subtask.setDuration(Duration.ofMillis(jsonReader.nextLong()));
                    break;
                default:
                    jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return subtask;
    }
}
