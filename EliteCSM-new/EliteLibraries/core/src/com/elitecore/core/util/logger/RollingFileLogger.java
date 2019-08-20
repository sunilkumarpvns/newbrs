package com.elitecore.core.util.logger;

import com.elitecore.commons.logging.LogLevel;
import org.apache.commons.csv.CSVFormat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.layout.CsvParameterLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.ParameterizedMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.Arrays;

import static com.elitecore.commons.base.Preconditions.*;

public class RollingFileLogger implements Closeable  {


    private Logger logger;
    // File logger related configuration parameters.
    private final String logFileName;
    private final int sizeBaseRollingLimitInKb;
    @Nonnull
    private final LayoutBuilder layout;
    @Nullable
    private final TimeBaseRollingType timeBaseRollingType;
    private final RollingType rollingType;
    private final int maxRolledUnits;
    private final boolean compressRolledUnit;
    private final String loggerName;

    RollingFileLogger(@Nonnull String loggerName,
                      @Nonnull String logFileName,
                      @Nonnull LayoutBuilder layout,
                      @Nonnull RollingType rollingType,
                      TimeBaseRollingType timeBaseRollingType,
                      int sizeLimit,
                      int maxRolledUnits,
                      boolean compressRolledUnit
                      ) {
        this.loggerName = loggerName;
        this.logFileName = logFileName;
        this.layout = layout;
        this.timeBaseRollingType = timeBaseRollingType;
        this.sizeBaseRollingLimitInKb = sizeLimit;
        this.rollingType = rollingType;
        this.maxRolledUnits = maxRolledUnits;
        this.compressRolledUnit = compressRolledUnit;
        init(layout);
    }


    public void setLogLevel(@Nullable String level) {
        if (level != null && logger != null){
            Level oldLogLevel = logger.getLevel();
            Level logLevel = Level.toLevel(level, oldLogLevel);
            logger.setLevel(logLevel);
        }
    }


    public int getCurrentLogLevel() {
        return logger.getLevel().intLevel();
    }

    public final boolean isCompressRolledUnit() {
        return compressRolledUnit;
    }

    public final String getFileName() {
        return logFileName;
    }

	/* Performance tuning parameters */

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

    public void trace(Throwable exception) {
        logger.trace("ST KEY :",exception);
    }

    public void trace(long localRelationKey, Throwable exception) {
        logger.trace(new ParameterizedMessage("ST KEY : [{}] ", localRelationKey, exception));
    }

    public void trace(String message, Object param1, Object param2, Object param3) {
        logger.trace(message, param1, param2, param3);
    }

    public void trace(String message, Object param1, Object param2) {
        logger.trace(message, param1, param2);
    }

    public void trace(Object param1, Object param2, Throwable throwable) {
        logger.trace(new ParameterizedMessage("[{}] ST KEY : [{}] ", new Object[]{param1, param2}, throwable));
    }

    public void trace(Object param1, Throwable throwable) {
        logger.trace(new ParameterizedMessage("[{}] ST KEY : ", new Object[]{param1}, throwable));
    }

    public void trace(long localRelationKey, String module, String exceptionMessage) {
        logger.trace("[{}] ST KEY : [{}] {}", module, localRelationKey, exceptionMessage);
    }

    public void trace(long localRelationKey, String module, Throwable exception) {
        logger.trace(new ParameterizedMessage("[{}] ST KEY : [{}] ", new Object[]{module, localRelationKey}, exception));
    }


    public void warn(final String message) {
        logger.warn(message);
    }

    public void warn(final String message, final Object param0) {

        logger.warn(message, param0);

    }

    public void warn(final String message, final Object param0,
                     final Object param1) {

        logger.warn(message, param0, param1);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2) {

        logger.warn(message, param0, param1, param2);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3) {

        logger.warn(message, param0, param1, param2, param3);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3,
                     final Object param4) {

        logger.warn(message, param0, param1, param2, param3, param4);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3,
                     final Object param4, final Object param5) {

        logger.warn(message, param0, param1, param2, param3, param4, param5);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3,
                     final Object param4, final Object param5, final Object param6) {

        logger.warn(message, param0, param1, param2, param3, param4, param5, param6);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3,
                     final Object param4, final Object param5, final Object param6,
                     final Object param7) {

        logger.warn(message, param0, param1, param2, param3, param4, param5, param6, param7);

    }

