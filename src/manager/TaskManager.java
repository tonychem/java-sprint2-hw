package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    //сохранение вех задач
    void saveTask(Task t);
    void saveEpic(Epic epic);
    void saveSubtask(Subtask sub);

    //удаление всех задач
    void eraseTasks();
    void eraseEpics();
    void eraseSubtasks();

    //обновление задач
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    //удаление по ID
    void removeTask(long ID);
    void removeEpic(long ID);
    void removeSubtask(long ID);

    //возвращать task по ID
    Task getTaskByID(long ID);

    //извлечение списка сабтаксов
    ArrayList<Subtask> extractSubtaskList(long epicID);

    //извлечение списков всех задач
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Task> getAllTasks();
}
