package T1;

public class Main {
  public static final int NACCOUNTS = 10;
  public static final int INITIAL_BALANCE = 10000;

  public static void main(String[] args) {
    Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
    int i;
    for (i = 0; i < NACCOUNTS; i++) {
      TransferThread thread = new TransferThread(b, i, INITIAL_BALANCE);
      thread.setPriority(Thread.NORM_PRIORITY + i % 2);
      thread.start();
    }
  }
}

class TransferThread extends Thread {
  private final Bank bank;
  private final int fromAccount;
  private final int maxAmount;
  private static final int REPS = 1000;

  public TransferThread(Bank b, int from, int max) {
    bank = b;
    fromAccount = from;
    maxAmount = max;
  }

  public void run() {
    try {
      while (!interrupted()) {
        for (int i = 0; i < REPS; i++) {
          int toAccount = (int) (bank.size() * Math.random());
          int amount = (int) (maxAmount * Math.random() / REPS);
          //bank.BasicTransfer(fromAccount, toAccount, amount);
//          bank.syncTransfer(fromAccount, toAccount, amount);
          // bank.syncBlockTransfer(fromAccount, toAccount, amount);
           bank.LockTransfer(fromAccount, toAccount, amount);
        }
      }
    } catch (InterruptedException ignored) {
    }
  }
}
