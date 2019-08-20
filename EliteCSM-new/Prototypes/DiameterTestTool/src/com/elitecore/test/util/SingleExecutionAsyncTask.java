	/*
 * Ezhava Baiju Dhanpal
 * 18th December 2010
 * 
 * Elitecore Technologies Pvt. Ltd.
 * 
 */
package com.elitecore.test.util;

import java.util.concurrent.TimeUnit;



/**
 * Signature of the task object targeted for one time execution.
 * 
 */
public interface SingleExecutionAsyncTask {

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
	 */
	public void execute();
	
	
}
