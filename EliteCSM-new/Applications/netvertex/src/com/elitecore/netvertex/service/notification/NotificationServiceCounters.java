package com.elitecore.netvertex.service.notification;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public class NotificationServiceCounters {

	private static final String MODULE = "NOTIFICATION-SRV-COUNTERS";

	private NotificationServiceContext notificationServiceContext;

	private long serviceUpTime;
	private long statisicsResetTime;
	public static final int STATS_OTHER = 1;
	public static final int STATS_RESET = 2;
	private final int STATS_INITIALIZING = 3;
	private final int STATS_RUNNING = 4;
	private int statisticsReset = STATS_OTHER;

	private AtomicLong totalNotificationProcessed;
	private AtomicLong totalEmailProcessed;
	private AtomicLong totalEmailSent;
	private AtomicLong totalEmailFailures;
	private AtomicLong totalEmailTemplateParsingFailures;

	private AtomicLong totalSMSProcessed;
	private AtomicLong totalSMSSent;
	private AtomicLong totalSMSFailures;
	private AtomicLong totalSMSTemplateParsingFailures;

	private AtomicLong totalNotificationProcessedToday;
	private AtomicLong totalEmailProcessedToday;
	private AtomicLong totalEmailSentToday;
	private AtomicLong totalEmailFailuresToday;
	private AtomicLong totalEmailTemplateParsingFailuresToday;

	private AtomicLong totalSMSProcessedToday;
	private AtomicLong totalSMSSentToday;
	private AtomicLong totalSMSFailuresToday;
	private AtomicLong totalSMSTemplateParsingFailuresToday;

	private AtomicLong totalNotificationProcessedYesterday;

	private AtomicLong totalEmailProcessedYesterday;              
	private AtomicLong totalEmailSentYesterday;                   
	private AtomicLong totalEmailFailuresYesterday;               
	private AtomicLong totalEmailTemplateParsingFailuresYesterday;

	private AtomicLong totalSMSProcessedYesterday;	
	private AtomicLong totalSMSSentYesterday;                     
	private AtomicLong totalSMSFailuresYesterday;                 
	private AtomicLong totalSMSTemplateParsingFailuresYesterday;  
	private NotificationStatisticsUpdater notificationStatisticsUpdater;
	public NotificationServiceCounters(NotificationServiceContext notificationServiceContext) {
		statisticsReset = STATS_INITIALIZING;
		this.notificationServiceContext = notificationServiceContext;
		this.currentHour = new Date().getHours();
		notificationStatisticsUpdater = new NotificationStatisticsUpdater();
		totalEmailSent = new AtomicLong();
		totalEmailFailures = new AtomicLong();
		totalEmailTemplateParsingFailures = new AtomicLong();
		totalNotificationProcessed = new AtomicLong();
		totalSMSProcessed = new AtomicLong();
		totalEmailProcessed = new AtomicLong();
		totalSMSSent = new AtomicLong();
		totalSMSFailures = new AtomicLong();
		totalSMSTemplateParsingFailures = new AtomicLong();


		totalNotificationProcessedToday = new AtomicLong();       
		totalEmailProcessedToday = new AtomicLong();              
		totalSMSProcessedToday = new AtomicLong();                
		totalEmailSentToday = new AtomicLong();                   
		totalEmailFailuresToday = new AtomicLong();               
		totalEmailTemplateParsingFailuresToday = new AtomicLong();
		totalSMSSentToday = new AtomicLong();                     
		totalSMSFailuresToday = new AtomicLong();                 
		totalSMSTemplateParsingFailuresToday = new AtomicLong();


		totalNotificationProcessedYesterday = new AtomicLong();       
		totalEmailProcessedYesterday = new AtomicLong();              
		totalSMSProcessedYesterday = new AtomicLong();                
		totalEmailSentYesterday = new AtomicLong();                   
		totalEmailFailuresYesterday = new AtomicLong();               
		totalEmailTemplateParsingFailuresYesterday = new AtomicLong();
		totalSMSSentYesterday = new AtomicLong();                     
		totalSMSFailuresYesterday = new AtomicLong();                 
		totalSMSTemplateParsingFailuresYesterday = new AtomicLong();  

		serviceUpTime = System.currentTimeMillis();
		statisicsResetTime = System.currentTimeMillis();
		statisticsReset = STATS_RUNNING;
	}
	public void incTotalNotificationProcessed() {
		totalNotificationProcessed.incrementAndGet();
		totalNotificationProcessedToday.incrementAndGet();
	}
	public void incTotalEmailProcessed() {
		totalEmailProcessed.incrementAndGet();
		totalEmailProcessedToday.incrementAndGet();
	}
	public void incTotalSMSProcessed() {
		totalSMSProcessed.incrementAndGet();
		totalSMSProcessedToday.incrementAndGet();
	}
	public void incTotalEmailSentCntr() {
		totalEmailSent.incrementAndGet();
		totalEmailSentToday.incrementAndGet();
	}
	public void incTotalEmailFailuresCntr() {
		totalEmailFailures.incrementAndGet();
		totalEmailFailuresToday.incrementAndGet();
	}
	public void init() {
		notificationServiceContext.getServerContext().getTaskScheduler().scheduleIntervalBasedTask(notificationStatisticsUpdater);
	}
	public void incTotalEmailTemplateParsingFailuresCntr() {
		totalEmailTemplateParsingFailures.incrementAndGet();
		totalEmailTemplateParsingFailuresToday.incrementAndGet();
	}
	public void incTotalSMSSentCntr() {
		totalSMSSent.incrementAndGet();
		totalSMSSentToday.incrementAndGet();
	}
	public void incTotalSMSFailuresCntr() {
		totalSMSFailures.incrementAndGet();
		totalSMSFailuresToday.incrementAndGet();
	}
	public void incTotalSMSTemplateParsingFailuresCntr() {
		totalSMSTemplateParsingFailures.incrementAndGet();
		totalSMSTemplateParsingFailuresToday.incrementAndGet();
	}
	public long getServiceUpTime() {
		return serviceUpTime;
	}
	public long getStatisicsResetTime() {
		return statisicsResetTime;
	}
	public int getStatisticsReset() {
		return statisticsReset;
	}

	public synchronized void resetCurrentDayStatsAndUpdtaeLastDayStats() {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Resetting today notification statistics");
		}
		totalNotificationProcessedYesterday.set(totalNotificationProcessedToday.getAndSet(0));        
		totalEmailProcessedYesterday.set(totalEmailProcessedToday.getAndSet(0));
		totalSMSProcessedYesterday.set(totalSMSProcessedToday.getAndSet(0));                 
		totalEmailSentYesterday.set(totalEmailSentToday.getAndSet(0));                    
		totalEmailFailuresYesterday.set(totalEmailFailuresToday.getAndSet(0));                
		totalEmailTemplateParsingFailuresYesterday.set(totalEmailTemplateParsingFailuresToday.getAndSet(0)); 
		totalSMSSentYesterday.set(totalSMSSentToday.getAndSet(0));                      
		totalSMSFailuresYesterday.set(totalSMSFailuresToday.getAndSet(0));                  
		totalSMSTemplateParsingFailuresYesterday.set(totalSMSTemplateParsingFailuresToday.getAndSet(0));
		LogManager.getLogger().debug(MODULE, "Today notification statistics reset successfull");
	}

	public synchronized void resetStatistics() {

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Resetting Notification service statistics");

		statisticsReset = STATS_INITIALIZING;
		totalNotificationProcessed.set(0);

		totalEmailProcessed.set(0);
		totalEmailSent.set(0);
		totalEmailFailures.set(0);
		totalEmailTemplateParsingFailures.set(0);

		totalSMSProcessed.set(0);
		totalSMSSent.set(0);
		totalSMSFailures.set(0);
		totalSMSTemplateParsingFailures.set(0);

		totalNotificationProcessedToday.set(0);

		totalEmailProcessedToday.set(0);                   
		totalEmailSentToday.set(0);                        
		totalEmailFailuresToday.set(0);                    
		totalEmailTemplateParsingFailuresToday.set(0);

		totalSMSProcessedToday.set(0);
		totalSMSSentToday.set(0);                          
		totalSMSFailuresToday.set(0);                      
		totalSMSTemplateParsingFailuresToday.set(0);       

		totalNotificationProcessedYesterday.set(0);

		totalEmailProcessedYesterday.set(0);               
		totalEmailSentYesterday.set(0);                    
		totalEmailFailuresYesterday.set(0);                
		totalEmailTemplateParsingFailuresYesterday.set(0);

		totalSMSProcessedYesterday.set(0);  
		totalSMSSentYesterday.set(0);                      
		totalSMSFailuresYesterday.set(0);                  
		totalSMSTemplateParsingFailuresYesterday.set(0);   

		statisicsResetTime = System.currentTimeMillis();
		statisticsReset = STATS_RUNNING;

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Reset operation for Notification service statistics is completed successfully");
	}

	public long getTotalNotificationProcessed() {
		return totalNotificationProcessed.get();
	}
	public long getTotalEmailProcessed() {
		return totalEmailProcessed.get();
	}
	public long getTotalSMSProcessed() {
		return totalSMSProcessed.get();
	}
	public long getTotalEmailSent() {
		return totalEmailSent.get();
	}
	public long getTotalEmailFailures() {
		return totalEmailFailures.get();
	}
	public long getTotalEmailTemplateParsingFailures() {
		return totalEmailTemplateParsingFailures.get();
	}
	public long getTotalSMSSent() {
		return totalSMSSent.get();
	}
	public long getTotalSMSFailures() {
		return totalSMSFailures.get();
	}
	public long getTotalSMSTemplateParsingFailures() {
		return totalSMSTemplateParsingFailures.get();
	}
	public long getTotalNotificationProcessedToday() {
		return totalNotificationProcessedToday.get();
	}
	public long getTotalEmailProcessedToday() {
		return totalEmailProcessedToday.get();
	}
	public long getTotalSMSProcessedToday() {
		return totalSMSProcessedToday.get();
	}
	public long getTotalEmailSentToday() {
		return totalEmailSentToday.get();
	}
	public long getTotalEmailFailuresToday() {
		return totalEmailFailuresToday.get();
	}
	public long getTotalEmailTemplateParsingFailuresToday() {
		return totalEmailTemplateParsingFailuresToday.get();
	}
	public long getTotalSMSSentToday() {
		return totalSMSSentToday.get();
	}
	public long getTotalSMSFailuresToday() {
		return totalSMSFailuresToday.get();
	}
	public long getTotalSMSTemplateParsingFailuresToday() {
		return totalSMSTemplateParsingFailuresToday.get();
	}
	public long getTotalNotificationProcessedYesterday() {
		return totalNotificationProcessedYesterday.get();
	}
	public long getTotalEmailProcessedYesterday() {
		return totalEmailProcessedYesterday.get();
	}
	public long getTotalSMSProcessedYesterday() {
		return totalSMSProcessedYesterday.get();
	}
	public long getTotalEmailSentYesterday() {
		return totalEmailSentYesterday.get();
	}
	public long getTotalEmailFailuresYesterday() {
		return totalEmailFailuresYesterday.get();
	}
	public long getTotalEmailTemplateParsingFailuresYesterday() {
		return totalEmailTemplateParsingFailuresYesterday.get();
	}
	public long getTotalSMSSentYesterday() {
		return totalSMSSentYesterday.get();
	}
	public long getTotalSMSFailuresYesterday() {
		return totalSMSFailuresYesterday.get();
	}
	public long getTotalSMSTemplateParsingFailuresYesterday() {
		return totalSMSTemplateParsingFailuresYesterday.get();
	}
	public Long getTotalSMSSProcessedToday() {
		return totalSMSProcessedToday.get();
	}
	public Long getTotalSMSSProcessedYesterday() {
		return totalSMSProcessedYesterday.get();
	}
	
	private int currentHour;
	private class NotificationStatisticsUpdater extends BaseIntervalBasedTask {
		
		@Override
		public long getInitialDelay() {
			Calendar nextMinute = Calendar.getInstance();
			long currentTimeMillis = nextMinute.getTimeInMillis();
			nextMinute.set(Calendar.MINUTE, nextMinute.get(Calendar.MINUTE) + 1);
			nextMinute.set(Calendar.SECOND, 01);
			nextMinute.set(Calendar.MILLISECOND, 00);
			long nextMinuteDiffSeconds = (nextMinute.getTimeInMillis() - currentTimeMillis)/1000;
			LogManager.getLogger().info(MODULE, "Initial delay: "  + nextMinuteDiffSeconds + " Seconds");
			return nextMinuteDiffSeconds;
		}
		
		@Override
		public long getInterval() {
			return 60;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public void execute(AsyncTaskContext context){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Executing notification statistics updater");
			}
			Date cuerrentTime = new Date();
			int newHour = cuerrentTime.getHours();
			int oldHour = currentHour;
			currentHour = newHour;
			if(oldHour != newHour) {
				if(oldHour == 23 && newHour == 0) {
					resetCurrentDayStatsAndUpdtaeLastDayStats();
				}
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Notification statistics updater execution completed");
			}
		}
	}
}