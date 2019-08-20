package com.elitecore.commons.logging;

/**
 * A replacement for real logger, will not log anything.
 * 
 * @author narendra.pathai
 *
 */
public class NullLogger implements ILogger{

	@Override
	public void error(String module, String strMessage) {
		
	}

	@Override
	public void debug(String module, String strMessage) {
		
	}

	@Override
	public void info(String module, String strMessage) {
		
	}

	@Override
	public void warn(String module, String strMessage) {
		
	}

	@Override
	public void trace(String module, String strMessage) {
		
	}

	@Override
	public void trace(Throwable exception) {
		
	}

	@Override
	public void trace(String module, Throwable exception) {
		
	}

	@Override
	public int getCurrentLogLevel() {
		return LogLevel.NONE.level;
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return false;
	}

	@Override
	public void addThreadName(String threadName) {
		
	}

	@Override
	public void removeThreadName(String threadName) {
		
	}

	@Override
	public boolean isErrorLogLevel() {
		return false;
	}

	@Override
	public boolean isWarnLogLevel() {
		return false;
	}

	@Override
	public boolean isInfoLogLevel() {
		return false;
	}

	@Override
	public boolean isDebugLogLevel() {
		return false;
	}
}
