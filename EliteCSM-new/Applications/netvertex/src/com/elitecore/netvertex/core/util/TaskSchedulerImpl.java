package com.elitecore.netvertex.core.util;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class TaskSchedulerImpl implements TaskScheduler {



    private AsyncTaskContext taskContext ;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private String MODULE;


    public TaskSchedulerImpl(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, String module) {
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.MODULE = module;
        this.taskContext = new DummyAsyncTaskContext();
    }

    public TaskSchedulerImpl(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, String module,AsyncTaskContext taskContext) {
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.MODULE = module;
        this.taskContext = taskContext;
    }



    @Override
    public Future<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
        if (task == null) {
            return null;
        }

        Future<?> future = null;
        if (task.getInitialDelay() > 0) {
            future = scheduledThreadPoolExecutor.schedule(() -> {

                try {
                    task.execute(taskContext);
                } catch (Exception e) {
                    LogManager.getLogger().trace(MODULE,e);

                }
            }, task.getInitialDelay(), task.getTimeUnit());
        } else {
            future = scheduledThreadPoolExecutor.submit(() -> {

                try {
                   task.execute(taskContext);
                } catch (Exception e) {
                    LogManager.getLogger().trace(MODULE,e);
                }
            });
        }

        return future;

    }

    @Override
    public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
        if (task == null) {
            return null;
        }

        Future<?> future = null;
        if (task.isFixedDelay()) {
            try {
                future = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
                    try {
                        task.preExecute(taskContext);
                    } catch (Exception e) {
                           getLogger().trace(MODULE,e);
                    }

                    try {
                        task.execute(taskContext);
                    } catch (Exception e) {
                        getLogger().trace(MODULE,e);
                    }

                    try {
                        task.postExecute(taskContext);
                    } catch (Exception e) {
                        getLogger().trace(MODULE,e);
                    }

                }, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());

            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error in scheduling Fixed Delay task reason: " + e.getMessage());
                LogManager.getLogger().trace(e);
            }

        } else {
            try {
                future = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {

                    try {
                        task.preExecute(taskContext);
                    } catch (Exception e) {
                          getLogger().trace(MODULE,e);
                    }

                    try {
                        task.execute(taskContext);
                    } catch (Exception e) {
                        getLogger().trace(MODULE,e);
                    }

                    try {
                        task.postExecute(taskContext);
                    } catch (Exception e) {
                        getLogger().trace(MODULE,e);
                    }
                }, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error in scheduling task reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }

        }

        return future;
    }

    public void execute(Runnable command) {
        scheduledThreadPoolExecutor.execute(command);
    }
}
