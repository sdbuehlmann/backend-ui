package ch.donkeycode.backendui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorServiceConfiguration {

    private static final String THREAD_NAME_PREFIX = "BUI-POOL-";
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 2;
    private static final Duration KEEP_ALIVE_TIME = Duration.ofSeconds(2);
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);

    @Bean
    public ThreadPoolExecutor executorService() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME.getSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                runnable -> new Thread(runnable, THREAD_NAME_PREFIX + THREAD_COUNTER.getAndIncrement())
        );
    }
}
