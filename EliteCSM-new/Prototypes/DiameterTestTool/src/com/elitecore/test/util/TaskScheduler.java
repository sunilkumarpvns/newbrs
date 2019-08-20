package com.elitecore.test.util;

import javax.annotation.Nullable;
import java.util.concurrent.Future;

/**
 * Represents an entity which can be used for executing tasks, for single or
 * periodic execution.  
 * 
 * @author narendra.pathai
 *
 */
public interface TaskScheduler {
	 /**
	 * <p>Schedules the task passed for single execution. If the task is null it will return null. 
	 * If the task is not null, it will be scheduled for execution as per the initial delay set. 
	 * 
	 * <p>This method is non blocking, the call to this method will return immediately
	 * and the task passed will be executed in other thread context. The caller can 
	 * cancel the task or check whether the task is completed using the Future returned.
	 * 
	 * @param task a nullable task which needs to be scheduled for one time execution
	 * @return future for the task which can be used to query the status of the task
	 * or cancel the task, null if the task is null
	 */
    public @Nullable Future<?> scheduleSingleExecutionTask(@Nullable SingleExecutionAsyncTask task);
    /**
	 * Executes a periodic action that becomes enabled first after the given
	 * initial delay, and subsequently with the given delay between the termination of one
	 * execution and the commencement of the next.
	 * 
	 * <p>This method is non blocking, the call to this method will return immediately
	 * and the task passed will be executed in other thread context. The caller can 
	 * cancel the task or check whether the task is completed using the Future returned.
	 * 
	 * @param task a nullable task which needs to be scheduled for periodic execution
	 * @return future for the task which can be used to query the status of the task
	 * or cancel the task, null if the task is null
	 */
    public @Nullable Future<?> scheduleIntervalBasedTask(@Nullable IntervalBasedTask task);
}
