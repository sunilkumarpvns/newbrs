package com.elitecore.corenetvertex.util;

import java.util.concurrent.Future;

import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
/**
 * 
 * 
 * @author Chetan.Sankhala
 */
public interface TaskScheduler {
	
	public Future<?> scheduleSingleExecutionTask(SingleExecutionTask task);
	
	public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task);
	
}
