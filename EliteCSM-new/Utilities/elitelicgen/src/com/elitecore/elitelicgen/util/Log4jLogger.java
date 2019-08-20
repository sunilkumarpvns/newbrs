/**
 * 
 */

package com.elitecore.elitelicgen.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Log4jLogger implements ILogger {
    
    public static Logger            logger = null;
    private static SimpleDateFormat sdf    = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
    private static final String     MODULE = "LOGGER";
    
    public Log4jLogger() {
        logger = Logger.getLogger("EliteLog4j");
        System.out.println("Logget is going to init()");
        String fileName = null;
        Layout layout = new PatternLayout("%m%n");
        ConsoleAppender consolAppender = null;
        FileAppender appender = null;
        
        try {
            File logDir = new File("../logs");
            
            if (logDir.exists())
                fileName = "../logs/EliteLicGen.log";
            else fileName = "./EliteLicGen.log";
            System.out.println("Logfile :" + fileName);
            consolAppender = new ConsoleAppender(layout);
            appender = new FileAppender(layout, fileName, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not able to get File appender");
        }
        
        logger.addAppender(consolAppender);
        logger.addAppender(appender);
        logger.setLevel((Level) Level.TRACE);
        
        logger.info("[ " + dateToString(new Date()) + " ]" + " [ INFO  ] " + "[" + MODULE + "]" + "  :  Server Started on " + dateToString(new Date()));
        logger.trace("[ " + dateToString(new Date()) + " ]" + " [ TRACE ] " + "[" + MODULE + "]" + "  :  Here is some TRACE");
        logger.debug("[ " + dateToString(new Date()) + " ]" + " [ DEBUG ] " + "[" + MODULE + "]" + "  :  Here is some DEBUG");
        logger.info("[ " + dateToString(new Date()) + " ]" + " [ INFO  ] " + "[" + MODULE + "]" + "  :  Here is some INFO");
        logger.warn("[ " + dateToString(new Date()) + " ]" + " [ WARN  ] " + "[" + MODULE + "]" + "  :  Here is some WARN");
        logger.error("[ " + dateToString(new Date()) + " ]" + " [ ERROR ] " + "[" + MODULE + "]" + "  :  Here is some ERROR");
        logger.fatal("[ " + dateToString(new Date()) + " ]" + " [ FATAL ] " + "[" + MODULE + "]" + "  :  Here is some FATAL");
    }
    
    public void trace( String strMessage ) {        
        logger.trace("[ " + dateToString(new Date()) + " ]" + " [ TRACE ] " + strMessage);
    }
    
    public void debug( String strMessage ) {
        logger.debug("[ " + dateToString(new Date()) + " ]" + " [ DEBUG ] " + strMessage);
    }
    
    public void info( String strMessage ) {
        logger.info("[ " + dateToString(new Date()) + " ]" + " [ INFO ] " + strMessage);
    }
    
    public void warn( String strMessage ) {
        logger.warn("[ " + dateToString(new Date()) + " ]" + " [ WARN ] " + strMessage);
    }
    
    public void error( String strMessage ) {
        logger.error("[ " + dateToString(new Date()) + " ]" + " [ ERROR ] " + strMessage);
    }
    
    public void fatal( String strMessage ) {
        logger.fatal("[ " + dateToString(new Date()) + " ]" + " [ FATAL ] " + strMessage);
    }
    
    public void trace( Throwable exception ) {        
        logger.trace(exception);
    }
    
    protected String dateToString( Date date ) {
        return sdf.format(date);
    }
    
}
