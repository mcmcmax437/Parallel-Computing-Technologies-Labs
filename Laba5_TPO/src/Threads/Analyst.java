package Threads;

import Systems.SystemService;

public class Analyst extends Thread {
    private final SystemService systemService;
    private int sumQueuesLengths;
    private int iteration;

    public Analyst(SystemService systemService) {
        this.systemService = systemService;
        sumQueuesLengths = iteration = 0;
    }

    @Override
    public void run() {
        while(systemService.isQueueOpen) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}

            sumQueuesLengths += systemService.getCurrentQueueLength();
            iteration++;
        }
    }

    public double getAverageQueueLength() {
        return  sumQueuesLengths / (double)iteration;
    }
}
