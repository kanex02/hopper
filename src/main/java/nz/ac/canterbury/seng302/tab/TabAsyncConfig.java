package nz.ac.canterbury.seng302.tab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Application config for async methods
 */
@Configuration
@EnableAsync
public class TabAsyncConfig implements AsyncConfigurer {

    /**
     * Defines the thread pool executor specific to this application
     *
     * @return Returns a new {@link Executor}
     */
    @Bean(name = "executor")
    public Executor executor() {
        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("TAB-Executor");
        executor.initialize();

        return executor;
    }

    /**
     * Creates a new scheduled executor service for asynchronously scheduling tasks as a Spring bean that can be
     * Autowired into other components and services
     *
     * @return Returns a new schedule executor service with 10 cores
     */
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(10);
    }

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    public Executor getAsyncExecutor() {
        return this.executor();
    }

}
