package Threads;

import Systems.SystemService;

import java.util.Random;

public class Producer extends Thread {
    private final SystemService systemService;

    public Producer(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public void run() {
        Random random = new Random();
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (elapsedTime < 10_000) {
            this.systemService.push(random.nextInt(100));

            try {
                Thread.sleep(random.nextInt(15));
            } catch (InterruptedException ignored) {}

            elapsedTime = System.currentTimeMillis() - startTime;
        }

        systemService.isQueueOpen = false;
    }
}