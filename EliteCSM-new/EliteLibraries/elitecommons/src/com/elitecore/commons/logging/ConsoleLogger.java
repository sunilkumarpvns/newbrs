package com.elitecore.commons.logging;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logs on System output console (System.out) in the following format 
 * <p>
 * [dd-MM-yyyy hh:mm:ss] [LOGLEVEL] CURRENT-THREAD-NAME [MODULE-NAME]: MESSAGE
 * <br/><br/>
 * Console logger always works on {@link LogLevel.ALL} log level
 * @author narendra.pathai
 *
 */
public class ConsoleLogger implements ILogger {

	private ThreadLocal<SimpleDateFormat> sdfLocal = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss,SSS");
		};
	};

	@Override
	public void error(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ ERROR ] " +Thread.currentThread().getName() + " ["+ module +"]: " + strMessage);
	}
	
	@Override
	public void debug(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ DEBUG ] " +Thread.currentThread().getName() + " ["+ module +"]: " + strMessage);
	}
	
	@Override
	public void info(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ INFO ] " +Thread.currentThread().getName() + " ["+ module +"]: " + strMessage);
	}
	
	@Override
	public void warn(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ WARN ] " +Thread.currentThread().getName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void trace(String module, String strMessage) {
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " +Thread.currentThread().getName() + " ["+ module +"]: " + strMessage);
	}

	@Override
	public void trace(Throwable exception) {
		trace("",exception);
	}

	@Override
	public void trace(String module, Throwable exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] "+ Thread.currentThread().getName() +" [" + module +"] "+ stringWriter.toString());
	}

	protected String dateToString(Date date){
		return sdfLocal.get().format(date);
	}

	@Override
	public int getCurrentLogLevel() {
		return LogLevel.ALL.level;
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return true; // all log level - 
	}
	
	@Override
	public void addThreadName(String threadName) {

	}
	
	@Override
	public void removeThreadName(String threadName) {

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
}
