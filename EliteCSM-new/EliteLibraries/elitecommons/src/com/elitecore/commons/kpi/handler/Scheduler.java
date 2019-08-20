package com.elitecore.commons.kpi.handler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;

public class Scheduler {
	
	private static final String MODULE = "SCHEDULER";

	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	
	public Scheduler() {
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(15, new EliteThreadFactory(KpiConfiguration.KPI_THREAD_KEY, KpiConfiguration.KPI_THREAD_NAME_PREFIX, Thread.NORM_PRIORITY));
	}
	
	//TODO check for minimum no. of threads
	public Scheduler(int maxThreads, ThreadFactory threadFactory) {
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(maxThreads, threadFactory);
	}
	
	public void scheduleSingleExecutionTask(final SingleExecutionTask task) {
    	
    	if (task != null) {
    		if (task.getInitialDelay() > 0) {
    			scheduledThreadPoolExecutor.schedule(new Runnable(){
					@Override
					public void run() {
	    				try {
	    					task.execute();
	    				}catch(Throwable t) {
	    					
	    				}
					}}, task.getInitialDelay(), task.getTimeUnit());
    		} else {
    			scheduledThreadPoolExecutor.execute(new Runnable() {

					@Override
					public void run() {
	    				try {
	    					task.execute();
	    				}catch(Throwable t) {
	    					
	    				}
					}});
    		}
    	} 
    }
	
	public void scheduleIntervalBasedTask(final IntervalBasedTask task) {
    	if (task != null) {
    		if (task.isFixedDelay()) {
    			try{
    				scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
    					
		    			@Override
						public void run() {
		    				try {
		    					task.preExecute();
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.execute();
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.postExecute();
		    				}catch(Throwable t) {
		    					
		    				}
							
						}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
    				
    			}catch(Exception e){
    				LogManager.getLogger().trace(e);
    				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
    					LogManager.getLogger().trace(MODULE, "Problem in scheduling Fixed Delay task, Reason: " + e.getMessage());
    			}
	    		
    		}else {
    			try{
    				scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
		    			@Override
						public void run() {
		    				try {
		    					task.preExecute();
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.execute();
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.postExecute();
		    				}catch(Throwable t) {
		    					
		    				}
							
						}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
    			}catch(Exception e){
    				LogManager.getLogger().trace(e);
    				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) 
    					LogManager.getLogger().trace(MODULE, "Problem in scheduling task reason: " + e.getMessage());
    			}
	    		
    		}
    	}
    }

	public void stopScheduler() {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Stopping DB Snmp Agent Scheduled task executor");
		
		if(scheduledThreadPoolExecutor != null){
			scheduledThreadPoolExecutor.shutdown();
		
			try {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Waiting for Scheduled task executor to complete execution");
			
				if(!scheduledThreadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Shutting down thread pool executer forcefully, Reason: Task taking more time to complete");
					scheduledThreadPoolExecutor.shutdownNow();
				}
				
			} catch (InterruptedException e) {
				scheduledThreadPoolExecutor.shutdownNow();
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "DB Snmp Agent Scheduled task executor stopped successfully");
		}
	}
}
