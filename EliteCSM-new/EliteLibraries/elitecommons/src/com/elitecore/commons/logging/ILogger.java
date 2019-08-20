package com.elitecore.commons.logging;

/**
 * 
 * The logger interface that should be implemented by all implementations using
 * {@link LogManager} to manage logging. 
 * 
 * <p>Logging can be done at various {@link LogLevel}s.
 * 
 * <p>All implementer classes can maintain current log level which is queried using
 * {@link #getCurrentLogLevel()}.
 * 
 * <p>While logging it is advised to first check the log level using {@link #isLogLevel(LogLevel)}
 * or log level query methods such as {@link #isErrorLogLevel()} and then call the appropriate method for logging. This is useful in saving creation of multiple {@code String}
 * instances.
 * 
 * <p>Usage:<br>
 * <code><pre>
 * if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
 * 	LogManager.getLogger().info(MODULE,"this" + "is" + "some" + "message");
 * }
 * </pre></code>
 * <br/>
 * or a better way of doing the same
 * <code><pre>
 * if(LogManager.getLogger().isInfoLogLevel()){
 * 	LogManager.getLogger().info(MODULE,"this" + "is" + "some" + "message");
 * }
 * </pre></code>
 * 
 * <p>Using the above convention creation of String instances can be saved which can drastically improve performance
 * of logging. In the above example only when current log level is INFO the String instances will be created.  
 *  
 * @author narendra.pathai
 *
 */
public interface ILogger {

	/**
	 * Logs the message if the current log level is {@link LogLevel#ERROR}
	 * @param module name of the logging module
	 * @param strMessage message that is to be logged
	 */
	public void error(String module, String strMessage);

	/**
	 * Logs the message if the current log level is {@link LogLevel#DEBUG} or above
	 * @param module name of the logging module
	 * @param strMessage message that is to be logged
	 */
	public void debug(String module, String strMessage);

	/**
	 * Logs the message if the current log level is {@link LogLevel#INFO} or above
	 * @param module name of the logging module
	 * @param strMessage message that is to be logged
	 */
	public void info(String module, String strMessage);

	/**
	 * Logs the message if the current log level is {@link LogLevel#WARN} or above
	 * @param module name of the logging module
	 * @param strMessage message that is to be logged
	 */
	public void warn(String module, String strMessage);

	/**
	 * Logs the message if the current log level is {@link LogLevel#TRACE} or above
	 * @param module name of the logging module
	 * @param strMessage message that is to be logged
	 */
	public void trace(String module, String strMessage);

	/**
	 * Logs the given exception trace
	 * @param exception an exception whose trace is to be logged
	 */
	public void trace(Throwable exception);

	/**
	 * Logs the given exception trace with module name
	 * @param module name of the logging module
	 * @param exception an exception whose trace is to be logged
	 */
	public void trace(String module, Throwable exception);

	/**
	 * Returns the current log level in integer
	 * 
	 * @return integer equivalent of current {@link LogLevel}
	 */
	public int getCurrentLogLevel();

	/**
	 * Compares the given log level with the current log level and returns true
	 * if they are same.
	 * 
	 * <p>
	 * A better way of checking log level is to use log level query methods defined
	 * for individual log levels such as {@link #isErrorLogLevel()}.
	 * 
	 * <p>NOTE: Implementations can override this behavior and return result as applicable.
	 *   
	 * @param level the level with which the current log level will be compared 
	 * @return true if {@code level} and current log level are same, false otherwise
	 * 
	 */
	public boolean isLogLevel(LogLevel level);
	
	/**
	 * Returns <tt>true</tt> if current log level is {@link LogLevel#ERROR} 
	 * or above.
	 * <br/>
	 * A better way of asking 
	 * <code>LogManager.getLogger().isLogLevel(LogLevel.ERROR)</code>
	 * @return <tt>true</tt> if current log level is {@link LogLevel#ERROR}
	 * or above
	 */
	public boolean isErrorLogLevel();
	/**
	 * Returns <tt>true</tt> if current log level is {@link LogLevel#WARN} 
	 * or above.
	 * <br/>
	 * A better way of asking 
	 * <code>LogManager.getLogger().isLogLevel(LogLevel.WARN)</code>
	 * @return <tt>true</tt> if current log level is {@link LogLevel#WARN}
	 * or above
	 */
	public boolean isWarnLogLevel();
	/**
	 * Returns <tt>true</tt> if current log level is {@link LogLevel#INFO} 
	 * or above.
	 * <br/>
	 * A better way of asking 
	 * <code>LogManager.getLogger().isLogLevel(LogLevel.INFO)</code>
	 * 
	 * @return <tt>true</tt> if current log level is {@link LogLevel#INFO}
	 * or above
	 */
	public boolean isInfoLogLevel();
	/**
	 * 
	 * Returns <tt>true</tt> if current log level is {@link LogLevel#DEBUG} 
	 * or above.
	 * <br/>
	 * A better way of asking 
	 * <code>LogManager.getLogger().isLogLevel(LogLevel.DEBUG)</code>
	 * 
	 * @return <tt>true</tt> if current log level is {@link LogLevel#DEBUG}
	 * or above
	 */
	public boolean isDebugLogLevel();

	public void addThreadName(String threadName);

	public void removeThreadName(String threadName);

}
