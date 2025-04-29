package LockingSkipList;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static LockingSkipList.LazySkipList.MAX_LEVEL;
import static org.junit.jupiter.api.Assertions.*;

public class LazySkipListTest {
    private final LazySkipList skipList = new LazySkipList();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    // Helper method to perform concurrent operations
    private void performConcurrentOperations(Runnable... operations) throws InterruptedException, ExecutionException {
        List<Future<Void>> futures = new ArrayList<>();
        for (Runnable operation : operations) {
            futures.add(executor.submit(() -> {
                operation.run();
                return null;
            }));
        }
        for (Future<Void> future : futures) {
            future.get();
        }
    }

    // Helper method to check if a name exists in the skip list
    private boolean contains(String name) {
        return skipList.contains(name);
    }

    // Helper method to find a name in the skip list
    private boolean find(String name) {
        return skipList.find(name, new Node[MAX_LEVEL + 1], new Node[MAX_LEVEL + 1]) != -1;
    }

    // Helper method to add a name to the skip list
    private boolean add(String name) {
        return skipList.add(name);
    }

    // Helper method to remove a name from the skip list
    private boolean remove(String name) {
        return skipList.remove(name);
    }

    // Helper method to add and then remove a name from the skip list
    private void addAndRemove(String name) {
        add(name);
        remove(name);
    }

    // Helper method to add a name and check its presence
    private void addAndCheck(String name) {
        add(name);
        assertTrue(contains(name));
    }

    // Helper method to remove a name and check its absence
    private void removeAndCheck(String name) {
        remove(name);
        assertFalse(contains(name));
    }

    // Helper method to perform multiple operations on a name
    private void performOperations(String name) {
        addAndCheck(name);
        removeAndCheck(name);
    }

    // Test for add operation
    @Test
    void testAddLinearizability() throws InterruptedException, ExecutionException {
        String name = "Alice";
        performConcurrentOperations(
                () -> addAndCheck(name),
                () -> addAndCheck(name),
                () -> addAndCheck(name)
        );
        assertTrue(contains(name));
    }

    // Test for remove operation
    @Test
    void testRemoveLinearizability() throws InterruptedException, ExecutionException {
        String name = "Bob";
        add(name); // Ensure the name is present before removal
        performConcurrentOperations(
                () -> removeAndCheck(name),
                () -> removeAndCheck(name),
                () -> removeAndCheck(name)
        );
        assertFalse(contains(name));
    }

    // Test for add and remove operations
    @Test
    void testAddAndRemoveLinearizability() throws InterruptedException, ExecutionException {
        String name = "Charlie";
        performConcurrentOperations(
                () -> performOperations(name),
                () -> performOperations(name),
                () -> performOperations(name)
        );
        assertFalse(contains(name));
    }

    // Test for contains operation
    @Test
    void testContainsLinearizability() throws InterruptedException, ExecutionException {
        String name = "David";
        add(name); // Ensure the name is present
        performConcurrentOperations(
                () -> assertTrue(contains(name)),
                () -> assertTrue(contains(name)),
                () -> assertTrue(contains(name))
        );
        remove(name); // Ensure the name is removed
        performConcurrentOperations(
                () -> assertFalse(contains(name)),
                () -> assertFalse(contains(name)),
                () -> assertFalse(contains(name))
        );
    }

    // Test for find operation
    @Test
    void testFindLinearizability() throws InterruptedException, ExecutionException {
        String name = "Eve";
        add(name); // Ensure the name is present
        performConcurrentOperations(
                () -> assertTrue(find(name)),
                () -> assertTrue(find(name)),
                () -> assertTrue(find(name))
        );
        remove(name); // Ensure the name is removed
        performConcurrentOperations(
                () -> assertFalse(find(name)),
                () -> assertFalse(find(name)),
                () -> assertFalse(find(name))
        );
    }
}
