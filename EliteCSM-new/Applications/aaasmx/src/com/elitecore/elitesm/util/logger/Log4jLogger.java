/**
 * 
 */

package com.elitecore.elitesm.util.logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;

public class Log4jLogger implements ILogger {
	public static Logger logger = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss,SSS");
	private static final String MODULE  = "LOGGER";
	public Log4jLogger(){
		logger = Logger.getLogger("EliteLog4j");
		System.out.println("Logget is going to init()");
		String fileName = null;
		Layout layout = new PatternLayout("%m%n");
		ConsoleAppender consolAppender= null;
		FileAppender appender = null;

		try {
			File logDir = new File("../logs");

			if(logDir.exists())
				fileName= "../logs/ServerManager.log";
			else
				fileName ="./ServerManager.log";
			System.out.println("Logfile :" + fileName);
			consolAppender = new ConsoleAppender(layout);
			appender = new FileAppender(layout,fileName,true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Not able to get File appender");
		}

		logger.addAppender(consolAppender);
		logger.addAppender(appender);
		logger.setLevel(Level.TRACE);

		logger.info("[" + dateToString(new Date()) + "] " +  "[ WARN  ] "+ "["+ MODULE +"]"  +"  :  Server Started on "+ dateToString(new Date()));

	}
	public void setLogLevel(Level level){
		logger.setLevel(level);
	}
	public void trace(String strMessage) {

		logger.trace("[" + dateToString(new Date()) + "] " + "[ TRACE ] "+ getClientAddress()+strMessage);

	}

	public void debug(String strMessage) {
		logger.debug("[" + dateToString(new Date()) + "] " + "[ DEBUG ] "+ getClientAddress()+strMessage);
	}

	public void info(String strMessage) {
		logger.info("[" + dateToString(new Date()) + "] " + "[ INFO  ] "+getClientAddress()+ strMessage);
	}


	public void warn(String strMessage) {
		logger.warn("[" + dateToString(new Date()) + "] " + "[ WARN ] "+getClientAddress()+ strMessage);
	}

	public void error(String strMessage) {
		logger.error("[" + dateToString(new Date()) + "] " + "[ ERROR ] "+ getClientAddress()+strMessage);
	}

	public void fatal(String strMessage) {
		logger.fatal("[" + dateToString(new Date()) + "] " + "[ FATAL ] "+getClientAddress()+ strMessage);
	}

	public void trace( Throwable exception ) {                    


	}


	protected String dateToString(Date date) {
		return sdf.format(date);
	}
	private static String getClientAddress(){

		String clientAddress = "";


		String remoteAddress = (String)MDC.get("remoteaddress");


		if(remoteAddress!=null){
			clientAddress  = "["+remoteAddress+"] ";
		}
		return clientAddress;
	}        
	@Override
	public void error(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ ERROR ] " +getClientAddress()+ " ["+ module +"]: " + strMessage);
	}

	@Override
	public void debug(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ DEBUG ] " +getClientAddress()+ " ["+ module +"]: " + strMessage);
	}

	@Override
	public void info(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ INFO ] " +getClientAddress()+ " ["+ module +"]: " + strMessage);
	}

	@Override
	public void warn(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ WARN ] " +getClientAddress()+ " ["+ module +"]: " + strMessage);
	}

	@Override
	public void trace(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " +getClientAddress()+ " ["+ module +"]: " + strMessage);
	}
	
	@Override
	public void trace(String module, Throwable exception) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " +getClientAddress()+ " ["+ module +"]: " + exception);
		
	}
	@Override
	public int getCurrentLogLevel() {
		return logger.getLevel().toInt();
	}
	@Override
	public boolean isLogLevel(LogLevel level) {
		return getCurrentLogLevel()==level.level;
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
