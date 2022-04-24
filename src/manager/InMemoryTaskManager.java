package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Long, Task> taskMap;
    private final HashMap<Long, Epic> epicMap;
    private final HashMap<Long, Subtask> subtaskMap;
    private final HistoryManager historyManager;
    public long assignID;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        assignID = 1;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public void eraseTasks() {
        taskMap.clear();
    }

    @Override
    public void eraseEpics() {
        epicMap.clear();
        eraseSubtasks();
    }

    @Override
    public void eraseSubtasks() {
        subtaskMap.clear();
        for (Epic e : epicMap.values()) {
            e.setMySubtasks(new ArrayList<>());
            updateEpic(e.update());
        }
    }

    @Override
    public Task getTaskByID(long ID) {
        Task taskToReturn = taskMap.get(ID) != null ? taskMap.get(ID) : (epicMap.get(ID) != null ? epicMap.get(ID) : (subtaskMap.get(ID) != null ? subtaskMap.get(ID) : null));
        historyManager.add(taskToReturn);
        return taskToReturn;
    }

    @Override
    public void saveTask(Task t) {
        if (t == null) {
            throw new IllegalArgumentException("попытка записать значение null");
        }

        t.setId(assignID);
        taskMap.put(assignID, t);
        assignID++;
    }

    @Override
    public void saveEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("попытка записать значение null");
        }
        for (Subtask sub : epic.getMySubtasks()) {
            if (!subtaskMap.containsValue(sub)) {
                sub.setId(assignID);
                sub.setMyEpicReference(epic);
                subtaskMap.put(assignID, sub);
                assignID++;
            }
        }
        epic.setId(assignID);
        epicMap.put(assignID, epic.update());
        assignID++;
    }

    @Override
    public void saveSubtask(Subtask sub) {
        if (sub == null) {
            throw new IllegalArgumentException("попытка записать значение null");
        }
        if (sub.getMyEpicReference() == null) {
            throw new IllegalArgumentException("Нельзя передавать подзадачу без эпика");
        }
        if (!subtaskMap.containsValue(sub)) {
            sub.setId(assignID);
            subtaskMap.put(assignID, sub);
            assignID++;
            updateEpic(sub.getMyEpicReference().update());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || task.getId() == 0 || taskMap.containsValue(task)) {
            return;
        } else {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || epic.getId() == 0 || epicMap.containsValue(epic)) {
            return;
        } else {
            epicMap.put(epic.getId(), epic.update());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getId() == 0 || subtaskMap.containsValue(subtask)) {
            return;
        } else {
            subtaskMap.put(subtask.getId(), subtask);
            updateEpic(subtask.getMyEpicReference());
        }
    }

    @Override
    public void removeTask(long ID) {
        taskMap.remove(ID);
    }

    @Override
    public void removeSubtask(long ID) {
        Optional<Subtask> subtaskInQuestion = Optional.ofNullable(subtaskMap.get(ID));
        Optional<Epic> epicAffected = subtaskInQuestion.isPresent() ? subtaskInQuestion.map(Subtask::getMyEpicReference) : Optional.empty();

        if (epicAffected.isPresent()) {
            epicAffected.get().deleteSubtask(ID);
            updateEpic(epicAffected.get().update());
            subtaskMap.remove(ID);
        }
    }

    @Override
    public void removeEpic(long ID) {
        Epic epicInQuestion = epicMap.get(ID);

        if (epicInQuestion != null) {
            for (Subtask subtask : epicInQuestion.getMySubtasks()) {
                if (subtask.getMyEpicReference().equals(epicInQuestion)) {
                    subtaskMap.remove(subtask.getId());
                }
            }
            epicMap.remove(ID);
        }
    }

    @Override
    public ArrayList<Subtask> extractSubtaskList(long epicID) {
        if (epicMap.get(epicID) != null) {
            return epicMap.get(epicID).getMySubtasks();
        } else return null;
    }

    @Override
    public String toString() {
        String out = "Manager\n";

        for (Task task : taskMap.values()) {
            out += task.toString() + "\n";
        }

        for (Epic epic : epicMap.values()) {
            out += epic.toString() + "\n";
        }

        for (Subtask sub : subtaskMap.values()) {
            out += sub.toString() + "\n";
        }
        return out;
    }
}
