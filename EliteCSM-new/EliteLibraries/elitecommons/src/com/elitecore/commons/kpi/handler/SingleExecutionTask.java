package com.elitecore.commons.kpi.handler;

import java.util.concurrent.TimeUnit;



/**
 * Signature of the task object targeted for one time execution.
 * 
 */
public interface SingleExecutionTask {

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
	public void execute();
	
	
}
