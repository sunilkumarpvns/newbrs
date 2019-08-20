package com.elitecore.core.util.logger;


import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static com.elitecore.commons.base.Preconditions.*;

public class EliteRollingFileLogger implements ILogger, Closeable {

    public static final int TIME_BASED_ROLLING_TYPE = 1;
    public static final int SIZE_BASED_ROLLING_TYPE = 2;

    public static final int TIME_BASED_ROLLING_EVERY_MINUTE = 3;
    public static final int TIME_BASED_ROLLING_EVERY_HOUR = 4;
    public static final int TIME_BASED_ROLLING_EVERY_DAY = 5;
    public static final String SYSLOG_LOGGER = "Syslog";


    private final String serverInstanceName;


    private RollingFileLogger logger;
    private RollingFileLogger traceLogger;
    private SysLogger sysLogger;
    private MonitorLogger monitorLogger;

    // File logger related configuration parameters.
    private String logFileName;
    private String traceLogFileName;
    private int rollingType;
    private int rollingUnit;
    private int maxRolledUnits;
    private boolean compressRolledUnit;

    // Sys logger related configuration parameters.
    private Optional<String> syslogHostIp;
    private Optional<String> sysLogFacility;

    private AtomicLong relationKey;
    private boolean appendDiagnosticInformation;

    EliteRollingFileLogger(@Nonnull String serverInstanceName,
                           @Nonnull String targetFile, int rollingType, int rollingUnit,
                           int maxRolledUnits, boolean compressRolledUnit,
                           boolean appendDiagnosticInformation,
                           @Nonnull Optional<String> syslogHostIp,
                           @Nonnull Optional<String> sysLogFacility) {


        this.logFileName = appendServerInstanceName(targetFile, serverInstanceName);

        if (traceLogFileName == null) {
            traceLogFileName = appendServerInstanceName(targetFile + "-trace", serverInstanceName);
        }


        this.appendDiagnosticInformation = appendDiagnosticInformation;
        this.rollingType = rollingType;
        this.rollingUnit = rollingUnit;
        this.maxRolledUnits = maxRolledUnits;
        this.compressRolledUnit = compressRolledUnit;
        this.syslogHostIp = syslogHostIp;
        this.sysLogFacility = sysLogFacility;
        this.serverInstanceName = serverInstanceName;


        relationKey = new AtomicLong(0);
        applyRollingPolicy();
    }

    private String appendServerInstanceName(String logFileName,
                                            String serverInstanceName) {
        if (Strings.isNullOrBlank(serverInstanceName)) {
            return logFileName;
        }

        return logFileName + "-" + serverInstanceName;
    }


    public void setLogLevel(@Nullable String level) {

        if (level != null && logger != null) {
            logger.setLogLevel(level);
        }
    }


    public void setTraceLogLevelOn() {
        if (traceLogger != null) {
            traceLogger.setLogLevel(Level.TRACE.name());
        }
    }

    public void setTraceLogLevelOFF() {
        if (traceLogger != null) {
            traceLogger.setLogLevel(Level.OFF.name());
        }
    }

    public int getCurrentLogLevel() {
        return logger.getCurrentLogLevel();
    }

    public boolean isTraceLogEnabled() {
        return traceLogger.isTraceLogLevel();
    }

    public final boolean isCompressRolledUnit() {
        return compressRolledUnit;
    }

    public final int getRollingType() {
        return rollingType;
    }

    public final int getRollingUnit() {
        return rollingUnit;
    }

	/* Performance tuning parameters */

    public void error(String module, String strMessage) {
        logger.error(module, strMessage);

        if (sysLogger != null) {
            sysLogger.warn(module, strMessage);
        }

        if (monitorLogger.isActive()) {
            monitorLogger.error(module, strMessage);
        }
    }

    public void debug(String module, String strMessage) {
        logger.debug(module, strMessage);
        if (monitorLogger.isActive()) {
            monitorLogger.debug(module, strMessage);
        }
    }

    public void info(String module, String strMessage) {
        logger.info(module, strMessage);
        if (monitorLogger.isActive()) {
            monitorLogger.info(module, strMessage);
        }
    }

    public void warn(String module, String strMessage) {
        logger.warn(module, strMessage);
        if (sysLogger != null) {
            sysLogger.warn(module, strMessage);
        }
        if (monitorLogger.isActive()) {
            monitorLogger.warn(module, strMessage);
        }
    }

    public void trace(String module, String strMessage) {
        logger.trace(module, strMessage);
        if (monitorLogger.isActive()) {
            monitorLogger.trace(module, strMessage);
        }
    }

    public void trace(Throwable exception) {
        long localRelationKey = relationKey.getAndIncrement();

        logger.trace("ST KEY : [{}] {}", localRelationKey, exception.getMessage());
        traceLogger.trace(localRelationKey, exception);

        if (monitorLogger.isActive()) {
            monitorLogger.trace(exception);
        }
    }

