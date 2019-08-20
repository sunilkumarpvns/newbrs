package com.elitecore.elitesm.ws.logger;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.ConsoleAppender;
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
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss,SSS");
//	private static final String WSLOG4J_PROPERTIES_FILE_LOCATION = "config/webservicelog4j.properties";
	private static final String logFileName = "WebServiceLog";
	public WebServiceLogger(){
		logger = Logger.getLogger("webservice");
		System.out.println("Logget is going to init()");

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
			ConsoleAppender consolAppender= null;
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
				consolAppender = new ConsoleAppender(layout);
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
			logger.addAppender(consolAppender);
			//logger.addAppender(appender);
			logger.addAppender(rollingFileAppender);						
			logger.setLevel(Level.toLevel(System.getProperty("ws.loglevel"), Level.ALL));
			System.out.println("Setting log level trace.");
//		}
	
	}

	public void trace( Throwable exception ) {                    
	    	trace("",exception);
		}

	private String getClientAddress(){
		String clientAddress = (String)MDC.get("clientAddress");
		if(clientAddress!=null){
			clientAddress = "["+clientAddress+"] ";
		}else{
			clientAddress="[0.0.0.0] ";
		}
		return clientAddress;
	}

	@Override
	public void error(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ ERROR ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void debug(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ DEBUG ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void info(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ INFO ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void warn(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ WARN ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void trace(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void trace(String module, Throwable exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " +getClientAddress()+"- " +getLoginUserName() + " ["+ module +"]: " + stringWriter.toString());
	}

	protected String dateToString(Date date){
		return sdf.format(date);
	}

	@Override
	public int getCurrentLogLevel() {
		return LogLevel.ALL.level;
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return true;
	}
	
	private static String getLoginUserName(){
		String loginUser = "";
		String loginUserName=(String)MDC.get("loginUser");
		
		if(loginUserName !=null){
			loginUser=loginUserName;
		}else{
			loginUser="unknown";
		}
		return  loginUser;
	}
	@Override
	public void addThreadName(String threadName) {
		
	}
	@Override
	public void removeThreadName(String threadName) {
		
	}

	@Override
	public boolean isErrorLogLevel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isWarnLogLevel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isInfoLogLevel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isDebugLogLevel() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
