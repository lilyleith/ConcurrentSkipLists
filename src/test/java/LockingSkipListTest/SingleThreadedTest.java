package LockingSkipListTest;
import LockingSkipList.LazySkipList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingleThreadedTest {

    @Test
    void testSingleThreadedOperations() {
        LazySkipList<String> list = new LazySkipList();

        assertTrue(list.add("Alice"));
        assertTrue(list.contains("Alice"));

        assertFalse(list.add("Alice")); // duplicate
        assertTrue(list.contains("Alice"));

        assertTrue(list.remove("Alice"));
        assertFalse(list.contains("Alice"));

        assertFalse(list.remove("Alice")); // already removed
    }

}
