package com.elitecore.netvertex.service.pcrf;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.SessionReAuthUtil;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.ReschedulePcrfRequest;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;


/**
 * 
 * PCCRuleExpiryManager manages the session which will expire on specific time.</br>
 * 
 * It cache the the information like coresessionid, expiry time etc. for session which will expired on specific time.</br>
 * 
 * PCCRuleExpiryManager fetch the expiry time {@code getRevalidationTime()} method from {@link PCRFResponse}</br>
 * 
 * 
 * @author harsh patel
 *
 */
public class PCCRuleExpiryManager {
	
	private static final String MODULE = "PCC-RULE-EXPIRY-MGR";
	private static final int INITIAL_CAPACITY = 256;
	@Nonnull private final ConcurrentHashMap<String,ReschedulableSessionData> reauthorizableSessionToTime;
	@Nonnull private final NetVertexServerContext serverContext;
	@Nonnull private final SessionLocator  sessionLocator;
	private ShutdownHook hook;

	
	
	PCCRuleExpiryManager(NetVertexServerContext serverContext,SessionLocator sessionLocator) {
		Preconditions.checkNotNull(serverContext, "Server Context must not be null");
		Preconditions.checkNotNull(sessionLocator, "Session Locator must not be null");
		
		PCRFServiceConfiguration configuration = serverContext.getServerConfiguration().getPCRFServiceConfiguration();
		int concurrencyLevel = configuration.getMaximumThread() < 2 ? configuration.getMaximumThread() : configuration.getMaximumThread() / 2 ;
		
		this.serverContext = serverContext;
		this.sessionLocator = sessionLocator;
		this.reauthorizableSessionToTime = new ConcurrentHashMap<String, PCCRuleExpiryManager.ReschedulableSessionData>(INITIAL_CAPACITY,CommonConstants.DEFAULT_LOAD_FACTOR,concurrencyLevel);
	}
	
	void init() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initializing PCCRuleExpiryListener");
		
		Dispatcher dispatcher = new Dispatcher(reauthorizableSessionToTime);
		
