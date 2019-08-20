package com.elitecore.nvsmx.system.listeners;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.NVSMXDefaults;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import com.elitecore.nvsmx.system.util.DataInitializer;
import com.google.code.jcaptcha4struts2.core.beans.JC4S2Config;
import com.octo.captcha.service.image.ImageCaptchaService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class NVSMXContextInitializer implements ServletContextListener {

	private static final String MODULE = "NVSMX-CNTXT-INIT";
	public static final String NAME = "Policy Designer";


	private String deploymentPath = null;
	private ConcurrentHashMap<String, Long> blockedIPreleaseTimeMap;

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE,"inside context destroyed method");
		}

		DataInitializer.getInstance().destroy();
	}

	/**
	 * This is the initialization point for nvsmx which <br/> 
	 * performs the following initialization tasks <br/>
	 * (i). Initializing Logger <br/>
	 * (ii).Initializing Hibernate Session Factory <br/>
	 * (iii).Populating System Information 
	 *
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		try{

			if(getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE,"******** inside contextInitialized method of NVSMXContextInitializer ********");
			}

			blockedIPreleaseTimeMap = new ConcurrentHashMap<>(10);
			servletContextEvent.getServletContext().setAttribute(Attributes.BLOCKED_IP_RELEASE_TIME_MAP,blockedIPreleaseTimeMap);


			deploymentPath = servletContextEvent.getServletContext().getRealPath(File.separator);
			String contextPath = servletContextEvent.getServletContext().getContextPath();
			DefaultNVSMXContext.getContext().setContextPath(contextPath);
			DefaultNVSMXContext.getContext().setServerHome(deploymentPath);
			if(getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "DeploymentPath   " + deploymentPath);
			}

			//setting import export operation
			servletContextEvent.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);

			//Initializing Logger
			initializingLogger();


			registerImageCaptchaService();

			EliteScheduler.getInstance().scheduleIntervalBasedTask(new ReleaseBlockedIp());

			DataInitializer.getInstance().init();

			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Server initialized successfully");
			}

		}catch(Exception e){
			getLogger().error(MODULE, "Error while initializing servlet Context. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}


	private void initializingLogger() {
            ConfigurationProvider.getInstance().init(deploymentPath);
            initializeLogger();
            if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Logger initialized successfully");
			}
	}


	/**
	 * Removed block ip from the map.
	 * @author Dhyani.Raval
	 *
	 */
	private class ReleaseBlockedIp implements IntervalBasedTask{

		@Override
		public long getInitialDelay() {
			return 1;
		}

		@Override
		public long getInterval() {
			return 1;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.DAYS;
		}

		@Override
		public void preExecute(AsyncTaskContext context) {
			//not-required
		}

		@Override
		public void execute(AsyncTaskContext context) {
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE,"Execute release blocked IP scheduler");
			}
			Iterator<Map.Entry<String, Long>> iterator = blockedIPreleaseTimeMap.entrySet().iterator();
			long currentTimeMillis = System.currentTimeMillis();
			while(iterator.hasNext()){
				Map.Entry<String,Long> entry = iterator.next();
				if(entry.getValue() < currentTimeMillis){
					iterator.remove();
					if(getLogger().isDebugLogLevel()){
						getLogger().debug(MODULE,"Remove Blocked IP: "+entry.getKey());
					}
				}
			}

		}

		@Override
		public void postExecute(AsyncTaskContext context) {
			//not-required
		}

	}

	/**
	 * Method to initialize logger for nvsmx
	 * it will create the log file at deployment path with default folder name "logs"
	 * & filename "nvsmx.log"
	 */
	private void initializeLogger(){

		getLogger().info(MODULE,"Initializing logger for NVSMX");

		String logFileName=ConfigurationProvider.getInstance().getLogFileName();
		String logLevel=ConfigurationProvider.getInstance().getLogLevel();
		String logFilLocation = getLogFileLocation();

		getLogger().info(MODULE,"***** " + MODULE + " ,Logs File Location for NVSMX : " + logFilLocation);

		boolean appendDiagnosticInformation = Boolean.parseBoolean(System.getProperty("AppendDiagnosticInformation"));
		EliteRollingFileLogger serverLevelLogger = new EliteRollingFileLogger.Builder(logFileName
				,logFilLocation+File.separator+"nvsmx")
				.appendDiagnosticInformation(appendDiagnosticInformation)
				.build();

		serverLevelLogger.setLogLevel(logLevel);

		LogManager.setDefaultLogger(serverLevelLogger);
	}

	/**
	 * Returns log file location based on criteria:
	 *      {@code Absolute} or {@code Relative}  path
	 *  <br/>
	 *   If it is Absolute
	 *  <br/>than it will return that path
	 *  <br/> else
	 *  <br/>for relative path it will create location relative to deployment path i.e. {@code deploymentPath+logFileLocation}
	 *  <br/>If any exception occur while accessing that location then
	 *       it returns default location i.e.
	 *  <br>{@code deploymentPath+"logs" }
	 *
	 * @return logFileLocation
	 */
	private String getLogFileLocation(){
		String defaultLogFilePath = deploymentPath+ NVSMXDefaults.LOGFILE_LOCATION.getVal();
		String logFileLocation = ConfigurationProvider.getInstance().getLogFileLocation();
		if (Strings.isNullOrBlank(logFileLocation)) {
			getLogger().warn(MODULE, " File location not configured.Using default location.");
			return defaultLogFilePath;
		}
		try {
			File file = new File(logFileLocation);
			if (file.isAbsolute() == false) {
				logFileLocation = deploymentPath + File.separator + logFileLocation;
				file = new File(logFileLocation);
			}
			if (file.exists() == true) {
				if (file.canWrite() == false) {
					getLogger().warn(MODULE, "No write access at location: " + logFileLocation + " .Using default location");
					return defaultLogFilePath;
				}
			} else {
				getLogger().info(MODULE, "Directory : " + logFileLocation + " does not exist.Creating the directory.");
				if (file.mkdirs() == false) {
					getLogger().warn(MODULE, "Problem in creating directory: " + logFileLocation); //NOSONAR -Reason. No need to create constant for  "Problem in creating directory: "
					return defaultLogFilePath;
				}
			}
		} catch (SecurityException ex) {
			getLogger().warn(MODULE, " No read permission at location: " + logFileLocation + ".Using default location");
			getLogger().trace(MODULE, ex);
			return defaultLogFilePath;
		}
		return logFileLocation;
	}
	private void registerImageCaptchaService() {
		try {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Registering Image captcha service");
			}

			Class<?> clazz = Class.forName("com.elitecore.nvsmx.commons.captcha.NVImageCaptchaService");
			JC4S2Config.getInstance().setImageCaptchaService((ImageCaptchaService) clazz.newInstance());
		}catch(Exception e){
			getLogger().error(MODULE, "Could not initialize Captcha Image Service. Will use Default Image Service");
			getLogger().trace(MODULE, e);
		}
	}

}