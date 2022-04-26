package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

class TaskTest {

    @Test
    public void shouldCalculateEndTimeNormalTest() {
        Task t1 = new Task("test1", "", Status.NEW, Instant.ofEpochSecond(180),
                Duration.ofMinutes(5));
        Assertions.assertEquals(Instant.ofEpochSecond(480), t1.getEndTime());
    }

    @Test
    public void shouldReturnNullIfDurationOrStartTimeIsAbsent() {
        Task t1 = new Task("test2", "", Status.NEW, Instant.now(), null);
        Task t2 = new Task("test2", "", Status.NEW, null, Duration.ofMillis(1));
        Task t3 = new Task("test2", "", Status.NEW, null, null);

        Assertions.assertNull(t1.getEndTime());
        Assertions.assertNull(t2.getEndTime());
        Assertions.assertNull(t3.getEndTime());
    }
}