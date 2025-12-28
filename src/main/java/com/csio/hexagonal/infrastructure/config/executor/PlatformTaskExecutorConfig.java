package com.csio.hexagonal.infrastructure.config.executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties(AsyncExecutorProperties.class)
@ConditionalOnProperty(
    name = "async.executor.type",
    havingValue = "platform"
)
public class PlatformTaskExecutorConfig  {

    @Bean("cpuExecutor")
    public TaskExecutor cpuExecutor(AsyncExecutorProperties props) {
        int cores = Runtime.getRuntime().availableProcessors();

        int corePoolSize = cores * props.getCoreMultiplier();
        int maxPoolSize  = cores * props.getMaxMultiplier();
        int queueCapacity = cores * props.getQueueMultiplier();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("CPU-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();
        return executor;
    }
}