package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    private static TaskType type = TaskType.TASK;
    private String title;
    private String description;
    private long id;
    private Status status;
    private Duration duration;
    private Instant startTime;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = Status.NEW;
    }

    public Task(String title, String description, Status status) {
        this(title, description);
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return duration == null ? null : startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(title, description, id, status);
        hash = 31 * hash + Objects.hash(status);
        return hash;
    }
}
