package com.elitecore.test.util;

import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.ErrorHandler;


public abstract class AsynchTask implements SingleExecutionAsyncTask {
	private static final String MODULE = "ASY-TASK";
	private long delay;
	private TimeUnit timeUnit;
	private ErrorHandler handler;
	

	public AsynchTask(long delay, TimeUnit timeUnit, ErrorHandler handler) {
		super();
		this.delay = delay;
		this.timeUnit = timeUnit;
		this.handler = handler;
	}

	@Override
	public long getInitialDelay() {
		return delay;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	@Override
	public final void execute() {
		try{
			run();
			System.out.println("TASK COMPLETED");
		}catch(Throwable ex){
			if(handler != null){
				handler.handleError(ex);
			}
			
			System.out.println("Abort task. Reason:" + ex.getMessage());
			LogManager.getLogger().trace(MODULE,ex);
		}
	}
	
	public abstract void run() throws Exception;
	

}
