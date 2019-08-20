package com.elitecore.aaa.radius.service.acct;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public abstract class TimeBoundryRollingIntervalTask extends BaseIntervalBasedTask{
	
	private long rollingTaskInitialDelay;
	private long rollingIntervalInSecond;
	
	public TimeBoundryRollingIntervalTask(long rollingIntervalInMinute) {
		
		Calendar cal = Calendar.getInstance();
		long currentMinute = cal.get(Calendar.MINUTE);
		long currentSecond = cal.get(Calendar.SECOND);
		rollingIntervalInSecond = rollingIntervalInMinute * 60;

		if(rollingIntervalInMinute == 1){
			rollingTaskInitialDelay = rollingIntervalInSecond - currentSecond;
		}else{
			if(currentMinute % rollingIntervalInMinute == 0){
				rollingTaskInitialDelay = rollingIntervalInSecond - currentSecond;
			}else{
				long nextInterval = (currentMinute + rollingIntervalInMinute);
				long intervalToRound = nextInterval % rollingIntervalInMinute;
				if(intervalToRound == 0)
					rollingTaskInitialDelay = 0;
				rollingTaskInitialDelay = ((nextInterval - intervalToRound) - currentMinute) * 60 - currentSecond;
			}
		}
	}

	/**
	 * @param rollingIntervalInMinute
	 * @param initialDelay in second
	 */
	public TimeBoundryRollingIntervalTask(long rollingIntervalInMinute,long initialDelay){
		if(initialDelay<0){
			initialDelay = rollingIntervalInMinute*60;
		}
		this.rollingTaskInitialDelay = initialDelay;
		this.rollingIntervalInSecond = rollingIntervalInMinute*60;
	}
	
	@Override
	public long getInterval() {
		return rollingIntervalInSecond;
	}

	@Override
	public long getInitialDelay() {
		return rollingTaskInitialDelay;
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}
}
