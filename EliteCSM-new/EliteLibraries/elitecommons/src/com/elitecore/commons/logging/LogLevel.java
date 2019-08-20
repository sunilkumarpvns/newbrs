package com.elitecore.commons.logging;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the levels on which the log messages can be published. 
 * The levels are defined in decreasing priority.
 * <table border="1">
 * <tr><td>LEVEL</td><td>DESCRIPTION</td></tr>
 * <tr><td>NONE(0)</td><td>This log level disables all logs</td></tr>
 * <tr><td>ERROR(1)</td><td>This log level should be used when some error condition occurs</td></tr>
 * <tr><td>WARN(2)</td><td>This log level should be used when some edge condition fails but system can continue to work</td></tr>
 * <tr><td>INFO(3)</td><td>This log level should be used for informative purposes only</td></tr>
 * <tr><td>DEBUG(4)</td><td>This log level should be used for in depth traces of system working. For developer level logging</td></tr>
 * <tr><td>TRACE(5)</td><td>This log level should be used when trace line should be shown in log</td></tr>
 * <tr><td>ALL(6)</td><td>This log level enables all logs</td></tr>
 * </table>
 * 
 * <br/><br/>
 * How levels work? - Suppose if the logger is working on log level {@link #WARN}, then
 * the log messages of all log levels above WARN are also displayed i.e. {@link #ERROR}
 * logs are also displayed.  
 * 
 * @author narendra.pathai
 *
 */
public enum LogLevel {
	/**
	 * This log level disables all the logs
	 */
	NONE(0),
	/**
	 * This log level should be used when some error condition occurs
	 */
	ERROR(1),
	/**
	 * This log level should be used when some edge condition fails but system can continue to work
	 */
	WARN(2),
	/**
	 * This log level should be used for informative purposes only
	 */
	INFO(3),
	/**
	 * This log level should be used for in depth traces of system working. For developer level logging
	 */
	DEBUG(4),
	/**
	 * This log level should be used when trace line should be shown in log
	 */
	TRACE(5),
	/**
	 * This log level enables all logs
	 */
	ALL(6);

	public final int level;
	private static Map<Integer, LogLevel> logLevels = new HashMap<Integer, LogLevel>();
	static {
		for (LogLevel logLevel:values()) {
			logLevels.put(logLevel.level, logLevel);
		}
	}

	LogLevel (int level) {
		this.level = level;
	}
	
	public static LogLevel fromLogLevel(int level) {
		return logLevels.get(level);
	}
}