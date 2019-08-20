package com.elitecore.commons.logging;

import com.elitecore.commons.annotations.VisibleForTesting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

/**
 * 
 * This class is the entry to the logging API. It maintains the mapping from the 
 * Log key to Logger instances. 
 * <br/>
 * <br/>
 * {@code LogManager} uses {@code ThreadLocal} references for all threads. The decision as to which logger
 * will be provided to the thread requesting the logger is dependent on the 
 * {@link LogKeyResolver} provided using {@link LogManager#setLogKeyResolver(LogKeyResolver)}.
 * The logger mapped with the key resolved using LogKeyResolver will be returned when {@link LogManager#getLogger()}}
 * is called, in case if no logger is mapped to the resolved key, default logger key is used and <b>default logger</b> instance
 * is returned.
 * 
 * <br/>
 * <br/>
 * Every application can register its own default logger instance using {@link LogManager#setDefaultLogger(ILogger)}, and
 * then subsequent calls to {@link LogManager#getLogger()} will return registered default logger is applicable.
 * <br/>
 * {@code LogManager} uses {@link ConsoleLogger} as the default logger.
 * <br/>
 * <br/>
 * Different modules can register different instances
 * of loggers with their key to {@code LogManager} using method {@link LogManager#setLogger(String, ILogger)}.
 * And when threads of those modules request logger and if the Log key of those threads match to that of the 
 * module log key then the registered logger instance is returned.
 * <br/>
 * <br/>
 * <i><b>Module wise logging</i></b> is helpful when application maintains Service wise loggers, and different services can register
 * different logger instances, and subsequently all the threads of that service will get the service wise logger that 
 * applies.
 *  
 * @author narendra.pathai
 *
 */
public class LogManager {
	public static final String DEFAULT_LOGGER_KEY = "DEFAULT_LOGGER";
	
	private static Map<String, ILogger> keyToLogger = new ConcurrentHashMap<String, ILogger>();
	
	static ThreadLocal<ILogger> loggerLocal = new LoggerThreadLocal();
	
	private static volatile LogKeyResolver logKeyResolver = new ThreadNameBasedReslover(10);
	
	static {
		keyToLogger.put(DEFAULT_LOGGER_KEY, new ConsoleLogger());
	}

	protected LogManager() {
		//This class is not meant to be instantiated
	}
	
	public static void setDefaultLogger(ILogger defaultLogger) {
		checkNotNull(defaultLogger, "defaultLogger is null");
		keyToLogger.put(DEFAULT_LOGGER_KEY, defaultLogger);
		resetLogger();
	}
	
	public static void setLogKeyResolver(LogKeyResolver logKeyResolver) {
		checkNotNull(logKeyResolver, "logKeyResolver is null");
		LogManager.logKeyResolver = logKeyResolver;
	}
	
	public static ILogger getLogger() {
		return loggerLocal.get();
	}
	
	public static void setLogger(String key, ILogger logger) {
		checkNotNull(key, "key for logger is null");
		checkNotNull(logger, "logger is null");
		keyToLogger.put(key, logger);
	}
	
	public interface LogKeyResolver {
		public String resloveKey();
	}
	
	private static class LoggerThreadLocal extends ThreadLocal<ILogger> {
		@Override
		protected ILogger initialValue() {
			ILogger logger = LogManager.get(logKeyResolver.resloveKey());
			if (logger == null) {
				logger = LogManager.get(DEFAULT_LOGGER_KEY);
			}
			return logger;
		}
	}
	
	@VisibleForTesting 
	static ILogger get(String key) {
		return keyToLogger.get(key);
	}
	
	private static void resetLogger() {
		loggerLocal.remove();
	}

	/**
	 * Use this method to delude sonar scanner about the exceptions which need to be ignored <i>intentionally</i>.
	 * @author Jay Trivedi
	 */
	public static void ignoreTrace(Throwable e) { //NOSONAR
		// Do nothing
	}
}
