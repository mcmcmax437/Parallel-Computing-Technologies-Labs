package T1;

import java.util.concurrent.locks.ReentrantLock;

public class Bank {
  public final Object sync = new Object();
  public static final int NTEST = 10000;
  private final int[] accounts;
  private long transaction = 0;
  private final ReentrantLock Lock = new ReentrantLock();

  public Bank(int n, int initialBalance) {
    accounts = new int[n];
    int i;
    for (i = 0; i < accounts.length; i++) accounts[i] = initialBalance;
    transaction = 0;
  }

  public void BasicTransfer(int from, int to, int amount) throws InterruptedException {
    accounts[from] -= amount;
    accounts[to] += amount;
    transaction++;

    if (transaction % NTEST == 0) {
      test();
    }
  }

public synchronized void syncTransfer(int from, int to, int amount) throws InterruptedException {
    accounts[from] -= amount;
    accounts[to] += amount;
    transaction++;
    if (transaction % NTEST == 0){
      test();
    }
  }

  public synchronized void syncBlockTransfer(int from, int to, int amount) throws InterruptedException {
    synchronized (sync) {
      accounts[from] -= amount;
      accounts[to] += amount;
      transaction++;
      if (transaction % NTEST == 0) {
        test();
      }
    }
  }

  public void LockTransfer(int from, int to, int amount) throws InterruptedException {
    Lock.lock();
    try {
      accounts[from] -= amount;
      accounts[to] += amount;
      transaction++;

      if (transaction % NTEST == 0) {
        test();
      }
    } finally {
        Lock.unlock();
      }
    }

  public void test() {
    int sum = 0;
    for (int account : accounts) sum += account;
    System.out.println("Transactions[" + transaction +"]" + " Balance[" + sum + "]");
  }

  public int size() {
    return accounts.length;
  }
}
