package com.csio.hexagonal.infrastructure.config.executor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(
    name = "async.executor.type",
    havingValue = "virtual",
    matchIfMissing = true
)
public class VirtualThreadExecutorConfig {

    // Expose an Executor backed by virtual threads. Use this with Reactor's
    // `Schedulers.fromExecutor(virtualExecutor)` to offload blocking calls.
    @Bean("virtualExecutor")
    public Executor virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