		try{
			hook = new ShutdownHook(dispatcher);
			Runtime.getRuntime().addShutdownHook(hook);
		}catch(Exception ex){
			throw new InitializationFailedException("Error in adding shutdown hook. Reason: " + ex.getMessage(), ex);
		}
		
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(dispatcher);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "PCCRuleExpiryListener initialized successfully");
	}
		

	/**
	 * remove the session from cache.
	 * 
	 * @param pcrfResponse used to remove specific session from cache
	 */
	public void onStop(@Nonnull PCRFResponse pcrfResponse){
		
		if(LogManager.getLogger().isDebugLogLevel())
			LogManager.getLogger().debug(MODULE,"Removing session:"+ pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) 
											 +" from reauthorizable sessions cache");
		
		reauthorizableSessionToTime.remove(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
	}
	
	
	/**
	 * 
	 * cached the current session data for the expirty. If data is already cached, It will update the time found from {@code getRevalidationTime()} method from {@link PCRFResponse} 
	 * 
	 * 
	 * @param pcrfResponse use for fetch session data like core-session-id,revalidation time etc.
	 * @param pccRuleExpiryListener Listenable object which are used to call when time is expired 
	 */
	public void onInitialOrUpdate(@Nonnull PCRFResponse pcrfResponse, @Nonnull PCCRuleExpiryListener pccRuleExpiryListener){

		if (isEligibleToReSchedule(pcrfResponse) == false) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Skipping response scheduling for Session Id: "
						+ pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)
						+ ". Reason: It is not PCRF Session or respective PCRF Session for this OCS Session is already scheduled");
			}
			return;
		}

		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE,"NEXT SESSION TIME OUT "+ pcrfResponse.getRevalidationTime());
		}


		ReschedulableSessionData reschedulableSessionData = reauthorizableSessionToTime.get(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		
		if(reschedulableSessionData == null){
			/// CALLING "putIfAbsent" JUST TO AVOID RACE CONDITION
			reschedulableSessionData = new ReschedulableSessionData(pcrfResponse.getRevalidationTime().getTime(), pccRuleExpiryListener);
			ReschedulableSessionData exitsingReschedulableSessionData = reauthorizableSessionToTime.putIfAbsent(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val), reschedulableSessionData);
			
			if(exitsingReschedulableSessionData != null){
				reschedulableSessionData = exitsingReschedulableSessionData;
			}
			
		}
		
		reschedulableSessionData.update(pcrfResponse.getRevalidationTime().getTime(), pccRuleExpiryListener);

	}

	private boolean isEligibleToReSchedule(PCRFResponse pcrfResponse) {
		SessionTypeConstant sessionTypeConstant = SessionTypeConstant.fromValue(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));

		/**
		 * If It is PCRF Session, Always schedule for ReAuth
		 * As RADIUS has same session for PCRF and OCS, considered it as PCRF Session and it should always be schedule
		 */
		if (SessionReAuthUtil.isPCRFSession(sessionTypeConstant)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Eligible for reschedule. Reason: It is PCRF Session");
			}
			return true;
			/**
			 * If It is OCS Session and respective PCRF Session not found then it should schedule for ReAuth
			 */
		} else if (SessionReAuthUtil.isOCSSession(sessionTypeConstant) && pcrfResponse.getPccProfileSelectionState() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Eligible for reschedule. Reason: It is OCS Session and does not have respective PCRF Session");
			}
			return true;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Not eligible to reschedule. Reason: Session Type is " + sessionTypeConstant.val);
		}

		return false;
	}

	public void stop() {
		if(this.hook != null) {
			this.hook.execute();
		}
	}
	
	
	/**
	 * 
	 * @param serverContext 
	 * @param sessionLocator
	 * @return {@link PCCRuleExpiryManager}
	 * @throws InitializationFailedException 
	 */
	public static PCCRuleExpiryManager create(@Nonnull NetVertexServerContext serverContext, 
			@Nonnull SessionLocator sessionLocator) throws InitializationFailedException{
			
			PCCRuleExpiryManager pccRuleExpiryManager = new PCCRuleExpiryManager(serverContext, sessionLocator);
			pccRuleExpiryManager.init();
			return pccRuleExpiryManager;
	}
	
	
	
	
	
	/**
	 * 
	 * ShutdownHook used to stop the dispacher thread
	 * 
	 * @author harsh
	 *
	 */
	private class ShutdownHook extends Thread{
		private Dispatcher dispatcher;

		public ShutdownHook(Dispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}

		@Override
		public void run(){
			execute();
		}

		private void execute() {
			dispatcher.stop();
		}
	}
	
	/**
	 * 
	 * Dispatcher, dispatch the tasks which are eligible for re-authorization and scheduled for execute.
	 * 
	 * @author harsh
	 *
	 */
	class Dispatcher extends BaseIntervalBasedTask{
		private static final int CAPACITY = 500;
		private ExecutorService threadPoolExecutor;
		private Map<String, ReschedulableSessionData> reauthorizableSessionToTime;
		private LinkedBlockingQueue<Runnable> blockingQueue;
		
		public Dispatcher(Map<String,ReschedulableSessionData> reauthorizableSessionToTime){
			this.reauthorizableSessionToTime = reauthorizableSessionToTime;
			this.blockingQueue = new LinkedBlockingQueue<Runnable>(500);
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES,blockingQueue);
			threadPoolExecutor.setThreadFactory(new EliteThreadFactory("PCC-EXPIRY-MGR-DISPTCHR", "PCC-EXPIRY-MGR-DISPTCHR", Thread.NORM_PRIORITY));
			this.threadPoolExecutor = threadPoolExecutor;
		}


		@Override
		public void execute(AsyncTaskContext context) {
			
			
			long currentTime = System.currentTimeMillis();
			
			if(isQueueFull()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping processing of session for timebase reauthorization. Reason:Queue is full");
				
				return;
			}
			
			
			
			for(Entry<String, ReschedulableSessionData> entry :  reauthorizableSessionToTime.entrySet()){
				
				if(Thread.interrupted()){
					//stop is called
					break;
				}
				
				if(isQueueFull()){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping processing of session for timebase reauthorization. Reason:Queue is full");
					break;
				}

				if (currentTime < entry.getValue().getTime()) {
					continue;
				}

				ReschedulableSessionData data = reauthorizableSessionToTime.remove(entry.getKey());
				
				/*
				 * data can be null, If "onStop" is called for the data during iteration
				 */
				if(data != null){
					try {
						threadPoolExecutor.execute(new ReschedulePcrfRequest(entry.getKey(),
								serverContext,sessionLocator, data.getPccRuleExpiryListener()));
					} catch (RejectedExecutionException e) {
						LogManager.ignoreTrace(e);
						break;
					}
				}
			
			}
			
			
			
		}
		
		private boolean isQueueFull() {
			return blockingQueue.size() > CAPACITY;
		}


		public void stop(){
			try{
				threadPoolExecutor.shutdown();
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Waiting for PCCRuleExpiryManager level scheduled async task executor to complete execution");
				if(threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS) == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Shutting down PCCRuleExpiryManager level scheduled async task executor forcefully. " +
								"Reason: Async task taking more than 5 second to complete");
					threadPoolExecutor.shutdownNow();
				}
			}catch(Exception ex){ 
				ignoreTrace(ex);
				try {
					threadPoolExecutor.shutdownNow();
				} catch (Exception e) {
					ignoreTrace(e);
				}
			}
		}
		
		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public long getInterval() {
			return 1;
		}
	}
	
	
	class ReschedulableSessionData {
		private Long timeInMillies;
		private PCCRuleExpiryListener pccRuleExpiryListener;

		public ReschedulableSessionData(Long timeInMillies,
				PCCRuleExpiryListener pccRuleExpiryListener) {
			super();
			this.timeInMillies = timeInMillies;
			this.pccRuleExpiryListener = pccRuleExpiryListener;
		}
		
		public PCCRuleExpiryListener getPccRuleExpiryListener() {
			return pccRuleExpiryListener;
		}
		
		public long getTime() {
			return timeInMillies;
		}

		private void update(Long timeInMillies,PCCRuleExpiryListener pccRuleExpiryListener){
			this.timeInMillies = timeInMillies;
			this.pccRuleExpiryListener = pccRuleExpiryListener;
		}
		
	}
}



