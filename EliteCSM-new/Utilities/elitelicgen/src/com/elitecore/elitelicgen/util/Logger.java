package com.elitecore.elitelicgen.util;

import com.elitecore.elitelicgen.exception.EliteExceptionUtils;


public class Logger {

    //private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class.toString());
    
    private static ILogger logger = new Log4jLogger();
    
    public static void logError(String strModule, String strMessage) {        
          logger.error("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logDebug(String strModule, String strMessage){
          logger.debug("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logInfo(String strModule, String strMessage){
          logger.info("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logWarn(String strModule, String strMessage){
          logger.warn("[ "+ strModule +" ] : "+ strMessage);
    }
    public static void logFatal(String strModule, String strMessage){
          logger.fatal("[ "+ strModule +" ] : "+ strMessage);
    }
    
    public static void logTrace(String strModule,String strMessage){
        logger.trace("[ "+ strModule +" ] : "+ strMessage);
    }
    
    public static void logTrace(String strModule,Throwable exception){
        logger.trace("[ "+ strModule +" ] : "+"\n"+ EliteExceptionUtils.getRootCauseStackTraceAsString(exception));
    }
    
    public static void setLogger(ILogger newlogger) {
    	logger = newlogger;
    }
    
    public static ILogger getLogger(){
    	return logger;
    }
}
