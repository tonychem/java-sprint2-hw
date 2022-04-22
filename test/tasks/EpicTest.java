package tasks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void shouldBeNewIfSubtaskListEmpty() {
        Status expected = Status.NEW;
        Epic epicTest = new Epic("","", new ArrayList<>()).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeNewIfSubtasksAreNew() {
        Status expected = Status.NEW;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.NEW));
        subtasks.add(new Subtask("2", "", Status.NEW));
        subtasks.add(new Subtask("3", "", Status.NEW));

        Epic epicTest = new Epic("","", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeDoneIfSubtasksAreDone() {
        Status expected = Status.DONE;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.DONE));
        subtasks.add(new Subtask("2", "", Status.DONE));
        subtasks.add(new Subtask("3", "", Status.DONE));

        Epic epicTest = new Epic("","", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreNewAndDone() {
        Status expected = Status.IN_PROGRESS;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.DONE));
        subtasks.add(new Subtask("2", "", Status.DONE));
        subtasks.add(new Subtask("3", "", Status.NEW));

        Epic epicTest = new Epic("","", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreInProgress() {
        Status expected = Status.IN_PROGRESS;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.IN_PROGRESS));
        subtasks.add(new Subtask("2", "", Status.IN_PROGRESS));
        subtasks.add(new Subtask("3", "", Status.IN_PROGRESS));

        Epic epicTest = new Epic("","", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

}