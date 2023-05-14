package Systems;

import java.util.ArrayDeque;
import java.util.Queue;



public class SystemService {
    private int QUEUE_SIZE;
    private int rejectedCount;
    private int approvedCount;
    private final Queue<Integer> queue;
    public boolean isQueueOpen;

    public SystemService(int QUEUE_SIZE) {
        this.QUEUE_SIZE = QUEUE_SIZE;
        this.approvedCount = rejectedCount = 0;
        this.isQueueOpen = true;
        this.queue = new ArrayDeque<>();
    }

    public synchronized void push(int item) {
        if(queue.size() >= QUEUE_SIZE) {
            rejectedCount++;
            return;
        }

        queue.add(item);
        notifyAll();
    }

    public synchronized int pop() {
        while(queue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }

        return queue.poll();
    }

    public synchronized void incrementApprovedCount() {
        approvedCount++;
    }

    public double calculateRejectedPercentage() {
        return rejectedCount / (double)(rejectedCount + approvedCount);
    }

    public  double totalCount (){
        return rejectedCount + approvedCount;
    }

    public synchronized int getCurrentQueueLength () {
        return queue.size();
    }
}