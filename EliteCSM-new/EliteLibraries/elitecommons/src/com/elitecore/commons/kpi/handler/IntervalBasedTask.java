package com.elitecore.commons.kpi.handler;

import java.util.concurrent.TimeUnit;


public interface IntervalBasedTask {

	/**
	 * Returns number of seconds 
	 * @return
	 */
	public long getInitialDelay();
	
	/**
	 * Delay between executions
	 * 
	 * @return
	 */
	public long getInterval();
	
	/**
	 * Return if fix delay of interval is required after each execution.
	 * 
	 * @return
	 */
	public boolean isFixedDelay();
	
	/**
	 * Return TimeUnit
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnit();
	
	/**
	 * Call-back before executing the task.
	 * @param context
	 */
	public void preExecute();
	
	
	/**
	 * Call-back for executing the task.
	 * @param context
	 */
	public void execute();
	
	
	/**
	 * Call-back after task execution.
	 * @param context
	 */
	public void postExecute();
	
}
