package Threads;

import Systems.SystemService;

public class Watcher extends Thread {
    private SystemService systemService;
    public Watcher(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public void run() {
        int totalCount = 0;
        while(systemService.isQueueOpen) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            totalCount = totalCount+systemService.getCurrentQueueLength();

            System.out.println("Queue size: " + systemService.getCurrentQueueLength()
                    + ", Rejected probability: " + Math.round(systemService.calculateRejectedPercentage() * 100.0) / 100.0);
        }
        System.out.println("Total count: " + totalCount);
    }
}
