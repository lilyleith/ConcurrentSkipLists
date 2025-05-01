package LockFreeSkipList;

import java.util.Random;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * LockFreeSkipList.java
 * Author: skeleton code from the Art of Multiprocessor Programming,
 * Edited by Lily Leith lleit@uic.edu for UIC CS454 Graduate Project
 */

public class LockFreeSkipList<T> {
    static final int MAX_LEVEL = 5;
    final Node<T> head = new Node<T>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<T>(Integer.MAX_VALUE);
    private final Random random = new Random();
    static final double P = 0.5;
    public LockFreeSkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<Node<T>>(tail, false);
        }
    }

    public boolean add(T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] predecessors = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] successors = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, predecessors, successors);
            if (found) {
                return false;
                } else {
                Node<T> newNode = new Node(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = successors[level];
                    newNode.next[level].set(succ, false);
                    }
                Node<T> pred = predecessors[bottomLevel];
                Node<T> succ = successors[bottomLevel];
                newNode.next[bottomLevel].set(succ, false);
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode,  false, false)) {
                    continue;
                    }
                for (int level = bottomLevel+1; level <= topLevel; level++) {
                    while (true) {
                        pred = predecessors[level];
                        succ = successors[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false)) {
                            break;
                        }
                        find(x, predecessors, successors);
                    }
                }
                return true;
            }
        }
    }

    public boolean remove(T x) {
        int bottomLevel = 0;
        Node<T>[] predecessors = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] successors = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, predecessors, successors);
            if (!found) {
                return false;
                } else {
                Node<T> nodeToRemove = successors[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel+1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].attemptMark(succ, true);
                        succ = nodeToRemove.next[level].get(marked);
                        }
                    }
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    succ = successors[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, predecessors, successors);
                        return true;
                    }
                    else if (marked[0]) return false;
                }
            }
        }
    }

    public boolean find(T x, Node<T>[] predecessors, Node<T>[] successors) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        boolean snip;
        Node<T> pred = null, curr = null, succ = null;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip) continue retry;
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                        }
                    if (curr.key < key){
                        pred = curr; curr = succ;
                        } else {
                        break;
                        }
                    }
                predecessors[level] = pred;
                successors[level] = curr;
            }
            return (curr.key == key);
        }
    }

    public boolean contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = {false};
        Node<T> pred = head, curr = null, succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = pred.next[level].getReference();
                    succ = curr.next[level].get(marked);
                }
                if (curr.key < v){
                    pred = curr;
                    curr = succ;
                    } else {
                    break;
                }
            }
        }
        return (curr.key == v);
    }

    public void drawList() {
        System.out.println("Current Skip List Structure:");
        for (int level = MAX_LEVEL; level >= 0; level--) {
            StringBuilder line = new StringBuilder("L" + level + ": ");
            Node<T> curr = head;
            while (curr != null) {
                Node<T> next = curr.next[level].getReference();
                boolean[] marked = {false};
                curr.next[level].get(marked);
                if (marked[0]) {
                    // Skip logically removed nodes
                    curr = next;
                    continue;
                }
                // Display current node
                if (curr == head) {
                    line.append("HEAD");
                } else if (curr == tail) {
                    line.append(" -> TAIL");
                    break;
                } else {
                    if (curr.value != null) {
                        line.append(String.format(" -> [%s]", curr.value.toString()));
                    }
                }
                curr = next;
            }
            System.out.println(line.toString());
        }
    }



    int randomLevel() {
        int level = 0;
        while (level < MAX_LEVEL && random.nextDouble() < P) {
            level++;
        }
        return level;
    }
}
