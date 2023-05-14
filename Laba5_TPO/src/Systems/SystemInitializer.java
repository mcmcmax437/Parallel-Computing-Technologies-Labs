
package Systems;

import Threads.Analyst;
import Threads.Consumer;
import Threads.Producer;
import Threads.Watcher;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class SystemInitializer implements Callable<double[]> {
    private int QUEUE_SIZE;

    private int CHANNELS;

    private boolean hasWatcher;

    public SystemInitializer(boolean hasWatcher, int CHANNELS, int QUEUE_SIZE) {
        this.QUEUE_SIZE = QUEUE_SIZE;
        this.CHANNELS = CHANNELS;
        this.hasWatcher = hasWatcher;
    }

    public double[] call() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        SystemService systemService = new SystemService(QUEUE_SIZE);

        Analyst analyst = new Analyst(systemService);

        for (int i = 0; i < CHANNELS; i++)
            executor.execute(new Consumer(systemService));

        if(hasWatcher)
            executor.execute(new Watcher(systemService));
        executor.execute(new Producer(systemService));
        executor.execute(analyst);

        executor.shutdown();

        System.out.println("SMO is running");

        try {
            boolean ok = executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        return new double[] {systemService.calculateRejectedPercentage(), analyst.getAverageQueueLength(), systemService.totalCount()};
    }
}
