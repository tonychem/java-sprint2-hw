package manager;

import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void init() {
        Path pathToOutputFileTest = Path.of(System.getProperty("user.dir"))
                .resolveSibling("test\\filesTest\\outputTest.csv");
        manager = new FileBackedTasksManager(pathToOutputFileTest.toString());
    }
}