package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
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
            history.linkLast(task);
        }
    }

    @Override
    //удаляет запись в HashMap, перезатирает ссылки в двусвязном списке
    public void remove(long id) {
        Node nodeToRemove = history.map.remove(id);
        history.removeNode(nodeToRemove);
    }

    class TaskLinkedList<T extends Task> {
        private Node head;
        private Node tail;
        private Map<Long, Node> map;

        public TaskLinkedList() {
            head = null;
            tail = null;
            map = new HashMap<>();
        }

        public void linkLast(T task) {
            Node oldTail = tail;
            Node newTail = new Node(oldTail, task, null);
            map.put(task.getId(), newTail);

            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.setNext(newTail);
            }
            tail = newTail;
        }

        //Метод перезаписывает ссылки внутри предыдущего и следующего узлов друг на друга
        public void removeNode(Node node) {
            if (node != null) {
                Node previousNode = node.getPrevious();
                Node nextNode = node.getNext();

                if (previousNode == null & nextNode == null) {
                    head = null;
                    tail = null;
                } else if (previousNode == null) {
                    head = nextNode;
                    nextNode.setPrevious(null);
                } else if (nextNode == null) {
                    tail = previousNode;
                    previousNode.setNext(null);
                } else {
                    previousNode.setNext(nextNode);
                    nextNode.setPrevious(previousNode);
                }
            }
        }

        public ArrayList<Task> getTasks() {
            //Итератор ссылается на текущий узел и собирает data в ArrayList
            Node iterator = head;
            ArrayList<Task> historyList = new ArrayList<>();

            while (iterator != null) {
                historyList.add(iterator.getData());
                iterator = iterator.getNext();
            }

            return historyList;
        }
    }
}

class Node {
    private Node previous;
    private Node next;
    private Task data;

    public Node(Node previous, Task data, Node next) {
        this.previous = previous;
        this.next = next;
        this.data = data;
    }

    public Node getPrevious() {
        return previous;
    }

    public Node getNext() {
        return next;
    }

    public Task getData() {
        return data;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setData(Task data) {
        this.data = data;
    }
}