    public void warn(final String message, final Object param0,
                     final Object param1, final Object param2, final Object param3,
                     final Object param4, final Object param5, final Object param6,
                     final Object param7, final Object param8) {

        logger.warn(message, param0, param1, param2, param3, param4, param5, param6, param7, param8);

    }

    public void warn(final String message, final Object param0,
                             final Object param1, final Object param2, final Object param3,
                             final Object param4, final Object param5, final Object param6,
                             final Object param7, final Object param8, final Object param9) {

        logger.warn(message, param0, param1, param2, param3, param4, param5, param6, param7, param8, param9);

    }

    public int getMaxRolledUnits() {
        return maxRolledUnits;
    }

    /**
     * Frees all the occupied resources and flushes the buffered logs to file if
     * immediate flush is disabled.
     */
    public void close() {
        logger.getContext().close();
    }

    private void init(LayoutBuilder layout) {


        String fileNamePattern = this.rollingType.getFileNamePattern(timeBaseRollingType);

        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);

        final Configuration config = loggerContext.getConfiguration();
        config.getRootLogger().setLevel(Level.ALL);


        createFileLogger(fileNamePattern, loggerContext, config, layout);


    }



    private void createFileLogger(String filePattern, LoggerContext ctx, Configuration configuration, LayoutBuilder layout) {


        TriggeringPolicy triggeringPolicy = rollingType.createPolicy(sizeBaseRollingLimitInKb);

        DefaultRolloverStrategy strategy = createStrategy(configuration);




        RollingRandomAccessFileAppender appender = createAppender(loggerName,
                logFileName,
                filePattern,
                configuration,
                layout.build(configuration),
                triggeringPolicy,
                strategy);

        appender.start();
        configuration.addAppender(appender);


        AppenderRef appenderRef = AppenderRef.createAppenderRef(loggerName, Level.ALL, null);

        AsyncLoggerConfig asyncLoggerConfig = (AsyncLoggerConfig) AsyncLoggerConfig.createLogger("false",
                Level.ALL.name(),
                loggerName,
                "false",
                new AppenderRef[]{appenderRef},
                null,
                configuration,
                null);


        asyncLoggerConfig.addAppender(appender, null, null);

        configuration.addLogger(loggerName, asyncLoggerConfig);

        configuration.initialize();
        configuration.start();

        configuration.getRootLogger().setLevel(Level.ALL);


        if(ctx.isInitialized() == false) {
            /*
            code copy from log4jContextFactory#getContext
             */
            ContextAnchor.THREAD_CONTEXT.set(ctx);
            try {
                ctx.start();
            } finally {
                ContextAnchor.THREAD_CONTEXT.remove();
            }
        } else {
            ctx.updateLoggers();
        }

        configuration.getRootLogger().setLevel(Level.ALL);

        logger = ctx.getLogger(loggerName);

    }

    private RollingRandomAccessFileAppender createAppender(String appenderName,
                                                           String logFileName,
                                                           String filePattern,
                                                           Configuration configuration,
                                                           StringLayout layout,
                                                           TriggeringPolicy triggeringPolicy,
                                                           DefaultRolloverStrategy strategy) {
        return RollingRandomAccessFileAppender.newBuilder().withFileName(logFileName)
                .withFilePattern(logFileName + filePattern + (isCompressionApplicable() ? ".log.gz" : ".log"))
                .withPolicy(triggeringPolicy)
                .withStrategy(strategy)
                .withLayout(layout)
                .withName(appenderName)
                .setConfiguration(configuration)
                .build();


    }


    private DefaultRolloverStrategy createStrategy(Configuration configuration) {
        return DefaultRolloverStrategy.newBuilder().withMax("" + maxRolledUnits).withFileIndex("1").withConfig(configuration).build();


    }




    private boolean isCompressionApplicable() {
        return compressRolledUnit;
    }

    public final boolean isValidLogLevel(String strLogLevel) {
        return Arrays.stream(Level.values()).anyMatch(level -> level.name().equalsIgnoreCase(strLogLevel));
    }

    public final boolean isLogLevel(LogLevel logLevel) {
        switch (logLevel) {
            case NONE:
                return isNoneLogLevel();
            case ERROR:
                return isErrorLogLevel();
            case WARN:
                return isWarnLogLevel();
            case INFO:
                return isInfoLogLevel();
            case DEBUG:
                return isDebugLogLevel();
            case TRACE:
                return isTraceLogLevel();
            case ALL:
                return isAllLogLevel();
            default:
                throw new AssertionError();
        }
    }

    private boolean isAllLogLevel() {
        return logger.isEnabled(Level.ALL, null, null);
    }


    private boolean isNoneLogLevel() {
        return logger.isEnabled(Level.OFF, null, null);

    }


    public boolean isErrorLogLevel() {
        return logger.isErrorEnabled();
    }

    public boolean isWarnLogLevel() {
        return logger.isWarnEnabled();
    }

    public boolean isInfoLogLevel() {
        return logger.isInfoEnabled();
    }

    public boolean isDebugLogLevel() {
        return logger.isDebugEnabled();
    }

    public void info(String message, Object param0, Object param1) {
        logger.info(message, param0, param1);
    }

    public boolean isTraceLogLevel() {
        return logger.isTraceEnabled();
    }


    /**
     * Helps in creating a logger instance
     *
     * @author narendra.pathai
     *
     */
    public static class Builder {

        private final String targetFileName;
        private final String loggerName;
        private TimeBaseRollingType timeBaseRollingType;
        private int sizeLimitInKb = 0;
        private int maxRolledUnits = 1;
        private boolean compressRolledUnit = true;
        private LayoutBuilder layoutBuilder = new PatternLayoutBuilder();

        /**
         * Creates a new instance of Builder
         *
         * log file name
         * @param targetFileName a non-null absolute path followed by filename prefix
         * @throws NullPointerException if any argument is null
         */
        public Builder(@Nonnull String loggerName,
                       @Nonnull String targetFileName) {

            this.loggerName = checkNotNull(loggerName, "logger name is null");
            this.targetFileName = checkNotNull(targetFileName, "targetFileName is null");
        }


        public RollingFileLogger.Builder sizeBaseRolling(int sizeInKb) {
            this.sizeLimitInKb = sizeInKb;
            return this;
        }

        public RollingFileLogger.Builder timeBaseRolling(TimeBaseRollingType timeBaseRollingType) {
            this.timeBaseRollingType = timeBaseRollingType;
            return this;
        }

        /**
         * Maximum number of times rolling should be performed. After the limit,
         * no rolling will be performed and all contents will be appended in  the
         * active file
         *
         * @param maxRolledUnits any value greater than zero specifying the maximum
         * no of times rolling should be performed
         *
         * @return this
         * @throws IllegalArgumentException if maxRolledUnits is less than or equal to zero
         */
        public RollingFileLogger.Builder maxRolledUnits(int maxRolledUnits) {
            checkArgument(maxRolledUnits > 0, "Invalid value: " + maxRolledUnits
                    + " for maxRolledUnits. Value should be greater than zero");
            this.maxRolledUnits = maxRolledUnits;
            return this;
        }

        /**
         * Specifies whether the rolled units should be compressed using Logger API
         * @param compressRolledUnit true enables compression of rolled units, false disables it
         *
         * @return this
         */
        public RollingFileLogger.Builder compressRolledUnits(boolean compressRolledUnit) {
            this.compressRolledUnit = compressRolledUnit;
            return this;
        }


        public RollingFileLogger.Builder withCSVLayout(CSVFormat csvFormat) {
            this.layoutBuilder = new CSVLayoutBuilder(csvFormat);
            return this;
        }

        public RollingFileLogger.Builder withPatternLayout(String pattern) {
            this.layoutBuilder = new PatternLayoutBuilder(pattern);
            return this;
        }



        /**
         * Builds a new {@link EliteRollingFileLogger} instance based on the
         * configuration
         *
         * @throws IllegalStateException if builder configuration is improper
         * @return a newly created {@link EliteRollingFileLogger}
         */
        public RollingFileLogger build() {


            return new RollingFileLogger(loggerName, targetFileName,
                    layoutBuilder,
                    RollingType.fromVal(sizeLimitInKb, timeBaseRollingType),
                    timeBaseRollingType, sizeLimitInKb, maxRolledUnits, compressRolledUnit);
        }


    }


    private static interface LayoutBuilder {
        StringLayout build(Configuration configuration);
    }

    public static class PatternLayoutBuilder implements LayoutBuilder {
        private final String pattern;

        public PatternLayoutBuilder(String pattern) {
            this.pattern = pattern;
        }

        public PatternLayoutBuilder() {
            this.pattern = "%d{DATE} [%level] %t %msg%n";
        }

        @Override
        public StringLayout build(Configuration configuration) {
            return PatternLayout.newBuilder().withPattern(pattern).withConfiguration(configuration).build();
        }

    }
    public static class CSVLayoutBuilder implements LayoutBuilder {
        private final CSVFormat csvFormat;

        public CSVLayoutBuilder(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
        }


        @Override
        public StringLayout build(Configuration configuration) {
            return CsvParameterLayout.createLayout(csvFormat);
        }
    }


    public enum TimeBaseRollingType {
        EVERY_MINUTE(3, "_%d{yyyy-MM-dd_HH_mm}"),
        EVERY_HOUR(4, "_%d{yyyy-MM-dd_HH}"),
        EVERY_DAY(5,"_%d");


        public final int id;
        private final String fileNamePattern;

        TimeBaseRollingType(int id, String fileNamePattern) {
            this.id = id;
            this.fileNamePattern = fileNamePattern;
        }

        public String getFileNamePattern() {
            return fileNamePattern;
        }

        public static TimeBaseRollingType fromId(int rollingUnit) {
            if(rollingUnit == 3) {
                return EVERY_MINUTE;
            } else if(rollingUnit == 4) {
                return EVERY_HOUR;
            } else if(rollingUnit == 5) {
                return EVERY_DAY;
            }
            return null;
        }
    }

    public enum RollingType {
        TIME_BASE_ROLLING {
            @Override
            public String getFileNamePattern(TimeBaseRollingType timeBaseRollingType) {
                return timeBaseRollingType.getFileNamePattern();
            }

            @Override
            public AbstractTriggeringPolicy createPolicy(int sizeBaseRollingLimit) {
                return TimeBasedTriggeringPolicy.newBuilder().withInterval(1).build();
            }
        },
        SIZE_BASE_ROLLING {
            @Override
            public String getFileNamePattern(TimeBaseRollingType timeBaseRollingType) {
                return "_%i";
            }

            @Override
            public AbstractTriggeringPolicy createPolicy(int sizeBaseRollingLimit) {
                return SizeBasedTriggeringPolicy.createPolicy(sizeBaseRollingLimit + "KB");
            }
        },
        TIME_AND_SIZE_BASE_ROLLING {
            @Override
            public String getFileNamePattern(TimeBaseRollingType timeBaseRollingType) {
                return TIME_BASE_ROLLING.getFileNamePattern(timeBaseRollingType) + SIZE_BASE_ROLLING.getFileNamePattern(timeBaseRollingType);
            }

            @Override
            public AbstractTriggeringPolicy createPolicy(int sizeBaseRollingLimit) {
                return CompositeTriggeringPolicy.createPolicy(TIME_BASE_ROLLING.createPolicy(sizeBaseRollingLimit)
                        , SIZE_BASE_ROLLING.createPolicy(sizeBaseRollingLimit));
            }
        };

        public abstract String getFileNamePattern(TimeBaseRollingType timeBaseRollingType);

        public abstract AbstractTriggeringPolicy createPolicy(int sizeBaseRollingLimit);

        public static RollingType fromVal(int sizeBaseRollingLimitInKb, TimeBaseRollingType timeBaseRollingType) {


            validateRollingUnit(sizeBaseRollingLimitInKb, timeBaseRollingType);

            if (sizeBaseRollingLimitInKb > 0 && timeBaseRollingType != null) {
                return TIME_AND_SIZE_BASE_ROLLING;
            } else if (sizeBaseRollingLimitInKb > 0) {
                return SIZE_BASE_ROLLING;
            } else {
                return TIME_BASE_ROLLING;
            }
        }

        private static void validateRollingUnit(int sizeBaseRollingLimitInKb, TimeBaseRollingType timeBaseRollingType) {
            checkArgument(sizeBaseRollingLimitInKb > 0 || timeBaseRollingType != null, "Rolling Type not provided");
        }
    }
}
