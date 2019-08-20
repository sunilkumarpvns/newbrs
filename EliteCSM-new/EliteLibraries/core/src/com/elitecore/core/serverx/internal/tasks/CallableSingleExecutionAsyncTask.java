package com.elitecore.core.serverx.internal.tasks;

import java.util.concurrent.TimeUnit;

public interface CallableSingleExecutionAsyncTask<T> {

	/**
	 * Returns number of seconds to be blocked before executing
	 * the task.
	 * @return
	 */
	public long getInitialDelay();
	
	
	/**
	 * Return TimeUnit
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnit();
	
	
	/**
	 * Call-back for executing the task.
	 * @param context
	 */
	public T execute(AsyncTaskContext context);
}
