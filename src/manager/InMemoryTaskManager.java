package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Long, Task> taskMap;
    private final HashMap<Long, Epic> epicMap;
    private final HashMap<Long, Subtask> subtaskMap;
    private final TreeSet<Task> prioritizedTaskSet = new TreeSet(new TaskTemporalComparator());
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
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTaskSet;
    }

    @Override
    public boolean hasIntersection(Task t) {
        Instant taskStartTime = t.getStartTime();
        if (taskStartTime == null) {
            return false;
        }
        //Проверяем, что в стриме нет заданий, интервал выполнения которых [время начала, время конца] включает startTime;
        return prioritizedTaskSet.stream().filter(x -> x.getEndTime() != null).anyMatch(x -> x.getStartTime().isBefore(taskStartTime) && x.getEndTime().isAfter(taskStartTime));
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
        prioritizedTaskSet.removeIf(x -> x.getType() == TaskType.TASK);
    }

    @Override
    public void eraseEpics() {
        epicMap.clear();
        prioritizedTaskSet.removeIf(x -> x.getType() == TaskType.EPIC);
        eraseSubtasks();
    }

    @Override
    public void eraseSubtasks() {
        subtaskMap.clear();
        prioritizedTaskSet.removeIf(x -> x.getType() == TaskType.SUBTASK);
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
        if (!hasIntersection(t)) {
            t.setId(assignID);
            taskMap.put(assignID, t);
            prioritizedTaskSet.add(t);
            assignID++;
        }
    }

    @Override
    public void saveEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("попытка записать значение null");
        }
        if (!hasIntersection(epic)) {
            for (Subtask sub : epic.getMySubtasks()) {
                if (!subtaskMap.containsValue(sub)) {
                    sub.setId(assignID);
                    sub.setMyEpicReference(epic);
                    prioritizedTaskSet.add(sub);
                    subtaskMap.put(assignID, sub);
                    assignID++;
                }
            }
            epic.setId(assignID);

            Epic updated = epic.update();
            epicMap.put(assignID, updated);
            prioritizedTaskSet.add(updated);
            assignID++;
        }
    }

    @Override
    public void saveSubtask(Subtask sub) {
        if (sub == null) {
            throw new IllegalArgumentException("попытка записать значение null");
        }

        if (sub.getMyEpicReference() == null || !epicMap.containsKey(sub.getMyEpicReference().getId())) {
            throw new IllegalArgumentException("попытка сохранить подзадачу без эпика");
        }

        //Найти эпик с соответствующим id (костыль для эпика-заглушки)
        Optional<Epic> epicInEpicMap = epicMap.values().parallelStream().filter(x -> x.getId() == sub.getMyEpicReference().getId()).findAny();
        Epic epicReference = null;

        if (epicInEpicMap.isPresent() && sub.getMyEpicReference().getTitle() == null && sub.getMyEpicReference().getDescription() == null) {
            epicReference = epicInEpicMap.get();
        }

        if (!hasIntersection(sub) && !subtaskMap.containsValue(sub)) {
            if (epicReference != null) {
                sub.setMyEpicReference(epicReference);
                epicReference.putSubtask(sub);
            }
            sub.setId(assignID);
            subtaskMap.put(assignID, sub);
            prioritizedTaskSet.add(sub);
            assignID++;
            updateEpic(sub.getMyEpicReference().update());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || task.getId() == 0 || taskMap.containsValue(task)) {
            return;
        } else if (!hasIntersection(task)) {
            prioritizedTaskSet.removeIf(x -> x.getId() == task.getId());
            prioritizedTaskSet.add(task);
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || epic.getId() == 0 || epicMap.containsValue(epic)) {
            return;
        } else if (!hasIntersection(epic)) {
            Epic epicUpdated = epic.update();
            prioritizedTaskSet.removeIf(x -> x.getId() == epic.getId());
            prioritizedTaskSet.add(epicUpdated);
            epicMap.put(epic.getId(), epicUpdated);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getId() == 0 || subtaskMap.containsValue(subtask)) {
            return;
        } else if (!hasIntersection(subtask)) {
            subtaskMap.put(subtask.getId(), subtask);
            prioritizedTaskSet.removeIf(x -> x.getId() == subtask.getId());
            prioritizedTaskSet.add(subtask);
            updateEpic(subtask.getMyEpicReference());
        }
    }

    @Override
    public void removeTask(long ID) {
        taskMap.remove(ID);
        prioritizedTaskSet.removeIf(x -> x.getId() == ID);
    }

    @Override
    public void removeSubtask(long ID) {
        Optional<Subtask> subtaskInQuestion = Optional.ofNullable(subtaskMap.get(ID));
        Optional<Epic> epicAffected = subtaskInQuestion.isPresent() ? subtaskInQuestion.map(Subtask::getMyEpicReference) : Optional.empty();

        if (epicAffected.isPresent()) {
            epicAffected.get().deleteSubtask(ID);
            updateEpic(epicAffected.get().update());
            subtaskMap.remove(ID);
            prioritizedTaskSet.removeIf(x -> x.getId() == ID);
        }
    }

    @Override
    public void removeEpic(long ID) {
        Epic epicInQuestion = epicMap.get(ID);

        if (epicInQuestion != null) {
            for (Subtask subtask : epicInQuestion.getMySubtasks()) {
                if (subtask.getMyEpicReference().equals(epicInQuestion)) {
                    subtaskMap.remove(subtask.getId());
                    prioritizedTaskSet.removeIf(x -> x.getId() == subtask.getId());
                }
            }
            epicMap.remove(ID);
            prioritizedTaskSet.removeIf(x -> x.getId() == ID);
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

class TaskTemporalComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        Instant task1StartTime = task1.getStartTime();
        Instant task2StartTime = task2.getStartTime();

        if (task1StartTime == null && task2StartTime == null) {
            return 1;
        }

        if (task1StartTime != null && task2StartTime == null) {
            return -1;
        }

        if (task1StartTime == null) {
            return 1;
        }
        return task1StartTime.compareTo(task2StartTime);
    }
}
