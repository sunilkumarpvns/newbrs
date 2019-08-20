package com.elitecore.core.util.logger;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;

import java.util.UUID;

import static com.elitecore.core.util.logger.EliteRollingFileLogger.SIZE_BASED_ROLLING_TYPE;
import static com.elitecore.core.util.logger.EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY;
import static com.elitecore.core.util.logger.EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE;

public class AlertRollingFileLogger implements ILogger {

	private final int rollingUnit;
	private RollingFileLogger logger;
	private final int rollingType;


	public AlertRollingFileLogger(String serverInstanceName, String targetFile){
    	this(serverInstanceName,targetFile, TIME_BASED_ROLLING_TYPE, TIME_BASED_ROLLING_EVERY_DAY, 1, true);
    }

    public AlertRollingFileLogger(String serverInstanceName, String targetFile, int rollingType, int rollingUnit, int maxRolledUnits, boolean compressRolledUnit){

		this.rollingType = rollingType;
		this.rollingUnit = rollingUnit;
		int sizeBaseRollingLimit = 0;
		RollingFileLogger.TimeBaseRollingType timeBaseRolling = null;
		if(rollingType == SIZE_BASED_ROLLING_TYPE) {
			sizeBaseRollingLimit = rollingUnit;
		} else {
			timeBaseRolling = RollingFileLogger.TimeBaseRollingType.fromId(rollingUnit);
		}

    	logger = new RollingFileLogger.Builder(UUID.randomUUID().toString(), targetFile)
				.sizeBaseRolling(sizeBaseRollingLimit)
				.timeBaseRolling(timeBaseRolling)
				.maxRolledUnits(maxRolledUnits)
				.withPatternLayout("%d{DATE} [ %-5p ] %m%n")
				.compressRolledUnits(compressRolledUnit)
				.build();

    }


	
    public void setLogLevel(String level){
       logger.setLogLevel(level);
    }

    
    @Override
	public final boolean isLogLevel(LogLevel logLevel) {
		return logger.isLogLevel(logLevel);
	}
	
    @Override
    public int getCurrentLogLevel(){
    	return logger.getCurrentLogLevel();
    }
    
    public final boolean isCompressRolledUnit() {
		return logger.isCompressRolledUnit();
	}
		
	public final int getRollingType() {
		return rollingType;
	}

	
	public final int getRollingUnit() {
		return rollingUnit;
	}
	
	@Override
	public void error(String module, String strMessage) {
        logger.error(module, strMessage);
    }

	@Override
	public void debug(String module, String strMessage) {
        logger.debug(module, strMessage);
        
    }

	@Override
	public void info(String module, String strMessage) {
        logger.info(module, strMessage);
    }

	@Override
	public void warn(String module, String strMessage) {
        logger.warn(module, strMessage);
    }
    
	@Override
	public void trace(String module, String strMessage) {
        logger.trace(module, strMessage);
    }

	@Override
	public void trace(Throwable exception) {
		logger.trace(exception);
	}

	public int getMaxRolledUnits() {
		return logger.getMaxRolledUnits();
	}

	@Override
	public void trace(String module, Throwable exception) {
		logger.trace(module, exception);
	}

	public void addThreadName(String threadName){
    	
}
    
    public void removeThreadName(String threadName){
    	
    }

	@Override
	public boolean isErrorLogLevel() {
		return logger.isErrorLogLevel();
	}

	@Override
	public boolean isWarnLogLevel() {
		return logger.isWarnLogLevel();
	}

	@Override
	public boolean isInfoLogLevel() {
		return logger.isInfoLogLevel();
	}

	@Override
	public boolean isDebugLogLevel() {
		return logger.isDebugLogLevel();
	}
}