    public void trace(String module, Throwable exception) {
        long localRelationKey = relationKey.getAndIncrement();

        logger.trace("[{}] ST KEY : [{}] {}", module, localRelationKey, exception.getMessage());
        traceLogger.trace((Object) module, localRelationKey, exception);
        if (monitorLogger.isActive()) {
            monitorLogger.trace(module, " ST KEY : [" + localRelationKey + "] " + exception.getMessage());
        }
    }

    public int getMaxRolledUnits() {
        return maxRolledUnits;
    }

    public void setMaxRolledUnits(int maxRolledUnits) {
        this.maxRolledUnits = maxRolledUnits;
    }

    /**
     * Frees all the occupied resources and flushes the buffered logs to file if
     * immediate flush is disabled.
     */
    public void close() {
        logger.close();
        monitorLogger.close();
        traceLogger.close();
        if (sysLogger != null) {
            sysLogger.close();
        }
    }

    private void applyRollingPolicy() {

        String layoutPattern = "%d{DATE} [%level] %t %msg";

        if(appendDiagnosticInformation) {
            layoutPattern = layoutPattern + " %X";
        }

        layoutPattern+="%n";

        if(Boolean.valueOf(System.getProperty("log.output.colored"))) {
            layoutPattern = "%highlight{" + layoutPattern + "}";
        }

        monitorLogger = new MonitorLogger(serverInstanceName + "-monitorLogger", logFileName + "-monitor.log");

        int sizeBaseRollingLimit = 0;
        RollingFileLogger.TimeBaseRollingType timeBaseRollingType = null;
        if(rollingType == SIZE_BASED_ROLLING_TYPE) {
            sizeBaseRollingLimit = rollingUnit;
        } else {
            timeBaseRollingType = RollingFileLogger.TimeBaseRollingType.fromId(rollingUnit);
        }



        logger = new RollingFileLogger.Builder(serverInstanceName + "-logger",
                logFileName + ".log")
                .sizeBaseRolling(sizeBaseRollingLimit)
                .timeBaseRolling(timeBaseRollingType)
                .maxRolledUnits(maxRolledUnits)
                .compressRolledUnits(compressRolledUnit)
                .withPatternLayout(layoutPattern).build();


        traceLogger = new RollingFileLogger.Builder(serverInstanceName + "-trace-logger",
                traceLogFileName + ".log")
                .sizeBaseRolling(sizeBaseRollingLimit)
                .timeBaseRolling(timeBaseRollingType)
                .maxRolledUnits(maxRolledUnits)
                .compressRolledUnits(compressRolledUnit)
                .withPatternLayout(layoutPattern).build();


        if (syslogHostIp.isPresent()) {
            sysLogger = new SysLogger(SYSLOG_LOGGER, syslogHostIp.get(), sysLogFacility.orElse(null));
        }


    }

    public final boolean isValidLogLevel(String strLogLevel) {
        return Arrays.stream(Level.values()).anyMatch(level -> level.name().equalsIgnoreCase(strLogLevel));
    }

    public final boolean isLogLevel(LogLevel logLevel) {
        return logger.isLogLevel(logLevel) || monitorLogger.isActive();
    }

    public void setSysLogHostIp(String syslogHost) {
        this.syslogHostIp = Optional.of(syslogHost);
    }

    public void setSysLogFacility(String sysLogFacility) {
        this.sysLogFacility = Optional.of(sysLogFacility);
    }

    @Override
    public void addThreadName(String threadName) {
        monitorLogger.addThreadName(threadName);
    }

    @Override
    public void removeThreadName(String threadName) {
        monitorLogger.removeThreadName(threadName);
    }

    @Override
    public boolean isErrorLogLevel() {
        return logger.isErrorLogLevel()
                || monitorLogger.isActive();
    }

    @Override
    public boolean isWarnLogLevel() {
        return logger.isWarnLogLevel()
                || monitorLogger.isActive();
    }

    @Override
    public boolean isInfoLogLevel() {
        return logger.isInfoLogLevel()
                || monitorLogger.isActive();
    }

    @Override
    public boolean isDebugLogLevel() {
        return logger.isDebugLogLevel()
                || monitorLogger.isActive();
    }


    /**
     * Helps in creating a logger instance
     *
     * @author narendra.pathai
     */
    public static class Builder {

        private final String serverInstanceName;
        private final String targetFileName;
        private int rollingType = TIME_BASED_ROLLING_TYPE;
        private int rollingUnit = TIME_BASED_ROLLING_EVERY_DAY;
        private int maxRolledUnits = 1;
        private boolean compressRolledUnit = true;
        private boolean appendDiagnosticInformation = false;
        private String syslogHostIP;
        private String syslogFacility;

        /**
         * Creates a new instance of Builder
         *
         * @param serverInstanceName a non-null server instance name that will be suffixed in the
         *                           log file name
         * @param targetFileName     a non-null absolute path followed by filename prefix
         * @throws NullPointerException if any argument is null
         */
        public Builder(@Nonnull String serverInstanceName,
                       @Nonnull String targetFileName) {
            this.serverInstanceName = checkNotNull(serverInstanceName, "serverInstanceName is null");
            this.targetFileName = checkNotNull(targetFileName, "targetFileName is null");
        }

