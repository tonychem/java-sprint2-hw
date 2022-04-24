package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.util.ArrayList;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    Path pathToOutputFileTest = Path.of(System.getProperty("user.dir"));

    @BeforeEach
    public void init() {
        Path pathToOutputFilePlainTest = pathToOutputFileTest.resolveSibling("test\\filesTest\\outputPlainTestsFlush.csv");
        manager = new FileBackedTasksManager(pathToOutputFilePlainTest.toString());
    }

    //Загрузка из пустого файла
    @Test
    public void emptyInputFileTest() {
        Path pathToInputFileEmptyTest = pathToOutputFileTest.resolveSibling("test\\filesTest\\inputEmptyTest.csv");
        Assertions.assertAll(() -> {
            FileBackedTasksManager.loadFromFile(pathToInputFileEmptyTest.toFile());
        });

        manager = FileBackedTasksManager
                .loadFromFile(pathToInputFileEmptyTest.toFile());

        Assertions.assertNotNull(manager);
        Assertions.assertNotNull(manager.getAllTasks());
        Assertions.assertNotNull(manager.getAllSubtasks());
        Assertions.assertNotNull(manager.getAllEpics());

        Assertions.assertEquals(0, manager.getAllTasks().size());
        Assertions.assertEquals(0, manager.getAllSubtasks().size());
        Assertions.assertEquals(0, manager.getAllEpics().size());
    }

    //Загрузка менеджера из файла без истории
    @Test
    public void emptyHistoryTest() {
        Path pathToInputFileEmptyHistoryTest = pathToOutputFileTest.resolveSibling("test\\filesTest\\emptyHistoryTest.csv");
        manager = FileBackedTasksManager.loadFromFile(pathToInputFileEmptyHistoryTest.toFile());
        Assertions.assertEquals(0, manager.getHistoryManager().getHistory().size());
    }

    //Создание менеджера и загрузка его копии из файла; проверка идентичности их элементов
    @Test
    public void saveAndLoadTest() {
        Path pathToOutputFileSaveTest = pathToOutputFileTest.resolveSibling("test\\filesTest\\outputSaveTest.csv");
        manager = new FileBackedTasksManager(pathToOutputFileSaveTest.toString());

        Task t1 = new Task("task1", "id1");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("sub1", "id2"));
        subtasks.add(new Subtask("sub1", "id3"));
        Epic epicFull = new Epic("epic1", "id4", subtasks);
        Epic epicWithoutSubtasks = new Epic("epic", "withoutSubtasks", new ArrayList<>());

        manager.saveEpic(epicFull);
        manager.saveTask(t1);
        manager.saveEpic(epicWithoutSubtasks);

        manager.getTaskByID(1);

        FileBackedTasksManager managerToCompare = FileBackedTasksManager.loadFromFile(pathToOutputFileSaveTest.toFile());

        Assertions.assertEquals(manager.getTaskByID(3), managerToCompare.getTaskByID(3));
        Assertions.assertEquals(manager.getAllEpics(), managerToCompare.getAllEpics());
        Assertions.assertEquals(manager.getHistoryManager().getHistory(), managerToCompare.getHistoryManager().getHistory());
    }
}