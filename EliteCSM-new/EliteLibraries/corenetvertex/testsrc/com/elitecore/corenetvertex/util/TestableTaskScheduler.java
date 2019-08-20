package com.elitecore.corenetvertex.util;

import java.util.concurrent.Future;

import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.commons.threads.SettableFuture;

public class TestableTaskScheduler implements TaskScheduler {

    private SingleExecutionTask singleExecutionTask;
    private IntervalBasedTask intervalBasedTask;

    @Override
    public Future<?> scheduleSingleExecutionTask(SingleExecutionTask task) {
        if(singleExecutionTask == null) {
            this.singleExecutionTask = task;
        }
        return SettableFuture.create();
    }

    @Override
    public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
        if(intervalBasedTask == null) {
            this.intervalBasedTask = task;
        }
        return SettableFuture.create();
    }

    public void executeIntervalBaseTask() {
        if(intervalBasedTask != null) {
            intervalBasedTask.execute();
        }
    }
    
    public void executeSingleExecutionTask() {
        if(singleExecutionTask != null) {
            singleExecutionTask.execute();
        }
    }
}
