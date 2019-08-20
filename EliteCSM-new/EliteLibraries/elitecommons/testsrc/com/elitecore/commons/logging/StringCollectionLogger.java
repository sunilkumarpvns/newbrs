package com.elitecore.commons.logging;

/**
 * Collects logs in a string builder so that assertions can be made on log contents.
 *  
 * @author narendra.pathai
 *
 */
public class StringCollectionLogger implements ILogger {
	
	private StringBuilder log = new StringBuilder();

	public String contents() {
		return log.toString();
	}
	
	@Override
	public void error(String module, String strMessage) {
		log.append(module + strMessage + "\n");
	}

	@Override
	public void debug(String module, String strMessage) {
		log.append(module + strMessage + "\n");
	}

	@Override
	public void info(String module, String strMessage) {
		log.append(module + strMessage + "\n");
	}

	@Override
	public void warn(String module, String strMessage) {
		log.append(module + strMessage + "\n");
	}

	@Override
	public void trace(String module, String strMessage) {
		log.append(module + strMessage + "\n");
	}

	@Override
	public void trace(Throwable exception) {
		
	}

	@Override
	public void trace(String module, Throwable exception) {
		log.append(module + exception.getMessage() + "\n");
	}

	@Override
	public int getCurrentLogLevel() {
		return LogLevel.ALL.level;
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return true;
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

	@Override
	public void addThreadName(String threadName) {
		
	}

	@Override
	public void removeThreadName(String threadName) {
		
	}
}
