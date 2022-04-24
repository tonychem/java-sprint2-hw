package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    // Проверяет, что эпик сохраняется в менеджере с ожидаемым статусом
    @Test
    public void epicStatusTest() {
        ArrayList<Subtask> subtaskNew = new ArrayList<>();
        ArrayList<Subtask> subtaskInProgress = new ArrayList<>();
        ArrayList<Subtask> subtaskDone = new ArrayList<>();
        ArrayList<Subtask> subtaskNewAndDone = new ArrayList<>();
        ArrayList<Subtask> subtaskNewAndInProgress = new ArrayList<>();
        ArrayList<Subtask> subtaskEmpty = new ArrayList<>();

        subtaskNew.add(new Subtask("1", "", Status.NEW));
        subtaskInProgress.add(new Subtask("1", "", Status.IN_PROGRESS));
        subtaskDone.add(new Subtask("1", "", Status.DONE));
        subtaskNewAndDone.add(new Subtask("1", "", Status.NEW));
        subtaskNewAndDone.add(new Subtask("2", "", Status.DONE));
        subtaskNewAndInProgress.add(new Subtask("1", "", Status.NEW));
        subtaskNewAndInProgress.add(new Subtask("2", "", Status.IN_PROGRESS));

        Epic allNew = new Epic("ALL_NEW", "", subtaskNew);
        Epic allInProgress = new Epic("ALL_IN_PROGRESS", "", subtaskInProgress);
        Epic allDone = new Epic("ALL_DONE", "", subtaskDone);
        Epic mixedNewAndDone = new Epic("NEW_AND_DONE", "", subtaskNewAndDone);
        Epic mixedNewAndInProgress = new Epic("NEW_AND_IN_PROGRESS", "", subtaskNewAndInProgress);
        Epic empty = new Epic("EMPTY", "", subtaskEmpty);

        manager.saveEpic(allNew);
        manager.saveEpic(allInProgress);
        manager.saveEpic(allDone);
        manager.saveEpic(mixedNewAndDone);
        manager.saveEpic(mixedNewAndInProgress);
        manager.saveEpic(empty);

        for (Epic e : manager.getAllEpics()) {
            Status expected = e.getStatus();
            String epicTitle = e.getTitle();

            if (epicTitle.equals("ALL_NEW")) {
                Assertions.assertEquals(Status.NEW, expected);
            } else if (epicTitle.equals("ALL_IN_PROGRESS")) {
                Assertions.assertEquals(Status.IN_PROGRESS, expected);
            } else if (epicTitle.equals("ALL_DONE")) {
                Assertions.assertEquals(Status.DONE, expected);
            } else if (epicTitle.equals("NEW_AND_DONE")) {
                Assertions.assertEquals(Status.IN_PROGRESS, expected);
            } else if (epicTitle.equals("NEW_AND_IN_PROGRESS")) {
                Assertions.assertEquals(Status.IN_PROGRESS, expected);
            } else {
                Assertions.assertEquals(Status.NEW, expected);
            }
        }
    }

    // Проверяет, что менеджер устанавливает зависимость между эпиком и сабтасками после сохранения
    @Test
    public void shouldSubtasksHaveEpic() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.IN_PROGRESS));
        subtasks.add(new Subtask("2", ""));

        Epic epic = new Epic("epic", "", subtasks);
        Subtask withoutEpic = new Subtask("w/o Epic", "");

        manager.saveEpic(epic);

        for (Subtask subtask : manager.getAllSubtasks()) {
            Assertions.assertNotNull(subtask.getMyEpicReference());
        }

        //нельзя сохранять подзадания, которые не были проиндексированы в TaskManager-e
        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.saveSubtask(withoutEpic));
    }

    //нормальный тест saveTask
    @Test
    public void saveTaskNormalTest() {
        Task task1 = new Task("1", "");
        Task task2 = new Task("2", "");

        manager.saveTask(task1);
        Assertions.assertEquals(1, manager.getAllTasks().size());

        manager.saveTask(task2);
        Assertions.assertEquals(2, manager.getAllTasks().size());

        manager.eraseTasks();
        Assertions.assertEquals(0, manager.getAllTasks().size());
    }

    //сохранение null значений в менеджере
    @Test
    public void saveTasksNullTest() {
        Task task1 = null;
        Epic epic1 = null;
        Subtask subtask1 = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.saveTask(task1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.saveSubtask(subtask1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.saveEpic(epic1));
    }

    //Проверяет корректное удаление всех списков заданий из менеджера
    @Test
    public void eraseTasksTest() {
        Task t1 = new Task("1", "");
        Task t2 = new Task("2", "");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", ""));
        subtasks.add(new Subtask("2", ""));
        Epic epicWithSubtasks = new Epic("epic1", "", subtasks);
        Epic epicWithOutSubtasks = new Epic("epicEMPTY", "", new ArrayList<>());

        Assertions.assertAll("Удаление списка заданий из пустого менеджера:", () -> manager.eraseTasks(),
                () -> manager.eraseEpics(), () -> manager.eraseSubtasks());

        manager.saveTask(t1);
        manager.saveTask(t2);
        Assertions.assertEquals(2, manager.getAllTasks().size());

        manager.eraseTasks();
        Assertions.assertEquals(0, manager.getAllTasks().size());

        manager.saveEpic(epicWithOutSubtasks);
        Assertions.assertEquals(1, manager.getAllEpics().size());
        manager.eraseEpics();
        Assertions.assertEquals(0, manager.getAllEpics().size());

        manager.saveEpic(epicWithSubtasks);
        Assertions.assertEquals(2, manager.getAllSubtasks().size());

        manager.eraseSubtasks();
        Assertions.assertEquals(0, manager.getAllSubtasks().size());
        Assertions.assertEquals(1, manager.getAllEpics().size());
    }

    // Нормальный тест извлечение заданий из менеджера по ID
    @Test
    public void getTaskByIdNormalTest() {
        Task t1 = new Task("1", "");
        Subtask subtask = new Subtask("2", "");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        Epic e1 = new Epic("3", "", subtasks);

        manager.saveTask(t1);
        manager.saveEpic(e1);

        for (int i = 1; i <= 3; i++) {
            Task retrieved = manager.getTaskByID(i);
            if (i == 1) {
                Assertions.assertEquals(t1, retrieved);
            } else if (i == 2) {
                Assertions.assertEquals(subtask, retrieved);
            } else {
                Assertions.assertEquals(e1, retrieved);
            }
        }
    }

    //Получение несуществующих заданий из менеджера
    @ParameterizedTest
    @ValueSource(longs = {0, 5, 10})
    public void getTaskByIdNonExistentTasksTest(long id) {
        Assertions.assertNull(manager.getTaskByID(id));
    }

    @Test
    public void removeTasksNormalTest() {
        Task task1 = new Task("Task1", "id1");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("Subtask1", "id2"));
        subtasks.add(new Subtask("Subtask2", "id3"));
        Epic epicWithTasks = new Epic("EpicWithTasks", "id4", subtasks);
        Epic epicWithOutSubtasks = new Epic("Epic without subtasks", "id5", new ArrayList<>());

        manager.saveTask(task1);
        manager.saveEpic(epicWithTasks);
        manager.saveEpic(epicWithOutSubtasks);

        Assertions.assertEquals(1, manager.getAllTasks().size());
        manager.removeTask(1);
        Assertions.assertEquals(0, manager.getAllTasks().size());

        manager.removeSubtask(2);
        Assertions.assertEquals(1, manager.getAllSubtasks().size());
        Assertions.assertEquals(1, manager.extractSubtaskList(4).size());

        manager.removeEpic(5);
        Assertions.assertEquals(1, manager.getAllEpics().size());

        //удаление эпика с 1 оставшейся подзадачей
        manager.removeEpic(4);
        Assertions.assertEquals(0, manager.getAllSubtasks().size());
        Assertions.assertEquals(0, manager.getAllEpics().size());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 5, 10})
    public void removeTasksIrregularTest(long id) {
        Assertions.assertAll("No exceptions expected", () -> manager.removeEpic(id),
                () -> manager.removeTask(id), () -> manager.removeSubtask(id));
    }
}
