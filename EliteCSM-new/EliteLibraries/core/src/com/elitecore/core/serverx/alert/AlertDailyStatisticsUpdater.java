package com.elitecore.core.serverx.alert;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public class AlertDailyStatisticsUpdater extends BaseIntervalBasedTask {

	private static final String MODULE = "ALERT-STAT-UPDATER";
	private static final short LAST_SECOND_OF_DAY = 59;
	private static final int LAST_MINUTE_OF_DAY = 59;
	private static final int LAST_HOUR_OF_DAY = 23;
	private Map<String,IAlertData> alertDetailsMap;
	
	public AlertDailyStatisticsUpdater(Map<String,IAlertData> alertDetailsMap) {
		
		this.alertDetailsMap = alertDetailsMap;
	}
	@Override
	public long getInitialDelay() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, LAST_HOUR_OF_DAY);
		calendar.set(Calendar.MINUTE, LAST_MINUTE_OF_DAY);
		calendar.set(Calendar.SECOND, LAST_SECOND_OF_DAY);
		
		return TimeUnit.MILLISECONDS.toSeconds((calendar.getTimeInMillis() - System.currentTimeMillis()));
	}
	
	@Override
	public long getInterval() {
		return TimeUnit.DAYS.toSeconds(1);
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}
	
	@Override
	public void execute(AsyncTaskContext context) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Executing daily alert statistics updater");
		}
		
		if (alertDetailsMap != null) {
    		for(IAlertData alertData : alertDetailsMap.values()) {
    			
    			if (alertData != null) {
    				alertData.getStatistics().resetDailyCounter();
    				Calendar calendar = Calendar.getInstance();
    				
    				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
    					alertData.getStatistics().resetWeeklyCounter();
    				}
    				
    				if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
    					alertData.getStatistics().resetMonthlyCounter();
    				}
    			}
    		}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Daily alert statistics updater execution completed");
		}
	}
	
}