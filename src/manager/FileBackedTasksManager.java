package manager;

import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private String pathToFile;

    public static void main(String[] args) {
        final String outputFilePath = "src/files/output.csv";
        final String inputFilePath = "src/files/input.csv";
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(outputFilePath);
        ArrayList<Subtask> subtasksOfEpic1 = new ArrayList<>();
        subtasksOfEpic1.add(new Subtask("Подзадание 1", "оп1"));
        subtasksOfEpic1.add(new Subtask("Подзадание 2", "оп2", Status.IN_PROGRESS));

        Epic epic1 = new Epic("Эпик1", "2 задачи", subtasksOfEpic1);
        Epic epic2 = new Epic("Эпик2", "Пустой", new ArrayList<>());

        Task t1 = new Task("Задание 1", "оп1");
        Task t2 = new Task("Задание 2", "оп2", Status.DONE);

        fileBackedTasksManager.saveEpic(epic1);
        fileBackedTasksManager.saveEpic(epic2);

        fileBackedTasksManager.saveTask(t1);
        fileBackedTasksManager.saveTask(t2);

        fileBackedTasksManager.getTaskByID(1);
        fileBackedTasksManager.getTaskByID(4);
        System.out.printf("*запись TaskManager в файл %s завершена*%n", outputFilePath);

        FileBackedTasksManager readFromFile = FileBackedTasksManager.loadFromFile(inputFilePath);
        System.out.printf("Восстановленный из файла TaskManager: %n%s%nИ его история:%n%s", readFromFile,
                readFromFile.getHistoryManager().getHistory());
    }

    public FileBackedTasksManager(String pathToFile) {
        super();
        this.pathToFile = pathToFile;
    }


    private void save() {
        StringBuilder toFileString = new StringBuilder("id,type,name,status,description,epic\n");
        File outputFile = new File(pathToFile);

        //собирает все задания текущего состояния менеджера в сортированный список
        List<Task> completeListOfTasks = new ArrayList<>();
        completeListOfTasks.addAll(getAllTasks());
        completeListOfTasks.addAll(getAllEpics());
        completeListOfTasks.addAll(getAllSubtasks());
        completeListOfTasks.sort(Comparator.comparing(Task::getId));

        for (Task task : completeListOfTasks) {
            toFileString.append(writeTask(task) + "\n");
        }

        toFileString.append("\n" + FileBackedTasksManager.writeHistoryManager(getHistoryManager()));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(toFileString.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

    }

    private String writeTask(Task task) {
        String taskFileRecord = String.format("%d,%s,%s,%s,%s,", task.getId(), task.getType(), task.getTitle(),
                task.getStatus(), task.getDescription());

        return task.getType() == TaskType.SUBTASK ? taskFileRecord + ((Subtask) task).getMyEpicReference().getId() :
                taskFileRecord;
    }

    private Task readTask(String fileString) {
        //Разбивает входную строку на массив строк с содержимым
        //0.id-1.type-2.name-3.status-4.description-5.epic
        String[] taskData = fileString.split(",");
        Task taskBuilder;
        TaskType taskType = TaskType.valueOf(taskData[1]);

        if (taskType == TaskType.TASK) {
            taskBuilder = new Task(taskData[2], taskData[4], Status.valueOf(taskData[3]));
        } else if (taskType == TaskType.EPIC) {
            // Прочитанная строка, содержащая эпик, не дает информации про подзадачи,
            // поэтому в конструктор эпика передается пустой список подзадач
            taskBuilder = new Epic(taskData[2], taskData[4], new ArrayList<Subtask>());
        } else {
            // Подзадачи содержат поле со ссылкой на свой эпик, но при чтении строки из файла
            // её эпика не существует, поэтому сохраняем id эпика в эпик-пустышку
            Epic dummyEpic = new Epic(null, null, null);
            taskBuilder = new Subtask(taskData[2], taskData[4], Status.valueOf(taskData[3]));
            dummyEpic.setId(Long.parseLong(taskData[5]));
            ((Subtask) taskBuilder).setMyEpicReference(dummyEpic);
        }

        taskBuilder.setId(Long.parseLong(taskData[0]));

        return taskBuilder;
    }

    static String writeHistoryManager(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        String[] outputArray = new String[history.size()];

        for (int i = 0; i < outputArray.length; i++) {
            outputArray[i] = String.valueOf(history.get(i).getId());
        }

        return String.join(",", outputArray);
    }

    static List<Long> readHistoryManager(String value) {
        List<Long> listToReturn = new ArrayList<>();

        for (String string : value.split(",")) {
            listToReturn.add(Long.parseLong(string));
        }

        return listToReturn;
    }

    static FileBackedTasksManager loadFromFile(String pathToFile) {
        FileBackedTasksManager backUp = new FileBackedTasksManager(pathToFile);
        List<Task> tasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        List<Long> history = null;

        try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
            br.readLine(); //пропускаем header

            while (br.ready()) {
                String currentString = br.readLine();
                //Восстанавливаем состояние менеджера, добавляя задачи в соответствующие списки
                if (!currentString.isBlank()) {
                    Task deserializedTask = backUp.readTask(currentString);

                    if (deserializedTask instanceof Epic) {
                        epics.add((Epic) deserializedTask);
                    } else if (deserializedTask instanceof Subtask) {
                        subtasks.add((Subtask) deserializedTask);
                    } else {
                        tasks.add(deserializedTask);
                    }

                } else {
                    //После чтения пустой строки читаем историю
                    history = FileBackedTasksManager.readHistoryManager(br.readLine());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Поскольку при чтении подзадач из файла им требовалось передавать эпик-заглушку,
        //здесь восстанавливаем связи эпик-подзадача
        for (Subtask subtask : subtasks) {
            for (Epic epic : epics) {
                if (subtask.getMyEpicReference().getId() == epic.getId()) {
                    subtask.setMyEpicReference(epic);
                    epic.putSubtask(subtask);
                }
            }
        }

        //Копирование всех заданий и истории в экземпляр менеджера:
        for (Task task : tasks) {
            backUp.updateTask(task);
        }

        for (Epic epic : epics) {
            backUp.updateEpic(epic);
        }

        for (Subtask subtask : subtasks) {
            backUp.updateSubtask(subtask);
        }

        //Восстанавливаем историю просмотров менджера
        if (history != null) {
            for (Long id : history) {
                backUp.getHistoryManager().add(backUp.getTaskByID(id));
            }
        }

        return backUp;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public void eraseTasks() {
        super.eraseTasks();
        save();
    }

    @Override
    public void eraseEpics() {
        super.eraseEpics();
        save();
    }

    @Override
    public void eraseSubtasks() {
        super.eraseSubtasks();
        save();
    }

    @Override
    public Task getTaskByID(long ID) {
        Task extracted = super.getTaskByID(ID);
        save();
        return extracted;
    }

    @Override
    public void saveTask(Task t) {
        super.saveTask(t);
        save();
    }

    @Override
    public void saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
    }

    @Override
    public void saveSubtask(Subtask sub) {
        super.saveSubtask(sub);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(long ID) {
        super.removeTask(ID);
        save();
    }

    @Override
    public void removeSubtask(long ID) {
        super.removeSubtask(ID);
        save();
    }

    @Override
    public void removeEpic(long ID) {
        super.removeEpic(ID);
        save();
    }

    @Override
    public ArrayList<Subtask> extractSubtaskList(long epicID) {
        return super.extractSubtaskList(epicID);
    }

    @Override
    public void assignSubtaskToEpic(long epicID, long subtaskID) {
        super.assignSubtaskToEpic(epicID, subtaskID);
        save();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
