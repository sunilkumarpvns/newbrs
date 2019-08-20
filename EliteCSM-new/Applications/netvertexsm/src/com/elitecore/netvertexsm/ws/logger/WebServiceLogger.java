package com.elitecore.netvertexsm.ws.logger;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;



public class WebServiceLogger implements ILogger{
	public static Logger logger = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
//	private static final String WSLOG4J_PROPERTIES_FILE_LOCATION = "config/webservicelog4j.properties";
	private static final String logFileName = "WebServiceLog";
	public WebServiceLogger(){
		logger = Logger.getLogger("webservice");
		System.out.println("Logger is going to init()");

//		
//		boolean sucess=false;
//		try{
//			
//			Properties properties = new Properties();
//			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(WSLOG4J_PROPERTIES_FILE_LOCATION));
//			PropertyConfigurator.configure(properties);
//			sucess = true;
//			
//		}catch(Exception e){
//			System.out.println("Failed to retrive configuration from property file[ "+WSLOG4J_PROPERTIES_FILE_LOCATION+" ].");
//			
//		}
//		if(!sucess){
//			System.out.println("Loading default configuration.");
			String fileName = null;
			Layout layout = new PatternLayout("%m%n");
			//ConsoleAppender consolAppender= null;
			//FileAppender appender = null;
			RollingFileAppender rollingFileAppender=null; 
			try {
				
				
				
				File logDir = new File("../logs");

				if(logDir.exists())
					fileName= "../logs/"+logFileName;
				else
					fileName ="./"+logFileName;
	
				
				File logFile = new File(fileName);
				System.out.println("Logfile :" + logFile.getCanonicalPath());
				//consolAppender = new ConsoleAppender(layout);
				//appender = new FileAppender(layout,fileName,true);
				rollingFileAppender = new RollingFileAppender();
				
				rollingFileAppender.setFile(fileName+".log");
				rollingFileAppender.setLayout(layout);
				rollingFileAppender.setAppend(true);
				
				TimeBasedRollingPolicy timeBasedRollingPolicy = new TimeBasedRollingPolicy();
				timeBasedRollingPolicy.setFileNamePattern(fileName+"_%d"+".log");
				timeBasedRollingPolicy.activateOptions();
				
				rollingFileAppender.setRollingPolicy(timeBasedRollingPolicy);
				rollingFileAppender.setTriggeringPolicy(timeBasedRollingPolicy);
				rollingFileAppender.activateOptions();
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Not able to get File appender");
			}
			//logger.addAppender(consolAppender);
			//logger.addAppender(appender);
			logger.addAppender(rollingFileAppender);
			Level level = Level.toLevel(System.getenv("WSLOGLEVEL"), Level.ALL);
			System.out.println("Setting Webservice Logger Level to " + level.toString());
			logger.setLevel(level);
			
//		}
	
	}

	protected String dateToString(Date date) {
		return sdf.format(date);
	}
	private String getClientAddress(){
		String clientAddress = (String)MDC.get("clientAddress");
		if(clientAddress!=null){
			clientAddress = "["+clientAddress+"] ";
		}else{
			clientAddress="";
		}
		return clientAddress;
	}
	
	@Override
	public void error(String module, String strMessage) {
		logger.error("[" + dateToString(new Date()) + "] " + "[ ERROR ] "+ getClientAddress() + "[ "+ module +" ] : " + strMessage);
	}

	@Override
	public void debug(String module, String strMessage) {
		logger.debug("[" + dateToString(new Date()) + "] " + "[ DEBUG ] "+ getClientAddress() + "[ "+ module +" ] : " + strMessage);
	}

	@Override
	public void info(String module, String strMessage) {
		logger.info("[" + dateToString(new Date()) + "] " + "[ INFO ] "+ getClientAddress() + "[ "+ module +" ] : " + strMessage);
	}

	@Override
	public void warn(String module, String strMessage) {
		logger.warn("[" + dateToString(new Date()) + "] " + "[ WARN ] "+ getClientAddress() + "[ "+ module +" ] : " + strMessage);
	}

	@Override
	public void trace(String module, String strMessage) {
		logger.trace("[" + dateToString(new Date()) + "] " + "[ TRACE ] "+ getClientAddress() + "[ "+ module +" ] : " + strMessage);
	}

	@Override
	public void trace(String module, Throwable exception) {
		logger.trace("[" + dateToString(new Date()) + "] " + "[ TRACE ] "+ getClientAddress() + "[ "+ module +" ] : " + exception);
	}

	@Override
	public void trace(Throwable exception) {
		logger.trace("[" + dateToString(new Date()) + "] " + "[ TRACE ] "+ getClientAddress() + exception);
	}        

	@Override
	public int getCurrentLogLevel() {
		return logger.getLevel().toInt();
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return getCurrentLogLevel() == level.level;
	}

	@Override
	public void addThreadName(String threadName) {
		
	}

	@Override
	public void removeThreadName(String threadName) {
		
	}
	
	@Override
	public boolean isDebugLogLevel() {
		return getCurrentLogLevel() >= LogLevel.DEBUG.level;
	}
	
	@Override
	public boolean isErrorLogLevel() {
		return getCurrentLogLevel() >= LogLevel.ERROR.level; 
	}
	
	@Override
	public boolean isInfoLogLevel() {
		return getCurrentLogLevel() >= LogLevel.INFO.level;
	}
	
	@Override
	public boolean isWarnLogLevel() {
		return getCurrentLogLevel() >= LogLevel.WARN.level;
	}
}
