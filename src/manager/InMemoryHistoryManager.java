package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_VIEW = 10;
    private final TaskLinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new TaskLinkedList<>();
    }

    public List<Task> getHistory() {
        return history.getTasks();
    }

    public void add(Task task) {
        if (task != null) {
            //если поставляется существующий в HashMap объект, то он только удаляется из двусвязного списка
            if (history.map.containsKey(task.getId())) {
                history.removeNode(history.map.get(task.getId()));
            }
            if (history.size() == MAX_HISTORY_VIEW) {
                remove(history.head.data.getId());
            }
            history.linkLast(task);
        }
    }

    @Override
    //удаляет запись в HashMap, перезатирает ссылки в двусвязном списке
    public void remove(long id) {
        Node nodeToRemove = history.map.remove(id);
        history.removeNode(nodeToRemove);
        history.size--;
    }

    class TaskLinkedList<T extends Task> {
        private Node head;
        private Node tail;
        private Map<Long, Node> map;
        private int size;

        public TaskLinkedList() {
            head = null;
            tail = null;
            map = new HashMap<>();
            size = 0;
        }

        public void linkLast(T task) {
            Node oldTail = tail;
            Node newTail = new Node(oldTail, task, null);
            map.put(task.getId(), newTail);

            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.next = newTail;
            }
            tail = newTail;
            size++;
        }

        public int size() {
            return size;
        }

        //Метод перезаписывает ссылки внутри предыдущего и следующего узлов друг на друга
        public void removeNode(Node node) {
            if (node != null) {
                Node previousNode = node.previous;
                Node nextNode = node.next;

                if (previousNode == null & nextNode == null) {
                    head = null;
                    tail = null;
                } else if (previousNode == null) {
                    head = nextNode;
                    nextNode.previous = null;
                } else if (nextNode == null) {
                    tail = previousNode;
                    previousNode.next = null;
                } else {
                    previousNode.next = nextNode;
                    nextNode.previous = previousNode;
                }

            }
        }

        public ArrayList<Task> getTasks() {
            //Итератор ссылается на текущий узел и собирает data в ArrayList
            Node iterator = head;
            ArrayList<Task> historyList = new ArrayList<>();

            while (iterator != null) {
                historyList.add(iterator.data);
                iterator = iterator.next;
            }

            return historyList;
        }
    }
}

class Node {
    Node previous;
    Node next;
    Task data;

    public Node(Node previous, Task data, Node next) {
        this.previous = previous;
        this.next = next;
        this.data = data;
    }
}
