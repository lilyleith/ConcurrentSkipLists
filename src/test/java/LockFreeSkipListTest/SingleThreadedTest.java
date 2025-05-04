package LockFreeSkipListTest;
import LockFreeSkipList.LockFreeSkipList;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SingleThreadedTest {

    @Test
    void testSingleThreadedAdd() {
        LockFreeSkipList<String> lockFreeSkipList = new LockFreeSkipList<String>(10);
        String[] names = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String name : names) {
            lockFreeSkipList.add(name);
        }
        for (String name : names) {
            assertTrue(lockFreeSkipList.contains(name));
        }
    }

    @Test
    void testSingleThreadedRemove() {
        LockFreeSkipList<String> lockFreeSkipList = new LockFreeSkipList<String>(10);
        String[] names = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String name : names) {
            lockFreeSkipList.add(name);
        }

        String[] notContainedNames = new String[] {"I", "J", "K", "L", "M", "N", "O", "P"};
        for (String name : notContainedNames) {
            assertFalse(lockFreeSkipList.remove(name));
        }
        for (String name : names) {
            assertTrue(lockFreeSkipList.remove(name));
            assertFalse(lockFreeSkipList.contains(name));
        }
    }
}



