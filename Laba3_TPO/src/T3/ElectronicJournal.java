package T3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ElectronicJournal {

    private static final int NUM_GROUPS = 3;
    private static final int NUM_STUDENTS_PER_GROUP = 20;
    private static final int NUM_LECTURERS = 4;
    private static final int MAX_GRADE = 100;

    private final Lock[] groupLocks;
    private final int[][] journal;

    public ElectronicJournal() {
        groupLocks = new Lock[NUM_GROUPS];
        Arrays.setAll(groupLocks, i -> new ReentrantLock());
        journal = new int[NUM_GROUPS][NUM_STUDENTS_PER_GROUP];
    }

    public void run() {
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_LECTURERS; i++) {
            int i1 = i;
            Thread thread = new Thread(() -> giveGrades(i1));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < NUM_GROUPS; i++) {
            System.out.print("Group" + (i+1) + " -- ");
            for (int j = 0; j < NUM_STUDENTS_PER_GROUP; j++) {
                System.out.print(journal[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void giveGrades(int lecturerId) {
        Random random = new Random();

        while (true) {

            boolean allGraded = true;
            for (int[] group : journal) {
                for (int grade : group) {
                    if (grade == 0) {
                        allGraded = false;
                        break;
                    }
                }
                if (!allGraded) {
                    break;
                }
            }
            if (allGraded) {
                break;
            }


            int groupId = random.nextInt(NUM_GROUPS);
            Lock groupLock = groupLocks[groupId];


            if (groupLock.tryLock()) {
                try {

                    if (Arrays.stream(journal[groupId]).allMatch(grade -> grade > 0)) {
                        continue;
                    }

                    int studentId;
                    do {
                        studentId = random.nextInt(NUM_STUDENTS_PER_GROUP);
                    } while (journal[groupId][studentId] > 0);

                    int grade = random.nextInt(MAX_GRADE) + 1;
                    journal[groupId][studentId] = grade;
//                    if(lecturerId+1 == 1) {
//                        System.out.println("Lec " + (lecturerId + 1) + " give " + grade +
//                                " Student " + (studentId + 1) + " Group " + (groupId + 1));
//                    }
                    System.out.println("Lec" + (lecturerId + 1) + " give " + grade +
                            " Student " + (studentId + 1) + " Group" + (groupId + 1));

                } finally {
                    groupLock.unlock();
                }
            }
        }
    }




    public static void main(String[] args) {
        ElectronicJournal journal = new ElectronicJournal();
        journal.run();
    }
}
