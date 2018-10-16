package max.dirscan.output;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Timer {

    private AtomicInteger counter = new AtomicInteger(0);

    private ScheduledExecutorService scheduledExecutorService;

    private Runnable timePrinting = new TimePrinter();

    private long start;

    public void start() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(timePrinting,1000, 1000, TimeUnit.MILLISECONDS);
        start = System.currentTimeMillis();
    }

    public long stop() {
        if(Objects.nonNull(scheduledExecutorService)) {
            CompletableFuture.runAsync(scheduledExecutorService::shutdownNow);
        }
        return System.currentTimeMillis() - start;
    }

    private class TimePrinter implements Runnable {

        @Override
        public void run() {
            int times = counter.incrementAndGet();
            if(times % 60 == 0) {
                System.out.print("|");
            } else {
                System.out.print(".");
            }
        }
    }
}
