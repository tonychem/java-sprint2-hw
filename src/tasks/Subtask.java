package tasks;

import java.util.Objects;

public class Subtask extends Task {

    private Epic myEpicReference;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic getMyEpicReference() {
        return myEpicReference;
    }

    public void setMyEpicReference(Epic myEpicReference) {
        this.myEpicReference = myEpicReference;
    }

    public Subtask setStatus(Status status) {
        Subtask subtask = new Subtask(this.getTitle(), this.getDescription(), status);
        subtask.setId(this.getId());
        subtask.setMyEpicReference(this.getMyEpicReference());
        return subtask;
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
