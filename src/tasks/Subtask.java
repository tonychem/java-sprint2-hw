package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Subtask extends Task {

    private Epic myEpicReference;

    private long epicId;

    private static TaskType type = TaskType.SUBTASK;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, Status status) {
        super(title, description, status);
    }

    public Subtask(String title, String description, Status status, Instant startTime, Duration duration) {
        super(title, description, status, startTime, duration);
    }

    public Epic getMyEpicReference() {
        return myEpicReference;
    }

    public void setMyEpicReference(Epic myEpicReference) {
        this.myEpicReference = myEpicReference;
        epicId = myEpicReference.getId();
    }

    public long getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return type;
    }

    public static void setType(TaskType type) {
        Subtask.type = type;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(getTitle(), subtask.getTitle()) && Objects.equals(getDescription(), subtask.getDescription())
                && Objects.equals(getStatus(), subtask.getStatus());
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(getTitle(), getDescription());
        hash = 31 * hash + Objects.hash(getStatus());
        return hash;
    }
}
