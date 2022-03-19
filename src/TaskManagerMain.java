import manager.*;
import tasks.*;

import java.util.ArrayList;

public class TaskManagerMain {
    public static void main(String[] args) {
        //Блок инициализации менеджера и задач
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        TaskManager newMgr = Managers.getDefault();
        Task task1 = new Task("Задача №1", "");
        Task task2 = new Task("Задача №2", "", Status.DONE);
        Task task3 = new Task("Задача №3", "");
        Task task4 = new Task("Задача №4", "");
        Task task5 = new Task("Задача №5", "");
        Task task6 = new Task("Задача №6", "");
        Task task7 = new Task("Задача №7", "");
        Task[] taskArray = {task1, task2, task3, task4, task5, task6, task7};

        Subtask sub1 = new Subtask("Подзадача №1", "");
        Subtask sub2 = new Subtask("Подзадача №1", "", Status.IN_PROGRESS);
        Subtask sub3 = new Subtask("Подзадача №1", "");

        ArrayList<Subtask> toEpic1 = new ArrayList<>();
        toEpic1.add(sub1);
        toEpic1.add(sub2);

        ArrayList<Subtask> toEpic2 = new ArrayList<>();
        toEpic2.add(sub3);

        Epic epic1 = new Epic("Эпик1", "", toEpic1);
        Epic epic2 = new Epic("Эпик2", "", toEpic2);
        Epic epic3 = new Epic("Эпик3", "", new ArrayList<>());
        Epic epic4 = new Epic("Эпик4", "", new ArrayList<>());
        Epic[] epicArray = {epic1, epic2, epic3, epic4};

        //Сохранение задач в менеджерах
        for (int i = 0; i < taskArray.length; i++) {
            taskManager.saveTask(taskArray[i]);
            newMgr.saveTask(taskArray[i]);
        }
        for (int i = 0; i < epicArray.length; i++) {
            taskManager.saveEpic(epicArray[i]);
            newMgr.saveEpic(epicArray[i]);
        }

        System.out.println("Manager state before: " + taskManager);
        //тесты
        for (int i = 1; i < 16; i++) {
            taskManager.getTaskByID(i);
            newMgr.getTaskByID(i);
        }

        HistoryManager historyManager = taskManager.getHistoryManager();
        System.out.println(historyManager.getHistory());


    }
}
