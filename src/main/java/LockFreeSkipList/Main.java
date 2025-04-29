package LockFreeSkipList;

public class Main {
    public static void main(String[] args) {
        LockFreeSkipList<String> lockFreeSkipList = new LockFreeSkipList<String>();
        String[] names = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
        lockFreeSkipList.drawList();
        for (String name : names) {
            lockFreeSkipList.add(name);

        }
        lockFreeSkipList.drawList();
    }
}
