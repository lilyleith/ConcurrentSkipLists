package LockingSkipList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockingSkipList/Node.java
 * Author: skeleton code from the Art of Multiprocessor Programming,
 * Edited by Lily Leith lleit@uic.edu for UIC CS454 Graduate Project
 */

public class Node<T> {
    final Lock lock = new ReentrantLock();
    final T item;
    final int key;
    final Node<T>[] next;
    final int MAX_LEVEL = 100;

    volatile boolean markedForRemoval = false;
    volatile boolean fullyLinked = false;

    public int topLevel;

    public Node(int key) {
        this.item = null;
        this.key = key;
        next = new Node[MAX_LEVEL + 1];
        topLevel = MAX_LEVEL;
    }

    public Node(T item, int height) {
        this.item = item;
        this.key = item.hashCode();
        next = new Node[height + 1];
        topLevel = height;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }


}
