package com.elitecore.netvertexsm.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class EliteScheduler {
	
	private ScheduledThreadPoolExecutor scheduleThreadPoolExecutor;
	private static final String MODULE = "ELITE-SCHEDULER";
	private static EliteScheduler eliteScheduler;
	private AtomicBoolean shutdownInProgress;
	
	public final TaskScheduler TASK_SCHEDULER = new TaskScheduler() {
		
		@Override
		public Future<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
			return EliteScheduler.this.scheduleSingleExecutionTask(task);
		}
		
		@Override
		public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
			return EliteScheduler.this.scheduleIntervalBasedTask(task);
		}

		@Override
		public void execute(Runnable command) {
			EliteScheduler.this.execute(command);
			
		}
	};
	
	static{
		eliteScheduler = new EliteScheduler();		
	}
	
	
	private EliteScheduler(){
		scheduleThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
		shutdownInProgress = new AtomicBoolean(false);
	}
	
	public static EliteScheduler getInstance(){
		return eliteScheduler;
	}

	public Future<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
		if (task == null) {
			return null;
		}

		Future<?> future = null;
		if (task.getInitialDelay() > 0) {
			future = scheduleThreadPoolExecutor.schedule(new Runnable(){
				@Override
				public void run() {
					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
					try {
						task.execute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}
				}}, task.getInitialDelay(), task.getTimeUnit());
		} else {
			future = scheduleThreadPoolExecutor.submit(new Runnable() {

				@Override
				public void run() {
					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
					try {
						task.execute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}
				}});
		}
		return future;
    }
    
	public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {

		if (task == null) {
			return null;
		}

		Future<?> future = null;
		if (task.isFixedDelay()) {
			future = scheduleThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
				@Override
				public void run() {
					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();

					try {
						task.preExecute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

					try {
						task.execute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

					try {
						task.postExecute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
		}else {
			future = scheduleThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run() {
					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
					try {
						task.preExecute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

					try {
						task.execute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

					try {
						task.postExecute(taskContext);
					}catch(Throwable t) {
						Logger.logTrace(MODULE, t);
					}

				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
		}
		return future;
    }
	
    
    public class AsyncTaskContextImpl implements AsyncTaskContext {
		
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
    
    public void shutdown(){
    	try{
    		
    		if(!shutdownInProgress.compareAndSet(false, true)){
    			return;
    		}
    		
			scheduleThreadPoolExecutor.shutdown();
			Logger.logInfo(MODULE, "Waiting for CDRDriverManager level scheduled async task executor to complete execution");
			if(!scheduleThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)){
				Logger.logWarn(MODULE, "Shutting down CDRDriverManager level scheduled async task executor forcefully. " +
							"Reason: Async task taking more than 5 second to complete");
				scheduleThreadPoolExecutor.shutdownNow();
			}
		}catch(Exception ex){
			try{scheduleThreadPoolExecutor.shutdownNow();}catch(Exception e){}
		}
    }

	public void execute(Runnable command) {
        scheduleThreadPoolExecutor.execute(command);
		
	}

	public TaskScheduler getTaskSchedular() {
		return TASK_SCHEDULER;
	}
	
}
