package Threads;

import Systems.SystemService;

import java.util.Random;

public class Consumer extends Thread {
    private final SystemService systemService;

    public Consumer(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public void run() {
        Random random = new Random();

        while(systemService.isQueueOpen) {
            int newItem = systemService.pop();

            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException ignored) {}

            systemService.incrementApprovedCount();
        }
    }

}