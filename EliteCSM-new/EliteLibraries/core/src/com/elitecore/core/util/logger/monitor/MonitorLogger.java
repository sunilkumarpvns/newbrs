package com.elitecore.core.util.logger.monitor;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.util.logger.RollingFileLogger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorLogger implements ILogger {

	public static final String MONITORED = "MONITORED";

	private final RollingFileLogger logger;
	private final Set<String> threadNames = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private boolean bActive;

    public MonitorLogger(String loggerName, String targetFile) {

		String layoutPattern = null;

		if(Boolean.valueOf(System.getProperty("log.output.colored"))) {
			layoutPattern = "%highlight{%d{DATE} [%level] %t %msg%n}";
		} else {
			layoutPattern = "%d{DATE} [%level] %t %msg%n";
		}

		logger = new RollingFileLogger.Builder(loggerName, targetFile)
				.timeBaseRolling(RollingFileLogger.TimeBaseRollingType.EVERY_DAY)
				.compressRolledUnits(true)
				.withPatternLayout(layoutPattern)
				.build();

		logger.setLogLevel(org.apache.logging.log4j.Level.ALL.name());
	}
    
    public void addThreadName(String threadName) {
    	bActive = true;
    	this.threadNames.add(threadName);
    }
    
    public void removeThreadName(String threadName) {
    	this.threadNames.remove(threadName);
    	if (threadNames.isEmpty()) {
    		bActive = false;
    	}
    }

	@Override
	public final boolean isLogLevel(LogLevel logLevel) {
		return true;
	}
	
    @Override
    public int getCurrentLogLevel() {
    	return logger.getCurrentLogLevel();
    }
	
	@Override
	public void error(String module, String strMessage) {
		if (threadNames.contains(Thread.currentThread().getName())) {
			logger.error(module, strMessage);
		}
    }

	@Override
	public void debug(String module, String strMessage) {
		if(threadNames.contains(Thread.currentThread().getName())) {
			logger.debug(module, strMessage);
		}
    }

	@Override
	public void info(String module, String strMessage) {
		if (threadNames.contains(Thread.currentThread().getName())) {
			logger.info(module, strMessage);
		}
    }

	@Override
	public void warn(String module, String strMessage) {
		if (threadNames.contains(Thread.currentThread().getName())) {
			logger.warn(module, strMessage);
		}
    }
    
	@Override
	public void trace(String module, String strMessage) {
		if (threadNames.contains(Thread.currentThread().getName())) {
			logger.warn(module, strMessage);
		}
    }

	@Override
	public void trace(Throwable exception) {
		trace(Thread.currentThread().getName(), exception);
	}

	@Override
	public void trace(String module, Throwable exception) {
		//not supported
	}

	public boolean isActive() {
		return bActive; 
	}

	@Override
	public boolean isErrorLogLevel() {
		return true;
	}

	@Override
	public boolean isWarnLogLevel() {
		return true;
	}

	@Override
	public boolean isInfoLogLevel() {
		return true;
	}

	@Override
	public boolean isDebugLogLevel() {
		return true;
	}

	public void close() {
    	logger.close();
	}
}
