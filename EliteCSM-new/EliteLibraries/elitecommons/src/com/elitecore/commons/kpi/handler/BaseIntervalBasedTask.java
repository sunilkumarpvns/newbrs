/*
 * 
 */
package com.elitecore.commons.kpi.handler;

import java.util.concurrent.TimeUnit;

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
	public void preExecute() {
		
	}

	@Override
	public void postExecute() {
		
	}

}
