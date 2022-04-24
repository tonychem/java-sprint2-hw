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


}