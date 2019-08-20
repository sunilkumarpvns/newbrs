package com.elitecore.core.servicex;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.base.BaseEliteService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class EliteScheduledService<T> extends BaseEliteService {

	private static final String MODULE = "BASE-SCHEDULED-SERVICE";
	private final EliteScheduledServiceConf conf;

	private ThreadPoolExecutor asynchronousTaskExecutor;
    
    private ScheduledExecutorService scheduledThreadPoolExecutor;
    
    private AtomicLong batchProcessorCounter;
    
    private boolean initialized = false;

	public EliteScheduledService(ServerContext ctx, EliteScheduledServiceConf conf) {
		super(ctx);
		this.conf = conf;
		batchProcessorCounter = new AtomicLong();
	}
	
	@Override
	protected void initService() throws ServiceInitializationException {
		if(initialized){
			return;
		}

		asynchronousTaskExecutor = new ThreadPoolExecutor(0,
				conf.getMaxParallelExecutions() + 100 /* +100 is to avoid rejected exception for now*/,
				60 * 1000, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		
		asynchronousTaskExecutor.setThreadFactory(new EliteThreadFactory(getServiceIdentifier(),getServiceIdentifier()+"-", Thread.MAX_PRIORITY));
		asynchronousTaskExecutor.prestartAllCoreThreads();

		/*
		 * There is no reason behind taking 3 as core pool size. We need to take more than one thread so take 3 that it. -- harsh patel
		 */
		scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(3, new EliteThreadFactory("SCHEDULED-SERVICE-","SCHEDULED-SERVICE-", Thread.NORM_PRIORITY));
		scheduledThreadPoolExecutor.scheduleAtFixedRate(new ScheduledTask(asynchronousTaskExecutor), conf.getInitialeDelay(),
				conf.getServiceExecutionInterval(), TimeUnit.SECONDS);
		
		initialized = true;
	

	}
	
	@Override
	protected boolean startService() {
		return true;
	}
	
	
	private class ScheduledTask implements Runnable {
		
		private final Executor executor;
		
		public ScheduledTask(Executor executor) {
			super();
			this.executor = executor;
		}

		@Override
		public void run() {

			try {

				preScheduleExecution();

				doInitScheduleExecution();

				List<T> targetEntityList = getMoreTargetEntitiesForSession();

				if (targetEntityList == null || targetEntityList.isEmpty()) {
					return;
				}

				CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executor);

				int noOfTaskCreated = scheduledTask(targetEntityList, completionService);

				waitForAllTaskToComplete(completionService, noOfTaskCreated);
				
			} catch (Exception ex) {
				getLogger().error(MODULE, "Error while executing scheduled task. Reason: " + ex.getMessage());
				getLogger().trace(MODULE, ex);
			} 
		}



		private int calculateBatchSize(List<T> targetEntityList) {
			int batchSize = 0;
			
			
			if(isDivisable(targetEntityList)) {
				batchSize = targetEntityList.size() / conf.getMaxParallelExecutions();
			} else {
				batchSize = (targetEntityList.size() / conf.getMaxParallelExecutions()) + 1;
			}

			if(getLogger().isInfoLogLevel()) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter out = new PrintWriter(stringWriter);

				out.println("***********************************************");
				out.println("total request : " + targetEntityList.size());
				out.println("batch size    : " + batchSize);
				out.println("***********************************************");

				getLogger().debug(MODULE, stringWriter.toString());
			}


			
			return batchSize;
		}



		private boolean isDivisable(List<T> targetEntityList) {
			return targetEntityList.size() % conf.getMaxParallelExecutions() == 0;
		}


		private int scheduledTask(List<T> targetEntityList, CompletionService<Boolean> completionService) {
			
			final int batchSize = calculateBatchSize(targetEntityList);

			int noOfTaskCreated = 0;
			int totalEntities = targetEntityList.size();

			int index = 0;
			while(index < totalEntities) {
				
				List<T> batchEntityList = new ArrayList<T>();
				for (int j = 0; j < batchSize && index < totalEntities; j++) {
					batchEntityList.add(targetEntityList.get(index++));
				}
				completionService.submit(new BatchExecutor(batchEntityList),Boolean.TRUE);
				noOfTaskCreated++;
			}


			return noOfTaskCreated;
		}



		private void waitForAllTaskToComplete(CompletionService<Boolean> completionService, int noOfTaskCreated) throws InterruptedException {
			for(int i= 0 ; i < noOfTaskCreated; i++){
				completionService.take();
			}
		}
	}

	private class BatchExecutor implements Runnable {

		private final List<T> entityBatch;

		public BatchExecutor(List<T> entityBatch) {
			this.entityBatch = entityBatch;
			batchProcessorCounter.incrementAndGet();
		}

		@Override
		public void run() {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Batch starting for : " + entityBatch.size());

			int count = 0;

			for (T entity : entityBatch) {
				try {
					doProcessTargetEntity(entity);
					count++;
				} catch (Exception throwable) {
					getLogger().trace(MODULE, throwable);
				}
			}

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Batch completed : " + count);
		}
	}
	
	
	protected abstract void preScheduleExecution();
	
	protected abstract void doInitScheduleExecution();
	
	protected abstract List<T> getMoreTargetEntitiesForSession();
	
	protected abstract void doProcessTargetEntity(T targetEntity);

	protected abstract void postScheduleExecution();


	public static class TargetEntity {
		
		String id;
		
		public TargetEntity(String id) {
			this.id = id;
		}
		
		
		public final String getId() {
			return id;
		}
		
	}
	

	@Override
	protected ServiceContext getServiceContext() {
		return null;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				"N.A.", getStartDate(), getRemarks());
	}
	
	@Override
	public boolean stopService() {
		return true;
	}
	
	@Override
	protected void shutdownService() {
		shutdownSyncTask();
		shutdownAsynchTask();

	}

	private void shutdownSyncTask() {
		if(scheduledThreadPoolExecutor!=null){
			scheduledThreadPoolExecutor.shutdown();
			getLogger().info(MODULE, getServiceIdentifier() + " - shutdown requested for sync task executor ");
			try {
				if(scheduledThreadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS) == false){
					getLogger().info(MODULE, "Shutting down thread pool executer forcefully, Reason: sync task taking more time to complete");
					scheduledThreadPoolExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduledThreadPoolExecutor.shutdownNow();
				Thread.currentThread().interrupt();
			}
			getLogger().info(MODULE, getServiceIdentifier() + " - sync task executor terminated");
		}
	}

	private void shutdownAsynchTask() {
		if(asynchronousTaskExecutor!=null){
			asynchronousTaskExecutor.shutdown();
			LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - shutdown requested for async task executor ");
			try {
				if(asynchronousTaskExecutor.awaitTermination(2, TimeUnit.SECONDS) == false){
					getLogger().info(MODULE, "Shutting down thread pool executer forcefully, Reason: Async task taking more time to complete");
					asynchronousTaskExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				asynchronousTaskExecutor.shutdownNow();
				Thread.currentThread().interrupt();
			}
			LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - async task executor terminated");
		}
	}

	

}
