package LockFreeSkipList;

import java.util.concurrent.atomic.AtomicMarkableReference;

import static LockFreeSkipList.LockFreeSkipList.MAX_LEVEL;


/**
 * LockFreeSkipList/Node.java
 * Author: skeleton code from the Art of Multiprocessor Programming,
 * Edited by Lily Leith lleit@uic.edu for UIC CS454 Graduate Project
 */

public class Node<T> {
    final T value;
    final int key;
    final AtomicMarkableReference<Node<T>>[] next;
    public final int topLevel;

    // constructor for the sentinel nodes
    public Node(int key) {
        this.value = null;
        this.key = key;
        this.next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
        for (int i = 0; i < next.length; i++) {
            next[i] = new AtomicMarkableReference<Node<T>>(null,false);
        }
        topLevel = MAX_LEVEL;
    }

    // constructor for ordinary nodes
    public Node(T x, int height) {
        value = x;
        key = x.hashCode();
        next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
        for (int i = 0; i < next.length; i++) {
            next[i] = new AtomicMarkableReference<Node<T>>(null,false);
        }
        topLevel = height;
    }
}
