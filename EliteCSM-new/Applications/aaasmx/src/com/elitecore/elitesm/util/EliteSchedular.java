package com.elitecore.elitesm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.elitesm.util.logger.Logger;

public class EliteSchedular {
	
	private static final String MODULE = null;
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	
	public EliteSchedular(int poolSize) {
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(poolSize, new EliteThreadFactory("SVR-SCH","SVR-SCH", Thread.NORM_PRIORITY));
	}
	
	public static EliteSchedular createEliteSchedular(int poolSize) {
		return new EliteSchedular(poolSize);
	}  

	public void scheduleIntervalBasedTask(final IntervalBasedTask task) {
    	if (task != null) {
    		if (task.isFixedDelay()) {
    			try{
    				scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
		    			@Override
						public void run() {
		    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
		    				
		    				try {
		    					task.preExecute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.execute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.postExecute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
						}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
    				
    			}catch(Exception e){
    				Logger.logError(MODULE, "Error in scheduling Fixed Delay task reason: " + e.getMessage());
    				Logger.logTrace(MODULE,e);
    			}
	    		
    		}else {
    			try{
    				scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
		    			@Override
						public void run() {
		    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
		    				try {
		    					task.preExecute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.execute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
		    				try {
		    					task.postExecute(taskContext);
		    				}catch(Throwable t) {
		    					
		    				}
							
						}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
    			}catch(Exception e){
    				Logger.logError(MODULE, "Error in scheduling Fixed Delay task reason: " + e.getMessage());
    				Logger.logTrace(MODULE,e);
    			}
	    		
    		}
    	}
    }
	
	private class AsyncTaskContextImpl implements AsyncTaskContext {
		
		private Map<String, Object> attributes;
		
		public AsyncTaskContextImpl() {
		}

		@Override
		public void setAttribute(String key, Object attribute) {
			if (attributes == null) {
				synchronized (attributes) {
					if (attributes == null) {
						attributes = new HashMap<String, Object>();
					}
				}
			}
			attributes.put(key, attribute);
		}

		@Override
		public Object getAttribute(String key) {
			if (attributes != null) {
				return attributes.get(key);
			}
			return null;
		}
		
	}
	
	public void stop() {
		scheduledThreadPoolExecutor.shutdown();
	}
    
}
