/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Sep 26, 2008
 *	@author Raghu.G
 *  Last Modified Oct 1, 2008
 */

/*
 * Log4jLogger.java
 * 
 * This class is used for logging all errors and info.
 * It used log4j api and implements ILogger interface.
 * Functionalities includes setting the log level,
 * logging with different info such as info(), warn(), debug(), trace(), log()
 * 
 */
package com.elitecore.classicrating.commons.util.logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.elitecore.classicrating.base.IRatingConstants;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;

public class Log4jLogger implements ILogger {

    private static final long serialVersionUID = 7515978684116484688L;

    /* Logger Object */
    public static Logger logger = null;

    /* Date format */
    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "dd-MMM-yyyy hh:mm:ss.SSS");
    private static Log4jLogger instance;

    public static Log4jLogger getInstance() {
        if (instance == null) {
            instance = new Log4jLogger();
        }
        return instance;
    }

    private Log4jLogger() {
        logger = Logger.getLogger(Logger.class.toString());

        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(IRatingConstants.LOG_FILE_NAME, "EliteClassicRating.log");
        parameterMap.put(IRatingConstants.LOG_FOLDER, "logs");

        Layout layout = new PatternLayout("%m%n"); /* layout of logging pattern */
        ConsoleAppender consolAppender = null;
        FileAppender appender = null;

        try {
            File logDir = new File(parameterMap.get(IRatingConstants.LOG_FOLDER));
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            String fileName = logDir.getAbsolutePath() + File.separatorChar +
                    parameterMap.get(IRatingConstants.LOG_FILE_NAME);

            consolAppender = new ConsoleAppender(layout);
            appender = new FileAppender(layout, fileName, true);
            logger.addAppender(consolAppender);
            logger.addAppender(appender);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not able to get File appender");
        }
        try {
            switch (Integer.parseInt(parameterMap.get(IRatingConstants.LOG_LEVEL))) {
                case IRatingConstants.OFF:
                    logger.setLevel(Level.OFF);
                    break;
                case IRatingConstants.INFO:
                    logger.setLevel(Level.INFO);
                    break;
                case IRatingConstants.WARN:
                    logger.setLevel(Level.WARN);
                    break;
                case IRatingConstants.DEBUG:
                    logger.setLevel(Level.DEBUG);
                    break;
                case IRatingConstants.ERROR:
                    logger.setLevel(Level.ERROR);
                    break;
                case IRatingConstants.FATAL:
                    logger.setLevel(Level.FATAL);
                    break;
                case IRatingConstants.TRACE:
                    logger.setLevel(Level.TRACE);
                    break;
                case IRatingConstants.ALL:
                    logger.setLevel(Level.ALL);
                    break;
            }
        } catch (NumberFormatException ne) {
            logger.setLevel(Level.TRACE);
        }
    }

    @Override
    public void trace(String module, String strMessage) {
        logger.trace("[" + dateToString(new Date()) + "] [TRACE] [" + module + "]    " + strMessage);
    }

    @Override
    public void debug(String module, String strMessage) {
        logger.trace("[" + dateToString(new Date()) + "] [DEBUG] [" + module + "]    " + strMessage);
    }

    @Override
    public void info(String module, String strMessage) {
        logger.trace("[" + dateToString(new Date()) + "] [INFO ] [" + module + "]    " + strMessage);
    }

    @Override
    public void warn(String module, String strMessage) {
        logger.trace("[" + dateToString(new Date()) + "] [WARN ] [" + module + "]    " + strMessage);
    }

    @Override
    public void error(String module, String strMessage) {
        logger.error("[" + dateToString(new Date()) + "] [ERROR] [" + module + "]    " + strMessage);
    }

    @Override
    public void trace(String module, Throwable exception) {
        logger.trace("[" + dateToString(new Date()) + "] [TRACE] [" + module + "]    " + exception.toString());
    }

    protected String dateToString(Date date) {
        return sdf.format(date);
    }

    public void setLogLevel(int logLevel) {
    }

    public void setLogLevel(String logLevel) {
    }

    @Override
    public void trace(Throwable exception) {
        trace("", exception);

    }

	@Override
	public int getCurrentLogLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isLogLevel(LogLevel level) {
		return true;
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
