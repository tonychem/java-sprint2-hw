import manager.HistoryManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class TaskManagerMain {
    public static void main(String[] args) {
        //Блок инициализации менеджера и задач
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача №1", "");
        Task task2 = new Task("Задача №2", "", Status.DONE);

        task1.setStartTime(Instant.ofEpochMilli(1_000));
        task1.setDuration(Duration.ofMillis(1000));
        task2.setStartTime(Instant.ofEpochMilli(2000));
        task2.setDuration(Duration.ofMillis(2000));

        Subtask sub1 = new Subtask("Подзадача №1", "");
        Subtask sub2 = new Subtask("Подзадача №2", "", Status.IN_PROGRESS);
        Subtask sub3 = new Subtask("Подзадача №3", "");

        sub1.setStartTime(Instant.ofEpochMilli(1_000_000));
        sub1.setDuration(Duration.ofMillis(1000_000));
        sub2.setStartTime(Instant.ofEpochMilli(2000_000));
        sub2.setDuration(Duration.ofMillis(2000_000));
        sub3.setStartTime(Instant.ofEpochMilli(3000_000));
        sub3.setDuration(Duration.ofMillis(3000_000));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(sub1);
        subtasks.add(sub2);
        subtasks.add(sub3);

        Epic epic1 = new Epic("Эпик1", "3 подзадачи", subtasks);
        Epic epic2 = new Epic("Эпик2", "пустой", new ArrayList<>());

        //Сохранение задач в менеджерах
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);

    }
}
