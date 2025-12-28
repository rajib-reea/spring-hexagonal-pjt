package com.csio.hexagonal.infrastructure.config.executor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(
    name = "async.executor.type",
    havingValue = "virtual",
    matchIfMissing = true
)
public class VirtualThreadExecutorConfig {

    @Bean("virtualExecutor")
    public TaskExecutor virtualExecutor() {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        return executor::execute;
    }
}
