import manager.HistoryManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;

public class TaskManagerMain {
    public static void main(String[] args) {
        //Блок инициализации менеджера и задач
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача №1", "");
        Task task2 = new Task("Задача №2", "", Status.DONE);

        Subtask sub1 = new Subtask("Подзадача №1", "");
        Subtask sub2 = new Subtask("Подзадача №2", "", Status.IN_PROGRESS);
        Subtask sub3 = new Subtask("Подзадача №3", "");

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

        HistoryManager historyManager = taskManager.getHistoryManager();
        System.out.println(taskManager);

        //вызов задач в произвольной последовательности с повторами [3 -> 1 -> 3 -> 7 -> 2 -> 1 -> 7 -> 6]
        taskManager.getTaskByID(3);
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(3);
        taskManager.getTaskByID(7);
        taskManager.getTaskByID(2);
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(7);
        taskManager.getTaskByID(6);

        //historyManager : [ 3 -> 2 -> 1 -> 7 -> 6 ]
        System.out.println("Вызов задач в произвольной последовательности с повторами: \n" + historyManager.getHistory());

        //удаление задачи посередине (id = 1)
        historyManager.remove(1);
        System.out.println("Удаление задачи с id = 1: \n" + historyManager.getHistory());

        //удаление эпика с 3-мя подзадачами (id = 6)
        historyManager.remove(6);
        System.out.println("Удаление эпика с 3-мя подзадачами (id = 6): \n" + historyManager.getHistory());
    }
}
