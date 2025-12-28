package com.csio.hexagonal.infrastructure.config.executor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "async.executor")
public class AsyncExecutorProperties {

    private int coreMultiplier;
    private int maxMultiplier;
    private int queueMultiplier;

    public int getCoreMultiplier() {
        return coreMultiplier;
    }

    public void setCoreMultiplier(int coreMultiplier) {
        this.coreMultiplier = coreMultiplier;
    }

    public int getMaxMultiplier() {
        return maxMultiplier;
    }

    public void setMaxMultiplier(int maxMultiplier) {
        this.maxMultiplier = maxMultiplier;
    }

    public int getQueueMultiplier() {
        return queueMultiplier;
    }

    public void setQueueMultiplier(int queueMultiplier) {
        this.queueMultiplier = queueMultiplier;
    }
}
