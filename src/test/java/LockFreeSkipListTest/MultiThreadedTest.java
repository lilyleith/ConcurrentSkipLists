package LockFreeSkipListTest;
import LockFreeSkipList.LockFreeSkipList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiThreadedTest {

    @Test
    public void testAdd() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<Integer>();
        int numThreads = 10;
        int perThread = 1000;
        Thread[] threads = new Thread[numThreads];
        Set<Integer>[] insertedValues = new Set[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int threadID = i;
            insertedValues[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    int value = threadID * perThread + j;
                    insertedValues[threadID].add(value);
                    skipList.add(value);
                }
            });
        }

        runAllThreads(threads);

        Set<Integer> expected = new HashSet<>();
        for (Set<Integer> set : insertedValues) expected.addAll(set);
        for (Integer val : expected) {
            Assertions.assertTrue(skipList.contains(val));
        }
    }

    @Test
    public void testRemove() {
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<Integer>();

        int totalElements = 1000;
        List<Integer> initialValues = new ArrayList<>();
        for (int i = 0; i < totalElements; i++) {
            skipList.add(i);
            initialValues.add(i);
        }

        Thread[] threads = new Thread[10];
        int perThread = totalElements / threads.length;
        List<Set<Integer>> removedByThread = new ArrayList<>();

        for (int i = 0; i < threads.length; i++) {
            int threadID = i;
            Set<Integer> threadRemoved = new HashSet<>();
            removedByThread.add(threadRemoved);
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                int removed = 0;
                while (removed < perThread) {
                    int candidate = initialValues.get(rnd.nextInt(initialValues.size()));
                    if (skipList.remove(candidate)) {
                        threadRemoved.add(candidate);
                        removed++;
                    }
                }
            });
        }

        runAllThreads(threads);
        for (int i = 0; i < totalElements; i++) {
            Assertions.assertFalse(skipList.contains(i));
        }

        int totalRemoved = removedByThread.stream().mapToInt(Set::size).sum();
        Assertions.assertEquals(totalElements, totalRemoved);
    }

    /*default*/
    static void runAllThreads(Thread ... threads) {
        AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        for (Thread thread : threads) {
            thread.setUncaughtExceptionHandler((t, ex) -> {
                ex.printStackTrace();
                exceptionThrown.set(true);
            });
        }
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
        Assertions.assertFalse(exceptionThrown.get());
    }
}
