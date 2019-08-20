package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class EDRDriver<T> extends BaseCSVDriver<T> {

    private static final String MODULE = "MONETARY-CDR-DRIVER";
    private static final String LOOP_BACK_ADDRESS = "127.0.0.1";
    private static final String LOCAL = "LOCAL";
    private static final String DELETE = "Delete";
    private static final String SUFFIX = "suffix";
    private final String name;
    private String csvHeader;

    public EDRDriver(String serverHome, String name, String csvHeader, CSVLineBuilder<T> csvLineBuilder, TaskScheduler schedular){
        super(serverHome,schedular, null);
        this.name = name;
        this.csvHeader = csvHeader;
        registerCSVLineBuilder(csvLineBuilder);
    }

    @Override
    public String getCSVHeaderLine() {
        return csvHeader;
    }

    @Override
    public String getCounterFileName() {
        return serverHome + File.separator + "system" + File.separator + "cdr_sequence_csv_driver";
    }

    @Override
    public String getDriverName() {
        return name;
    }

    @Override
    public String getDriverInstanceUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int getDriverType() {
        return DriverTypes.CSV_CDR_DRIVER;
    }

    @Override
    public SimpleDateFormat getCDRTimeStampFormat() {
        return new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT);
    }

    @Override
    public long getRollingUnit() {
        return CommonConstants.ONE_DAY;
    }

    @Override
    public int getRollingType() {
        return EliteFileWriter.TIME_BASED_ROLLING;
    }

    @Override
    public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
        return null;
    }

    @Override
    public String getParameterValue(T request, String key) {
        return null;
    }

    @Override
    public int getPort() {
        return 22;
    }

    @Override
    public int getFailOverTime() {
        return 3;
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public boolean isSequenceGlobalization() {
        return false;
    }

    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public String getMultipleDelimiter() {
        return null;
    }

    @Override
    public String getFileName() {
        return "EDR.csv";
    }

    @Override
    public String getFileLocation() {
        return "data" + File.separator + "csvfiles";
    }

    @Override
    public String getPrefixFileName() {
        return name;
    }

    @Override
    public String getDefaultDirectoryName() {
        return null;
    }

    @Override
    public String getDirectoryName() {
        return null;
    }

    @Override
    public String getSequenceRange() {
        return null;
    }

    @Override
    public String getSequencePosition() {
        return SUFFIX;
    }

    @Override
    public String getAllocatingProtocol() {
        return LOCAL;
    }

    @Override
    public String getIPAddress() {
        return LOOP_BACK_ADDRESS;
    }

    @Override
    public String getRemoteLocation() {
        return null;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String decrypt(String encriptedPassword, int encriptionType) {
        String password = null;
        try {
            password = PasswordEncryption.getInstance().decrypt(encriptedPassword, encriptionType);
        } catch (NumberFormatException | NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
            LogManager.getLogger().warn(MODULE, e.getMessage());
            ignoreTrace(e);
        } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                LogManager.getLogger().trace(MODULE, e);
        }
        return password;
    }

    @Override
    public String getPostOperation() {
        return DELETE;
    }

    @Override
    public String getArchiveLocation() {
        return null;
    }

    @Override
    public CSVStripMapping getStripMapping(String key) {
        return null;
    }

    @Override
    public List<CSVFieldMapping> getCSVFieldMappings() {
        return null;
    }

    @Override
    protected int getStatusCheckDuration() {
        return ESCommunicator.NO_SCANNER_THREAD;
    }

    @Override
    public String getName() {
        return getDriverName();
    }

    @Override
    public String getTypeName() {
        return MODULE;
    }

}