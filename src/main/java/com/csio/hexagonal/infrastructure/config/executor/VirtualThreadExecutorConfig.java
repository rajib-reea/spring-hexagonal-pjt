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
    // Note: Falls back to cached thread pool for Java < 21
    @Bean("virtualExecutor")
    public Executor virtualExecutor() {
        try {
            // Try to use virtual threads if available (Java 21+)
            return (Executor) Executors.class.getMethod("newVirtualThreadPerTaskExecutor").invoke(null);
        } catch (Exception e) {
            // Fall back to cached thread pool for Java < 21
            return Executors.newCachedThreadPool();
        }
    }
}
