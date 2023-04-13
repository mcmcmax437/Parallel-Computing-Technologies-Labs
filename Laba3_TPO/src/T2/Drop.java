package T2;

public class Drop {
  private int message;
  private boolean empty = true;

  public synchronized int take() {
    while (empty) {
      try {
        wait();
      } catch (InterruptedException ignored) {
      }
    }
    empty = true;
    notifyAll();
    return message;
  }

  public synchronized void put(int message) {
    while (!empty) {
      try {
        wait();
      } catch (InterruptedException ignored) {
      }
    }
    empty = false;
    this.message = message;
    notifyAll();
  }
}
