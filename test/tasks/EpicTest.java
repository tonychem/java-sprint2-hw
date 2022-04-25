package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void shouldBeNewIfSubtaskListEmpty() {
        Status expected = Status.NEW;
        Epic epicTest = new Epic("", "", new ArrayList<>()).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeNewIfSubtasksAreNew() {
        Status expected = Status.NEW;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.NEW));
        subtasks.add(new Subtask("2", "", Status.NEW));
        subtasks.add(new Subtask("3", "", Status.NEW));

        Epic epicTest = new Epic("", "", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeDoneIfSubtasksAreDone() {
        Status expected = Status.DONE;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.DONE));
        subtasks.add(new Subtask("2", "", Status.DONE));
        subtasks.add(new Subtask("3", "", Status.DONE));

        Epic epicTest = new Epic("", "", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreNewAndDone() {
        Status expected = Status.IN_PROGRESS;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.DONE));
        subtasks.add(new Subtask("2", "", Status.DONE));
        subtasks.add(new Subtask("3", "", Status.NEW));

        Epic epicTest = new Epic("", "", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreInProgress() {
        Status expected = Status.IN_PROGRESS;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("1", "", Status.IN_PROGRESS));
        subtasks.add(new Subtask("2", "", Status.IN_PROGRESS));
        subtasks.add(new Subtask("3", "", Status.IN_PROGRESS));

        Epic epicTest = new Epic("", "", subtasks).update();
        assertEquals(expected, epicTest.getStatus());
    }

    @Test
    public void shouldCalculateStartTimeEndTimeAndDurationNormalTest() {
        Subtask subtask1 = new Subtask("sub1", "", Status.NEW, Instant.ofEpochSecond(60),
                Duration.ofSeconds(60));
        Subtask subtask2 = new Subtask("sub1", "", Status.NEW, Instant.ofEpochSecond(0),
                Duration.ofSeconds(600));
        Subtask subtask3 = new Subtask("sub1", "", Status.NEW, Instant.ofEpochSecond(601),
                Duration.ofSeconds(800));

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        subtasks.add(subtask3);

        Epic epic = new Epic("epic", "", subtasks).update(); // начинается в 0 эпох. секунд, заканчивается в 1401

        Assertions.assertEquals(Instant.EPOCH, epic.getStartTime());
        Assertions.assertEquals(Instant.ofEpochSecond(1401), epic.getEndTime());
        Assertions.assertEquals(Duration.ofSeconds(1401), epic.getDuration());
    }

    @Test
    public void shouldReturnNullIfEmptyList() {
        Epic e1 = new Epic("e1", "empty", new ArrayList<>());

        Assertions.assertNull(e1.getStartTime());
        Assertions.assertNull(e1.getDuration());
        Assertions.assertNull(e1.getEndTime());
    }

    @Test
    public void shouldReturnNullIfNoDurationGiven() {
        Subtask subtask1 = new Subtask("sub1", "", Status.NEW, Instant.EPOCH,
                null);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);

        Epic epic = new Epic("e", "", subtasks).update();
        Assertions.assertNull(epic.getEndTime());
        Assertions.assertNull(epic.getDuration());
        Assertions.assertEquals(Instant.EPOCH, epic.getStartTime());
    }

    @Test
    public void shouldCalculateEndTimeIfSomeSubtasksProvideNoData() {
        Subtask subtask1 = new Subtask("sub1", "", Status.NEW, Instant.EPOCH,
                Duration.ofSeconds(60)); //считается - начало в 0 э.с.
        Subtask subtask2 = new Subtask("sub1", "", Status.NEW, null,
                Duration.ofSeconds(600)); // должно быть проигнорировано
        Subtask subtask3 = new Subtask("sub1", "", Status.NEW, Instant.ofEpochSecond(601),
                null); // должно быть проигнорировано
        Subtask subtask4 = new Subtask("sub1", "", Status.NEW, Instant.ofEpochSecond(30),
                Duration.ofSeconds(90)); //считается - конец в 120 э.с.

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        subtasks.add(subtask3);
        subtasks.add(subtask4);
        
        Epic epic = new Epic("epic", "", subtasks).update();

        Assertions.assertEquals(Instant.ofEpochSecond(120), epic.getEndTime());
    }
}