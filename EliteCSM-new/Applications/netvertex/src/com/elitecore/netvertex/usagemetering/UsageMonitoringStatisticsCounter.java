package com.elitecore.netvertex.usagemetering;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.util.SerializationUtil;
import com.elitecore.netvertex.core.NetVertexServerContext;

public class UsageMonitoringStatisticsCounter {

	private static final String MODULE = "USAGE-MONTR-STATS-CNTR";
	private static final String USAGESTATISTICS_FILENAME = "usagestatistics.ser";
	private NetVertexServerContext serverContext;
	private AtomicLong[]  hourlyUsageCounters;
	private long  lastDayUsge;
	private int currentHour;
	private transient long statisicsResetTime;
	private UsageStatisticsUpdater usageStatisticsUpdater;
	
	private final String usageSerializationFilePath;
	
	public UsageMonitoringStatisticsCounter(NetVertexServerContext serverContext) {
		this.serverContext= serverContext;
		this.usageStatisticsUpdater = new UsageStatisticsUpdater();
		this.usageSerializationFilePath = new StringBuilder(serverContext.getServerHome()).append(File.separator).append("system").append(File.separator)
				.append(USAGESTATISTICS_FILENAME).toString();
		this.hourlyUsageCounters = new AtomicLong[24];
	}

