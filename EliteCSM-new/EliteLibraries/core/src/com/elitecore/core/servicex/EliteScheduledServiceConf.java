package com.elitecore.core.servicex;

public class EliteScheduledServiceConf {
    private int maxParallelExecutions;
    private long initialeDelay;
    private long serviceExecutionInterval;

    public EliteScheduledServiceConf(int maxParallelExecutions, long initialeDelay, long serviceExecutionInterval) {
        this.maxParallelExecutions = maxParallelExecutions;
        this.initialeDelay = initialeDelay;
        this.serviceExecutionInterval = serviceExecutionInterval;
    }

    public int getMaxParallelExecutions() {
        return maxParallelExecutions;
    }

    public void setMaxParallelExecutions(int maxParallelExecutions) {
        this.maxParallelExecutions = maxParallelExecutions;
    }

    public long getInitialeDelay() {
        return initialeDelay;
    }

    public void setInitialeDelay(long initialeDelay) {
        this.initialeDelay = initialeDelay;
    }

    public long getServiceExecutionInterval() {
        return serviceExecutionInterval;
    }

    public void setServiceExecutionInterval(long serviceExecutionInterval) {
        this.serviceExecutionInterval = serviceExecutionInterval;
    }
}
