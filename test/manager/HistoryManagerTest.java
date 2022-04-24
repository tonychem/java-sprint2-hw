package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

class HistoryManagerTest {
    protected HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void addTaskTest() {
        Task task1 = new Task("task1", "");
        Task task2 = new Task("task2", "");
        task1.setId(1);
        task1.setId(2);
        historyManager.add(task1);

        Assertions.assertEquals(1, historyManager.getHistory().size());

        historyManager.add(task2);
        Assertions.assertEquals(2, historyManager.getHistory().size());

        historyManager.add(task2);
        historyManager.add(task1);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void removeExistingHistoryElementTest() {
        Task t1 = new Task("task1", "");
        t1.setId(1);
        historyManager.add(t1);
        historyManager.remove(1);
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void removeNonExistingElementTest() {
        Task t1 = new Task("task1", "");
        t1.setId(1);
        historyManager.add(t1);
        Assertions.assertAll(() -> historyManager.remove(5));
    }

    @Test
    public void removeOnEmptyHistoryTest() {
        Assertions.assertAll(() -> historyManager.remove(1));
    }

    @Test
    public void removeIndexTest() {
        Task[] taskArray = new Task[5];

        for (int i = 0; i < 5; i++) {
            taskArray[i] = new Task(String.format("t%d", i + 1), "");
        }

        for (int i = 0; i < 5; i++) {
            taskArray[i].setId(i + 1);
            historyManager.add(taskArray[i]);
        }
        //удаление 1-ого элемента; проверить, что история сместилась после удаления на 1.
        historyManager.remove(1);

        Assertions.assertEquals(4, historyManager.getHistory().size());
        Assertions.assertEquals(taskArray[1], historyManager.getHistory().get(0));

        //удаление из середины оставшегося списка, проверка соседних от 3-его элементов
        historyManager.remove(3);
        Assertions.assertEquals(taskArray[1], historyManager.getHistory().get(0));
        Assertions.assertEquals(taskArray[3], historyManager.getHistory().get(1));

        //удаление последнего элемента
        historyManager.remove(5);
        Assertions.assertEquals(taskArray[3], historyManager.getHistory().get(1));
    }
}