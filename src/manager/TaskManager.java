package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    void saveTask(Task t);
    void saveEpic(Epic epic);
    void saveSubtask(Subtask sub);
    void eraseTasks();
    void eraseEpics();
    void eraseSubtasks();
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void removeTask(long ID);
    void removeEpic(long ID);
    void removeSubtask(long ID);
    Task getTaskByID(long ID);
    ArrayList<Subtask> extractSubtaskList(long epicID);
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Task> getAllTasks();
    HistoryManager getHistoryManager();
}
