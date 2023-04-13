package T2;
import java.util.Random;
import static T2.Main.ITER;

public class Producer implements Runnable {
  private final Drop drop;

  public Producer(Drop drop) {
    this.drop = drop;
  }

  public void run() {
    int[] importantInfo = new int[ITER];
    for (int i = 0; i < importantInfo.length; i++){
      importantInfo[i] = i + 1;
    }
    Random random = new Random();

    for (int i : importantInfo) {
      drop.put(i);
      try {
        Thread.sleep(random.nextInt(50));
      } catch (InterruptedException ignored) {
      }
    }
    drop.put(0);
  }
}