        /**
         * The type of rolling the logger should perform Time based or Size based
         *
         * @param rollingType any value greater than zero
         * @return this
         * @throws IllegalArgumentException if rollingType is less than or equal to zero
         */
        public EliteRollingFileLogger.Builder rollingType(int rollingType) {
            checkArgument(rollingType == TIME_BASED_ROLLING_TYPE
                            || rollingType == SIZE_BASED_ROLLING_TYPE,
                    "rollingType should be one of Time Based (1) or Size Based (2)");
            this.rollingType = rollingType;
            return this;
        }

        /**
         * For time-based rolling, rollingUnit signifies whether the log should be
         * rolled per day, per hour or per minute.
         * <p>
         * <p><pre>
         * 	3		per minute {@link EliteRollingFileLogger#TIME_BASED_ROLLING_EVERY_MINUTE}
         * 	4		per hour {@link EliteRollingFileLogger#TIME_BASED_ROLLING_EVERY_HOUR}
         * 	5		per day {@link EliteRollingFileLogger#TIME_BASED_ROLLING_EVERY_DAY}
         * </pre>
         * <p>For size-based rolling, rollingUnit signifies no. of megabytes [MBs]
         * after which the log should be rolled.
         *
         * @return this
         * @throws IllegalArgumentException if rollingUnit is less than or equal to zero
         */
        public EliteRollingFileLogger.Builder rollingUnit(int rollingUnit) {
            checkArgument(rollingUnit > 0, "Invalid value: " + rollingUnit
                    + " for rollingUnit. Value should be greater than zero");
            this.rollingUnit = rollingUnit;
            return this;
        }

        /**
         * Maximum number of times rolling should be performed. After the limit,
         * no rolling will be performed and all contents will be appended in  the
         * active file
         *
         * @param maxRolledUnits any value greater than zero specifying the maximum
         *                       no of times rolling should be performed
         * @return this
         * @throws IllegalArgumentException if maxRolledUnits is less than or equal to zero
         */
        public EliteRollingFileLogger.Builder maxRolledUnits(int maxRolledUnits) {
            checkArgument(maxRolledUnits > 0, "Invalid value: " + maxRolledUnits
                    + " for maxRolledUnits. Value should be greater than zero");
            this.maxRolledUnits = maxRolledUnits;
            return this;
        }

        /**
         * Specifies whether the rolled units should be compressed using Logger API
         *
         * @param compressRolledUnit true enables compression of rolled units, false disables it
         * @return this
         */
        public EliteRollingFileLogger.Builder compressRolledUnits(boolean compressRolledUnit) {
            this.compressRolledUnit = compressRolledUnit;
            return this;
        }

        /**
         * @param syslogHostIP   a non-null host IP
         * @param syslogFacility a non-null syslog facility
         * @return this
         * @throws NullPointerException if any argument is null
         */
        public EliteRollingFileLogger.Builder sysLogParameters(@Nonnull String syslogHostIP, @Nonnull String syslogFacility) {
            this.syslogHostIP = checkNotNull(syslogHostIP, "syslogHostIP is null");
            this.syslogFacility = checkNotNull(syslogFacility, "syslogFacility is null");
            return this;
        }

        /**
         * Specifies whether the append context (MDC) parameter at end of the log
         *
         * @param appendDiagnosticInformation true log context information, false disables it
         * @return this
         */
        public EliteRollingFileLogger.Builder appendDiagnosticInformation(boolean appendDiagnosticInformation) {
            this.appendDiagnosticInformation = appendDiagnosticInformation;
            return this;
        }

        /**
         * Builds a new {@link com.elitecore.core.util.logger.EliteRollingFileLogger} instance based on the
         * configuration
         *
         * @return a newly created {@link com.elitecore.core.util.logger.EliteRollingFileLogger}
         * @throws IllegalStateException if builder configuration is improper
         */
        public EliteRollingFileLogger build() {
            validateRollingUnit();

            return new EliteRollingFileLogger(serverInstanceName, targetFileName,
                    rollingType, rollingUnit, maxRolledUnits, compressRolledUnit, appendDiagnosticInformation,
                    Optional.of(syslogHostIP), Optional.of(syslogFacility));
        }

        private void validateRollingUnit() {

            if(rollingType == TIME_BASED_ROLLING_TYPE) {
                checkState(rollingUnit == TIME_BASED_ROLLING_EVERY_DAY
                                || rollingUnit == TIME_BASED_ROLLING_EVERY_HOUR
                                || rollingUnit == TIME_BASED_ROLLING_EVERY_MINUTE
                        , "Invalid value: " + rollingUnit
                                + " of rollingUnit for rollingType: TIME_BASED_ROLLING");
            }
        }
    }
}