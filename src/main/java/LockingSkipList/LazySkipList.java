package LockingSkipList;

import java.util.Random;

public class LazySkipList {
    static final int MAX_LEVEL = 5;
    final Node<String> head = new Node<>(Integer.MIN_VALUE);
    final Node<String> tail = new Node<>(Integer.MAX_VALUE);
    private final Random random = new Random();
    static final double P = 0.5;

    public LazySkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = tail;
        }
    }

    int find(String name, Node<String>[] predecessors, Node<String>[] successors) {
        int key = name.hashCode();
        int lFound = -1;
        Node<String> pred = head;
        for (int level = MAX_LEVEL; level >= 0; level--) {
            Node<String> curr = pred.next[level];
            while (key > curr.key) {
                pred = curr; curr = pred.next[level];
            }
            if (lFound == -1 && key == curr.key) {
                lFound = level;
            }
            predecessors[level] = pred;
            successors[level] = curr;
        }
        return lFound;
    }

    boolean add(String name) {
        int topLevel = randomLevel();
        Node<String>[] predecessors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        Node<String>[] successors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            int lFound = find(name, predecessors, successors);
            if (lFound != -1) {
                Node<String> nodeFound = successors[lFound];
                if (!nodeFound.markedForRemoval) {
                    while (!nodeFound.fullyLinked) {}
                    return false;
                }
                continue;
            }
            int highestLocked = -1;
            try {
                Node<String> pred;
                Node<String> succ;
                boolean valid = true;
                for (int level = 0; valid && (level <= topLevel); level++) {
                    pred = predecessors[level];
                    succ = successors[level];
                    pred.lock.lock();
                    highestLocked = level;
                    valid = !pred.markedForRemoval && !succ.markedForRemoval && pred.next[level]==succ;
                }
                if (!valid) continue;
                Node<String> newNode = new Node<>(name, topLevel);
                for (int level = 0; level <= topLevel; level++) {
                    newNode.next[level] = successors[level];
                }
                for (int level = 0; level <= topLevel; level++) {
                    predecessors[level].next[level] = newNode;
                }
                newNode.fullyLinked = true; // successful add linearization point
                return true;
            } finally {
                for (int level = 0; level <= highestLocked; level++) {
                    predecessors[level].unlock();
                }
            }
        }
    }

    boolean remove(String name) {
        Node<String> victim = null; boolean isMarked = false; int topLevel = -1;
        Node<String>[] predecessors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        Node<String>[] successors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            int lFound = find(name, predecessors, successors);
            if (lFound != -1) victim = successors[lFound];
            if (isMarked | (lFound != -1 && (victim.fullyLinked && victim.topLevel == lFound && !victim.markedForRemoval))) {
                if (!isMarked) {
                    topLevel = victim.topLevel;
                    victim.lock.lock();
                    if (victim.markedForRemoval) {
                        victim.lock.unlock();
                        return false;
                    }
                    victim.markedForRemoval = true;
                    isMarked = true;
                }
                int highestLocked = -1;
                try {
                    Node<String> pred, succ; boolean valid = true;
                    for (int level = 0; valid && (level <= topLevel); level++) {
                        pred = predecessors[level];
                        pred.lock.lock();
                        highestLocked = level;
                        valid = !pred.markedForRemoval && pred.next[level]==victim;
                    }
                    if (!valid) continue;
                    for (int level = topLevel; level >= 0; level--) {
                        predecessors[level].next[level] = victim.next[level];
                    }
                    victim.lock.unlock();
                    return true;
                } finally {
                    for (int i = 0; i <= highestLocked; i++) {
                        predecessors[i].unlock();
                    }
                }
            }
            else return false;
        }
    }

    boolean contains(String name) {
        Node<String>[] predecessors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        Node<String>[] successors = (Node<String>[]) new Node[MAX_LEVEL + 1];
        int lFound = find(name, predecessors, successors);
        return (lFound != -1 && successors[lFound].fullyLinked && !successors[lFound].markedForRemoval);
    }

    int randomLevel() {
        int level = 0;
        while (level < MAX_LEVEL && random.nextDouble() < P) {
            level++;
        }
        return level;
    }
}
