package com.elitecore.netvertexsm.ws.logger;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;

public class Logger {
	 private static ILogger logger = new WebServiceLogger();

	    public static void logError(String strModule, String strMessage) {
	    	try{
	        logger.error(strModule, strMessage);
	    	}catch(Exception e){}
	    }
	    public static void logDebug(String strModule, String strMessage){
	    	try{
	        logger.debug(strModule, strMessage);
	    	}catch(Exception e){}
	    }
	    public static void logInfo(String strModule, String strMessage){
	    	try{
	        logger.info(strModule, strMessage);
	    	}catch(Exception e){}
	    }
	    public static void logWarn(String strModule, String strMessage){
	    	try{
	        logger.warn(strModule, strMessage);
	    	}catch(Exception e){}
	    }

	    public static void logTrace(String strModule,String strMessage){
	    	try{
	        logger.trace(strModule, strMessage);
	    	}catch(Exception e){}
	    }

	    public static void logError(String strModule, Object strMessage) {
	    	try{
	        logger.error(strModule, strMessage.toString());
	    	}catch(Exception e){}
	    }
	    public static void logDebug(String strModule, Object strMessage){
	    	try{
	        logger.debug(strModule, strMessage.toString());
	    	}catch(Exception e){}
	    }
	    public static void logInfo(String strModule, Object strMessage){
	    	try{
	        logger.info(strModule, strMessage.toString());
	    	}catch(Exception e){}
	    }
	    public static void logWarn(String strModule, Object strMessage){
	    	try{
	        logger.warn(strModule, strMessage.toString());
	    	}catch(Exception e){}
	    }

	    public static void logTrace(String strModule,Object strMessage){
	    	try{
	        logger.trace(strModule, strMessage.toString());
	    	}catch(Exception e){}
	    }

	    public static void logTrace(String strModule,Throwable exception){
	    	try{
	        logger.trace(strModule, EliteExceptionUtils.getRootCauseStackTraceAsString(exception));
	    	}catch(Exception e){}
	    }

	    public static void setLogger(ILogger newlogger) {
	        logger = newlogger;
	    }

	    public static ILogger getLogger(){
	        return logger;
	    }
}
