package com.elitecore.core.util.logger;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.message.ParameterizedMessage;

public class SysLogger {


    private Logger logger;
    private String syslogHost;
    private String facility;
    private LoggerContext loggerContext;
    private String loggerName;


    public SysLogger(String loggerName, String syslogHost, String facility) {
        this.loggerName = loggerName;
        this.syslogHost = syslogHost;
        this.facility = facility;
    }


    public void init() throws InitializationFailedException {
        try {

            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setStatusLevel(Level.ALL);
            builder.setConfigurationName("RollingBuilder");


            builder.add(builder.newAsyncRootLogger(Level.WARN));
            loggerContext = Configurator.initialize(builder.build());


            String strippedHostIp = syslogHost;
            String port = "";
            if (syslogHost.contains(":")) {
                int index = strippedHostIp.indexOf(":");
                strippedHostIp = strippedHostIp.substring(0, index);
                port = strippedHostIp.substring(index + 1, syslogHost.length());
            }

            LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                    .addAttribute("pattern", "%m");


            AppenderComponentBuilder appenderComponentBuilder = builder.newAppender(loggerName, "Socket")
                    .addAttribute("host", strippedHostIp)
                    .addAttribute("port", port)
                    .addAttribute("facility", facility)
                    .add(layoutBuilder);


            builder.add(appenderComponentBuilder);
// create the new logger
            LoggerComponentBuilder loggerComponentBuilder = builder.newAsyncLogger("SysLogger", Level.ALL)
                    .add(builder.newAppenderRef("rolling"))
                    .addAttribute("additivity", false);

            if (Strings.isNullOrBlank(facility) == false) {
                layoutBuilder.addAttribute("facility", facility);
            }
            builder.add(loggerComponentBuilder);


            logger = loggerContext.getLogger(loggerName);
        } catch (Exception e) {
            throw new InitializationFailedException(e.getMessage());
        }

    }

    public void error(String strMessage) {
        logger.error( "{}", strMessage);
    }

    public void warn(String strMessage) {
        logger.warn( "{}", strMessage);
    }

    public void info(String strMessage) {
        logger.info( "{}", strMessage);
    }

    public void fatal(String strMessage) {
        logger.fatal( "{}", strMessage);
    }
    public void error(String module, String strMessage) {
        logger.error( "[{}]: {}", module, strMessage);
    }

    public void debug(String module, String strMessage) {
        logger.debug( "[{}]: {}", module, strMessage);
    }

    public void info(String module, String strMessage) {
        logger.info( "[{}]: {}", module, strMessage);
    }

    public void warn(String module, String strMessage) {
        logger.warn( "[{}]: {}", module, strMessage);
    }

    public void trace(String module, String strMessage) {
        logger.trace( "[{}]: {}", module, strMessage);

    }

    public void trace(long localRelationKey, Throwable exception) {
        logger.trace(new ParameterizedMessage("ST KEY : [{}] ", localRelationKey, exception));
    }

    public void trace(long localRelationKey, String execeptionMessage) {
        logger.trace("ST KEY : [{}] {}", localRelationKey, execeptionMessage);
    }

    public void trace(long localRelationKey, String module, String exceptionMessage) {
        logger.trace("[{}] ST KEY : [{}] {}", module, localRelationKey, exceptionMessage);
    }

    public void trace(long localRelationKey, String module, Throwable exception) {
        logger.trace(new ParameterizedMessage("[{}] ST KEY : [{}] ", new Object[]{module, localRelationKey}, exception));
    }

    public void close() {
        logger.getContext().close();
    }


}