	public void init() {
		
		this.currentHour = getCurrentDate().get(Calendar.HOUR_OF_DAY);
		for (int index = 0 ; index < hourlyUsageCounters.length ; index++) {
			hourlyUsageCounters[index] = new AtomicLong();
		}

		UsageStatisticsData oldStatisticData = null;
		try {
			oldStatisticData = (UsageStatisticsData) SerializationUtil.deserialize(usageSerializationFilePath);			
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while deserializing usage counter. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		
		if (oldStatisticData != null) {
			
			Calendar currentTime = getCurrentDate();
			Calendar lastSyncTime = Calendar.getInstance();
			lastSyncTime.setTimeInMillis(oldStatisticData.lastSyncTimeInMillis);
			
			int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
			int lastSyncHour = lastSyncTime.get(Calendar.HOUR_OF_DAY);
			/*
			 * IF last sync performed in same hour THEN
			 * 	start from previous counter
			 * ELSE IF last sync performed within 24 hour THEN
			 * 		IF is day changed THEN
			 * 			calculate previous day usage
			 * 	set usage counter zero for difference hour
			 *    
			 *    Example:
			 * 	     last sync on time 1.35
			 *       restart on time 6.22
			 *  
			 *       set usage counter zero for hour 2 to 6
			 * ELSE 
			 * 	IF next startTime is before end of next day THEN
			 * 		calculate previous day usage counters
			 * 
			 */
			
			if (isTimeFallingInSameHour(currentTime, lastSyncTime)) {
				this.hourlyUsageCounters = oldStatisticData.hourlyUsageCounters;
				this.lastDayUsge = oldStatisticData.lastDayUsage;
			} else if (isTimeDiffIsWithin24hour(currentTime, lastSyncTime)) {
				this.hourlyUsageCounters = oldStatisticData.hourlyUsageCounters;
				this.lastDayUsge = oldStatisticData.lastDayUsage;

				/// is day changed
				if (currentHour < lastSyncHour) {
					long usage = 0;
					for (int index = 0; index <= lastSyncHour; index++) {
						usage += oldStatisticData.hourlyUsageCounters[index].get();
					}
					
					this.lastDayUsge  = usage;
				}

				int diffInHour = (int) Math.abs(getTimeDifferenceInHours(lastSyncTime, currentTime));
				for (int index = lastSyncHour + 1, hour=index; index <= lastSyncHour + diffInHour; index++, hour++) {
					if (hour == 24) {
						hour = 0;
					}
					hourlyUsageCounters[hour] = new AtomicLong();
				}
				

			} else {
				Calendar nextDayEndTime = (Calendar) lastSyncTime.clone();
				nextDayEndTime.add(Calendar.DAY_OF_MONTH, 1);
				nextDayEndTime.set(Calendar.HOUR_OF_DAY, 23);
				nextDayEndTime.set(Calendar.MINUTE, 59);
				nextDayEndTime.set(Calendar.SECOND, 59);
				nextDayEndTime.set(Calendar.MILLISECOND, 999);
				
				// next start time is next day of last sync time, we have to manage previous day usage
				if (currentTime.before(nextDayEndTime)) {
					long usage = 0;
					for(int index = 0 ; index <= lastSyncHour ; index++){
						usage += oldStatisticData.hourlyUsageCounters[index].get();
					}
					
					this.lastDayUsge  = usage;
				}
			}
		}
		this.statisicsResetTime = System.currentTimeMillis(); 
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(usageStatisticsUpdater);
	}

	private boolean isTimeFallingInSameHour(Calendar currentTime, Calendar lastSyncTime) {
		return  (currentTime.get(Calendar.HOUR_OF_DAY) == lastSyncTime.get(Calendar.HOUR_OF_DAY))
				&& (TimeUnit.MILLISECONDS.toHours(currentTime.getTimeInMillis() - lastSyncTime.getTimeInMillis())) < 1;
	}

	private boolean isTimeDiffIsWithin24hour(Calendar currentTime, Calendar lastSyncTime) {
		return TimeUnit.MILLISECONDS.toHours(currentTime.getTimeInMillis() - lastSyncTime.getTimeInMillis()) < 24;
	}

	private long getTimeDifferenceInHours(Calendar lastSyncTime, Calendar currentTime) {
		return TimeUnit.MILLISECONDS.toHours(lastSyncTime.getTimeInMillis() - currentTime.getTimeInMillis());
	}

	private long getHourlyUsage(int hour) {
		return hourlyUsageCounters[hour].get();
	}

	public void incrementHourlyUsage(long usage) {
		try{
			this.hourlyUsageCounters[currentHour].addAndGet(usage);
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while incrementing hourly usage (Hour: "+ currentHour + ",Usage: " + usage
					+"). Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void resetHourlyUsage(int hour) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Resetting usage counters for hour: " + hour);
		}
		try{
			hourlyUsageCounters[hour].set(0);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Usage counters for hour: " + hour + " reset successfull");
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while resetting usage counters for hour: " + hour+ 
					" Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	public long getCurrentDayUsage(){
		Calendar currentTime = getCurrentDate();
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		long usage = 0;
		for(int hour = 0 ; hour <= currentHour ; hour++){
			usage += hourlyUsageCounters[hour].get();
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Usage for current day(" + 1 + " to " + (currentHour + 1) + 
				" hours) : " + usage);
		}
		return usage;
	}
	
	public long getTotalUsageForLast24Hours(){
		long usage = 0;
		for(int index = 0 ; index < hourlyUsageCounters.length ; index++){
			usage += hourlyUsageCounters[index].get();
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Usage for last 24 hours: " + usage);
		}
		return usage;
	}

	
	private class UsageStatisticsUpdater extends BaseIntervalBasedTask {
		
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
				LogManager.getLogger().debug(MODULE, "Executing usage statistics updater");
			}
			Calendar currentTime = getCurrentDate();
			int newHour = currentTime.get(Calendar.HOUR_OF_DAY);
			int oldHour = currentHour;
			currentHour = newHour;
			if(oldHour != newHour) {
				if(oldHour == 23 && newHour == 0) {
					lastDayUsge = getTotalUsageForLast24Hours();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Last day usage updated: " + lastDayUsge);
					}
				}
				resetHourlyUsage(currentHour);
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Usage statistics updater execution completed");
			}
		}
	}
	// revert
	public static void main(String[] args) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		Calendar nextHour = Calendar.getInstance();
		System.out.println(simpleDateFormat.format(new Date(nextHour.getTimeInMillis())));
		System.out.println(new Date().getHours());
		long currentTimeMillis = nextHour.getTimeInMillis();
		nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
		//nextHour.set(Calendar.HOUR_OF_DAY, 23);
		//nextHour.set(Calendar.MINUTE, 59);
		nextHour.set(Calendar.MINUTE, nextHour.get(Calendar.MINUTE) + 1);
		nextHour.set(Calendar.SECOND, 00);
		nextHour.set(Calendar.MILLISECOND, 00);
		System.out.println(simpleDateFormat.format(new Date(nextHour.getTimeInMillis())));
		System.out.println(new Date(nextHour.getTimeInMillis()).getHours());
		System.out.println((nextHour.getTimeInMillis() - currentTimeMillis)/1000);
		System.out.println((nextHour.getTimeInMillis() - currentTimeMillis)/(1000*60));
	}

	public long getTotalUsageReportedInYesterDay() {
		return lastDayUsge;
	}

	public long getUsageReportedInLastHour() {
		int lastHour = currentHour - 1;
		if(lastHour < 0){
			lastHour = 23;
		}
		return getHourlyUsage(lastHour);
	}
	
	public void serialize() {
		try {
			SerializationUtil.serialize(new UsageStatisticsData(hourlyUsageCounters, lastDayUsge, getCurrentDate().getTimeInMillis()), usageSerializationFilePath);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while serializing usage statistics. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	protected Calendar getCurrentDate() {
		return Calendar.getInstance();
	}
	
	public long getStatisicsResetTime() {
		return statisicsResetTime;
	}
	
	/**
	 * This class only used as object to serialise and deserialize usage statistics
	 * 
	 * @author Chetan.Sankhala
	 */
	public static class UsageStatisticsData implements Serializable {

		private static final long serialVersionUID = 101L;
		private AtomicLong[]  hourlyUsageCounters;
		private long  lastDayUsage;
		private long lastSyncTimeInMillis;
		
		public UsageStatisticsData() { }

		public UsageStatisticsData(AtomicLong[] hourlyUsageCounters, long lastDayUsge, long syncTimeInMillis) {
			this.hourlyUsageCounters = hourlyUsageCounters;
			this.lastDayUsage = lastDayUsge;
			this.lastSyncTimeInMillis = syncTimeInMillis;
		}
	}
	
	public void reset() {
		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Resetting usage service statistics");
		}
		this.lastDayUsge = 0;
		for (int i = 0; i < hourlyUsageCounters.length; i++) {
			hourlyUsageCounters[i].getAndSet(0);
		}
		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Resetting usage service statistics completed");
		}
		this.statisicsResetTime = System.currentTimeMillis(); 
	}
}