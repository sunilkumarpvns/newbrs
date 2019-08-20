package com.elitecore.aaa.diameter.service.application;

import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.license.base.commons.LicenseNameConstants;

/**
 * Maintains Concurrent Diameter Sessions count per minute and 
 * Validates Licensed Concurrent Diameter Sessions of last hour
 * 
 * @author monica.lulla
 */
public class ConcurrentSessionLicenseTask extends BaseIntervalBasedTask{

	private int[] perMinuteSessionCountBuffer = new int[60];
	private int currentIndex = 0;
	private int sumOfConncurrentSessions = 0;
	
	private ServerContext serverContext;
	
	private static final String MODULE = "CONC-SESS-LICENSE-TASK";
	private SessionFactoryManager sessionFactoryManager;

	public ConcurrentSessionLicenseTask(SessionFactoryManager sessionFactoryManager,
			ServerContext serverContext) {
		this.sessionFactoryManager = sessionFactoryManager;
		this.serverContext = serverContext;
	}

	@Override
	public long getInterval() {
		return 1;
	}

	@Override
	public long getInitialDelay() {
		return 1;
	}

	@Override
	public boolean isFixedDelay() {
		return true;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}

	@Override
	public void execute(AsyncTaskContext context) {

		int currentSessionCount = sessionFactoryManager.getSessionCount();
		
		sumOfConncurrentSessions = sumOfConncurrentSessions - perMinuteSessionCountBuffer[currentIndex] + currentSessionCount;
		perMinuteSessionCountBuffer[currentIndex] = currentSessionCount;
		currentIndex = (currentIndex == perMinuteSessionCountBuffer.length -1) ? 0 : currentIndex + 1;

		int average = sumOfConncurrentSessions/perMinuteSessionCountBuffer.length;
		/*
		 * Below will generate Alert, Concurrent Session exceed.
		 * License Alert Generation is done by isLicenceValid()
		 */
		if(!serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_CONCURRENT_SESSION, 
				String.valueOf(average))){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Concurrent Diameter Sessions of last hour exceeded the licensed limit, please consider upgrading the system");
			}
			reset();
		}
	}

	private void reset() {
		
		perMinuteSessionCountBuffer = new int[60];
		currentIndex = 0;
		sumOfConncurrentSessions = 0;
	}

}
