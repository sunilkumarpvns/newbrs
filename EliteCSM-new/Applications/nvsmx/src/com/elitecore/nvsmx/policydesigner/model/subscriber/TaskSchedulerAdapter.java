package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskSchedulerAdapter implements com.elitecore.corenetvertex.util.TaskScheduler {

    private TaskScheduler taskScheduler;

    public TaskSchedulerAdapter(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public Future<?> scheduleSingleExecutionTask(final SingleExecutionTask task) {

        return taskScheduler.scheduleSingleExecutionTask(new SingleExecutionAsyncTask() {

            @Override
            public TimeUnit getTimeUnit() {
                return task.getTimeUnit();
            }

            @Override
            public long getInitialDelay() {
                return task.getInitialDelay();
            }

            @Override
            public void execute(AsyncTaskContext context) {
                task.execute();
            }
        });
    }

    @Override
    public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {

        return taskScheduler.scheduleIntervalBasedTask(new com.elitecore.core.serverx.internal.tasks.IntervalBasedTask() {

            @Override
            public void preExecute(AsyncTaskContext context) {
                task.preExecute();
            }

            @Override
            public void postExecute(AsyncTaskContext context) {
                task.postExecute();
            }

            @Override
            public boolean isFixedDelay() {
                return task.isFixedDelay();
            }

            @Override
            public TimeUnit getTimeUnit() {
                return task.getTimeUnit();
            }

            @Override
            public long getInterval() {
                return task.getInterval();
            }

            @Override
            public long getInitialDelay() {
                return task.getInitialDelay();
            }

            @Override
            public void execute(AsyncTaskContext context) {
                task.execute();
            }
        });
    }

}
