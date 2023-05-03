package T2;

public class Main {
  public static final int ITER = 100;
  public static void main(String[] args) {

    Drop drop = new Drop();
    (new Thread(new Producer(drop))).start();
    (new Thread(new Consumer(drop))).start();
  }
}
