package LockFreeSkipList;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        LockFreeSkipList<Integer> lockFreeSkipList = new LockFreeSkipList<Integer>(5);
        System.out.println(LockFreeSkipList.MAX_LEVEL);
        for (int i = 0; i < 21; i++) {
            lockFreeSkipList.add(i);
        }
        System.out.println("Initial SkipList: ");
        lockFreeSkipList.drawList();
        System.out.println();

        for (int i = 0; i < 21; i++) {
            int randomNum = rand.nextInt(2);
            if (randomNum == 0) {
                System.out.println("Result of remove " + i + ": " + lockFreeSkipList.remove(i));
            } else {
                int randomToAdd = rand.nextInt(21, 30);
                System.out.println("Result of add " + randomToAdd + ": " + lockFreeSkipList.add(randomToAdd));
            }
            System.out.println("List after above operation: ");
            lockFreeSkipList.drawList();
            System.out.println();
        }
    }
}
