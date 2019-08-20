package com.elitecore.core.serverx.internal.tasks.base;

import java.util.concurrent.TimeUnit;

import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;

/**
 * 
 * Default base implementation class for single time execution task.
 * This class can be extended to define new single time task. 
 *
 */
public abstract class BaseSingleExecutionAsyncTask implements SingleExecutionAsyncTask {

	@Override
	public long getInitialDelay() {
		return 0;
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}

}
