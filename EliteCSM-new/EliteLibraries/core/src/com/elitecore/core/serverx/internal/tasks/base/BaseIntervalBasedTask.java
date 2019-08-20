/*
 * 
 */
package com.elitecore.core.serverx.internal.tasks.base;

import java.util.concurrent.TimeUnit;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;

/**
 * 
 *
 */
public abstract class BaseIntervalBasedTask implements IntervalBasedTask {

	@Override
	public long getInitialDelay() {
		return 1;
	}

	@Override
	public boolean isFixedDelay() {
		return false;
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}

	@Override
	public void preExecute(AsyncTaskContext context) {
		
	}

	@Override
	public void postExecute(AsyncTaskContext context) {
		
	}

}
