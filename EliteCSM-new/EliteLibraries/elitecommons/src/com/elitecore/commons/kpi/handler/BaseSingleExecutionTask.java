package com.elitecore.commons.kpi.handler;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Default base implementation class for single time execution task.
 * This class can be extended to define new single time task. 
 *
 */
public abstract class BaseSingleExecutionTask implements SingleExecutionTask {

	@Override
	public long getInitialDelay() {
		return 0;
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}

}
