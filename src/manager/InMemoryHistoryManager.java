package manager;

import tasks.Task;

import java.util.*;

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
            if (history.size() == MAX_HISTORY_VIEW) {
                remove(history.head.data.getId());
            }
            history.linkLast(task);
        }
    }

    @Override
    public void remove(long id) {
        Node<Task> nodeToRemove = history.map.remove(id);
        history.removeNode(nodeToRemove);
        history.size--;
    }

    class TaskLinkedList<T extends Task> {
         //TODO add implementation of linked list
        //TODO linkedlist works fine but why?
        private Node<T> head;
        private Node<T> tail;
        private Map<Long, Node<T>> map;
        private int size;

        public TaskLinkedList() {
            head = null;
            tail = null;
            map = new HashMap<>();
            size = 0;
        }

        public void linkLast(T task) {
            Node<T> oldTail = tail;
            Node<T> newTail = new Node(oldTail, task, null);
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

        public void removeNode(Node<T> n) {
            if (n != null) {
                Node<T> previousNode = n.previous;
                Node<T> nextNode = n.next;

                if(previousNode == null) {
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
            Node<?> iterator = head;
            ArrayList<Task> historyList = new ArrayList<>();

            while(iterator != null) {
                historyList.add(iterator.data);
                iterator = iterator.next;
            }

            return historyList;
        }
    }
}

class Node<T extends Task> {
    Node previous;
    Node next;
    T data;

    public Node(Node previous, T data, Node next) {
        this.previous = previous;
        this.next = next;
        this.data = data;
    }
    //TODO check if it is enough
}
