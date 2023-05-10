package task2;

import task2.basic.BasicAlgorithm;
import task2.fox.FoxAlgorithm;
import task2.utils.Matrix;
import java.util.concurrent.ForkJoinPool;

public class Main {
  public static void main(String[] args) {
    simpleRun(false);
  }

  public static void simpleRun(boolean printMatrices) {
    int sizeAxis0 = 2000;
    int sizeAxis1 = 2000;

    Matrix A = new Matrix(sizeAxis0, sizeAxis1);
    Matrix B = new Matrix(sizeAxis0, sizeAxis1);

    A.generateRandomMatrix();
    B.generateRandomMatrix();


    int nThread = Runtime.getRuntime().availableProcessors();

    BasicAlgorithm ba = new BasicAlgorithm(A, B);

    long currTimeBasic = System.currentTimeMillis();
    Matrix C = ba.multiply();
    currTimeBasic = System.currentTimeMillis() - currTimeBasic;

    if (printMatrices) C.print();

    System.out.println("Time for Basic Algorithm: " + currTimeBasic);

    long currTimeFox = System.currentTimeMillis();
    ForkJoinPool forkJoinPool = new ForkJoinPool(nThread);
    C = forkJoinPool.invoke(new FoxAlgorithm(A, B, 6));
    currTimeFox = System.currentTimeMillis() - currTimeFox;

    if (printMatrices) C.print();

    System.out.println("Time for Fox Algorithm: " + currTimeFox);
    System.out.println();
    System.out.println(
        "SpeedUp (timeBasic / timeFoxForkJoin): " + (double) currTimeBasic / currTimeFox);

    if (sizeAxis0 == 2000) {
      /*
      22474 - this is average execution time for Fox algorithm (from assignment 2)
       */
      System.out.println(
          "SpeedUp (timeFox / timeFoxForkJoin): " + (double) 22474 / currTimeFox);
    }else if (sizeAxis0 == 1000){
      System.out.println(
              "SpeedUp (timeFox / timeFoxForkJoin): " + (double) 14045 / currTimeFox );
    }else if  (sizeAxis0 == 500){
      System.out.println(
              "SpeedUp (timeFox / timeFoxForkJoin): " + (double) 6812 / currTimeFox);
    }
  }
}
