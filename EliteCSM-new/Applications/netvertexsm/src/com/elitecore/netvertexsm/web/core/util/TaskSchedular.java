package com.elitecore.netvertexsm.web.core.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.elitecore.netvertexsm.util.logger.Logger;

public class TaskSchedular {
	
		private final String MODULE = this.getClass().getName();
		private final ExecutorService executorService = Executors.newFixedThreadPool(5);
		private static TaskSchedular taskSchedular;
		
		static{
			taskSchedular = new TaskSchedular();
			taskSchedular.init();
		}
		public static TaskSchedular getInstance(){
			return taskSchedular;
		}
		private void init(){
			try{
				Runtime.getRuntime().addShutdownHook(new ShutdownHook(taskSchedular));
				Logger.logInfo(MODULE, "shutdown hook initialized successfully");
			} catch(Exception e){
				Logger.logWarn(MODULE, "Error in adding shutdown hook. Reason: " + e.getMessage());
			}
		}
		public <T> Future<T> submit(Callable<T> callable){
			return executorService.submit(callable);
		}
		
		private class ShutdownHook extends Thread{
			private TaskSchedular taskSchedular;

			public ShutdownHook(TaskSchedular taskSchedular) {
				this.taskSchedular = taskSchedular;
			}
			public void run(){
				taskSchedular.stop();
			}
		}
		public void stop(){
			try{
				executorService.shutdown();
				Logger.logInfo(MODULE, "Waiting for TaskSchedular async task executor to complete execution ");
				if(executorService.awaitTermination(5, TimeUnit.SECONDS) == false){
					Logger.logError(MODULE, "Shutting down TaskSchedular async task executor forcefully. " +
							"Reason: Async task taking more than 5 seconds to complete");
					executorService.shutdownNow();
				}
			}catch(Exception ex){
				try{executorService.shutdownNow();}catch(Exception e){}
			}
		}
}
