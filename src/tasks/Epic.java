package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private static TaskType type = TaskType.EPIC;

    private ArrayList<Subtask> mySubtasks;

    public Epic(String title, String description, ArrayList<Subtask> subtasks) {
        super(title, description);
        this.mySubtasks = subtasks;
    }

    private Epic(String title, String description, Status status, ArrayList<Subtask> subtasks) {
        super(title, description, status);
        this.mySubtasks = subtasks;
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
            newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), getMySubtasks());
            newEpicWithUpdatedStatus.setId(getId());
            return newEpicWithUpdatedStatus;
        } else if (allDONE) {
            newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), Status.DONE, getMySubtasks());
            newEpicWithUpdatedStatus.setId(getId());
            return newEpicWithUpdatedStatus;
        }

        newEpicWithUpdatedStatus = new Epic(getTitle(), getDescription(), Status.IN_PROGRESS, getMySubtasks());
        newEpicWithUpdatedStatus.setId(getId());
        return newEpicWithUpdatedStatus;
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
                && Objects.equals(getTitle(), epic.getTitle()) && Objects.equals(getDescription(), epic.getDescription());
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(getTitle(), getDescription(), getStatus());
        hash = 31 * hash + (mySubtasks == null? 0 : mySubtasks.hashCode());
        return hash;
    }
}
