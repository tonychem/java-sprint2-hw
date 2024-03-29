package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {

    private static TaskType type = TaskType.EPIC;

    private ArrayList<Subtask> mySubtasks;
    private Instant startTime;
    private Duration duration;

    public Epic(String title, String description, ArrayList<Subtask> subtasks) {
        super(title, description);
        this.mySubtasks = subtasks;
    }

    private Epic(String title, String description, Status status, ArrayList<Subtask> subtasks) {
        super(title, description, status);
        this.mySubtasks = subtasks;
    }

    private Epic(String title, String description, Status status, ArrayList<Subtask> subtasks,
                 Instant startTime, Duration duration) {
        super(title, description, status);
        this.mySubtasks = subtasks;
        this.startTime = startTime;
        this.duration = duration;
    }

    public ArrayList<Subtask> getMySubtasks() {
        return mySubtasks;
    }

    public void setMySubtasks(ArrayList<Subtask> mySubtasks) {
        if (mySubtasks != null) {
            for (Subtask s : mySubtasks) {
                s.setMyEpicReference(this);
            }
        }
        this.mySubtasks = mySubtasks;
    }

    public void putSubtask(Subtask sub) {
        if (!mySubtasks.contains(sub)) {
            mySubtasks.add(sub);
        }
    }

    public void deleteSubtask(long ID) {
        if (mySubtasks != null) {
            for (Subtask sub : mySubtasks) {
                if (sub.getId() == ID) {
                    mySubtasks.remove(sub);
                    return;
                }
            }
        }
    }

    public TaskType getType() {
        return type;
    }

    //Метод возвращает новый экземпляр с обновленным статусом, расчитанным по статусам подзадач
    public Epic update() {
        boolean allNEW = true;
        boolean allDONE = true;
        Epic newEpicWithUpdatedStatus;

        for (Subtask sub : mySubtasks) {
            allNEW &= sub.getStatus() == Status.NEW;
            allDONE &= sub.getStatus() == Status.DONE;
        }

        if (mySubtasks.isEmpty() || allNEW) {
            newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), Status.NEW, getMySubtasks(),
                    getStartTime(), getDuration());
            newEpicWithUpdatedStatus.setId(getId());
            setThisEpicForSubtasks(newEpicWithUpdatedStatus);
            return newEpicWithUpdatedStatus;
        } else if (allDONE) {
            newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), Status.DONE, getMySubtasks(),
                    getStartTime(), getDuration());
            newEpicWithUpdatedStatus.setId(getId());
            setThisEpicForSubtasks(newEpicWithUpdatedStatus);
            return newEpicWithUpdatedStatus;
        }

        newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), Status.IN_PROGRESS, getMySubtasks(),
                getStartTime(), getDuration());
        newEpicWithUpdatedStatus.setId(getId());
        setThisEpicForSubtasks(newEpicWithUpdatedStatus);
        return newEpicWithUpdatedStatus;
    }

    private void setThisEpicForSubtasks(Epic e) {
        for (Subtask sub : e.getMySubtasks()) {
            sub.setMyEpicReference(e);
        }
    }

    @Override
    public Duration getDuration() {
        Instant startTime = getStartTime();
        Instant endTime = getEndTime();
        return (startTime == null || endTime == null) ? null : Duration.between(startTime, endTime);
    }

    @Override
    public void setDuration(Duration duration) {
        //Класс закрыт для установки продолжительности
    }

    @Override
    public Instant getStartTime() {
        Optional<Instant> theEarliestSubtaskInstant = mySubtasks.stream()
                .map(x -> x.getStartTime())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());
        return theEarliestSubtaskInstant.orElse(null);
    }

    @Override
    public void setStartTime(Instant startTime) {
        //Класс закрыт для установки времени начала
    }

    @Override
    public Instant getEndTime() {
        Optional<Instant> theLatestSubtaskInstant = mySubtasks.stream()
                .map(x -> x.getEndTime())
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
        return theLatestSubtaskInstant.orElse(null);
    }

    @Override
    public void setStatus(Status s) {
        //закрыт для записи
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", subtasks=" + mySubtasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(mySubtasks, epic.mySubtasks) && Objects.equals(getStatus(), epic.getStatus())
                && Objects.equals(getTitle(), epic.getTitle()) && Objects.equals(getDescription(), epic.getDescription())
                && Objects.equals(mySubtasks, epic.getMySubtasks());
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(getTitle(), getDescription(), getStatus());
        hash = 31 * hash + (mySubtasks == null ? 0 : mySubtasks.hashCode());
        return hash;
    }
}
