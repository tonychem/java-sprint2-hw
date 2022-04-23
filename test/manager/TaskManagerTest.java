package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.util.ArrayList;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

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

        for(Epic e : manager.getAllEpics()) {
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
}
