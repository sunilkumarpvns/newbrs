package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.fileio.loactionalloactor.FileLocationAllocatorFactory;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverFieldMappingData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverStripMappingData;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.CSVDriverConfigurationImpl;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.corenetvertex.constants.CommonConstants.DEFAULT_TIMESTAMP_FORMAT;
import static com.elitecore.netvertex.core.util.ConfigurationUtil.stringToBoolean;
import static com.elitecore.netvertex.core.util.ConfigurationUtil.stringToInteger;
import static com.elitecore.netvertex.core.util.ConfigurationUtil.stringToLong;

public class CSVCDRDriverConfigurationFactory {
    private static final String MODULE = "CSV-CDR-DRIVER-CONF-FACTORY";


    public CSVDriverConfigurationImpl create(DriverData driverData) throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {
        String tempValue;
        int port = 0000;
        boolean header = true;
        boolean sequenceGlobalization = false;
        int failOverTime = 3;
        int timeBoundary = 0;
        long timeBasedRollingUnit = 0;
        long sizeBasedRollingUnit = 0;
        long recordBasedRollingUnit = 0;
        SimpleDateFormat cdrTimeStampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);

        List<CSVFieldMapping> fieldMappings = new ArrayList<>();
        Map<String, CSVStripMapping> stripMappings = new HashMap<>();
        EnumMap<RollingTypeConstant, Integer> rollingTypeMap = new EnumMap<RollingTypeConstant, Integer>(RollingTypeConstant.class);

        CsvDriverData csvDriverData = driverData.getCsvDriverData();
        tempValue = csvDriverData.getHeader();
        if (tempValue != null && !tempValue.trim().isEmpty()) {
            header = stringToBoolean("CSV header", tempValue, header);
        }

        tempValue = csvDriverData.getTimeStampFormat();
        if (tempValue != null && tempValue.trim().isEmpty() == false) {
            try {
                cdrTimeStampFormat = new SimpleDateFormat(tempValue);
            } catch (IllegalArgumentException e) {
                cdrTimeStampFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
                LogManager.getLogger().warn(MODULE, "Using default timestamp format: " + DEFAULT_TIMESTAMP_FORMAT + ". Reason: Invalid cdr timestamp format: " + tempValue);
                LogManager.getLogger().trace(MODULE, e);
            }
        } else {
            LogManager.getLogger().info(MODULE, "Timestamp format not configured. timestamp will not going to be dumped for csv file");
        }

        tempValue = csvDriverData.getSequenceGlobalization();
        if (tempValue != null && !tempValue.trim().isEmpty()) {
            sequenceGlobalization = stringToBoolean("CSV sequence globalization", tempValue, sequenceGlobalization);
        }

        tempValue = csvDriverData.getAddress();
        if (!"local".equalsIgnoreCase(csvDriverData.getAllocatingProtocol())) {
            if (tempValue != null && !tempValue.trim().isEmpty()) {
                try {
                    URLData urlData = URLParser.parse(tempValue);
                    csvDriverData.setAddress(urlData.getHost());
                    port = urlData.getPort();
                    if (urlData.getPort() == URLParser.UNKNOWN_PORT) {
                        if (FileLocationAllocatorFactory.FTPS.equalsIgnoreCase(csvDriverData.getAllocatingProtocol())) {
                            port = 22;
                        } else if (FileLocationAllocatorFactory.SMTP.equalsIgnoreCase(csvDriverData.getAllocatingProtocol())) {
                            port = 25;
                        }
                    }
                } catch (InvalidURLException e) {
                    LogManager.getLogger().error(MODULE, "Error while parsing address. Reason: Invalid address:" + tempValue);
                    LogManager.ignoreTrace(e);
                }
            } else {
                LogManager.getLogger().error(MODULE, "Using default file allocator address " + csvDriverData.getAddress() + ":" + port + ". Reason: Address not configured for remote file allocator");
            }
        }

        tempValue = csvDriverData.getRemoteFileLocation();
        if (tempValue != null && !tempValue.trim().isEmpty()) {
            csvDriverData.setRemoteFileLocation(tempValue);
        }

        csvDriverData.setUserName(csvDriverData.getUserName());

        csvDriverData.setPassword(csvDriverData.getPassword());

        if (Strings.isNullOrBlank(csvDriverData.getPassword()) == false) {
            csvDriverData.setPassword(PasswordEncryption.getInstance().decrypt(csvDriverData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT));
        }


        tempValue = csvDriverData.getFailOverTime() == null ? String.valueOf(failOverTime) : csvDriverData.getFailOverTime().toString();
        if (tempValue != null && !tempValue.trim().isEmpty()) {
            csvDriverData.setFailOverTime(stringToInteger("fail over time", tempValue, failOverTime));
        }

        csvDriverData.setReportingType(csvDriverData.getReportingType());

        if (csvDriverData.getReportingType() != null) {

            String timeBasedRolling = (csvDriverData.getTimeBasedRollingUnit()!=null)?csvDriverData.getTimeBasedRollingUnit().toString():null;
            if (timeBasedRolling != null && timeBasedRolling.trim().isEmpty() == false) {
                timeBasedRollingUnit = stringToLong("Time based rolling unit", timeBasedRolling, timeBasedRollingUnit);
                rollingTypeMap.put(RollingTypeConstant.TIME_BASED_ROLLING, Integer.parseInt(String.valueOf(timeBasedRollingUnit)));
            } else {
                timeBasedRolling = (csvDriverData.getTimeBoundary()!=null)?csvDriverData.getTimeBoundary().toString():null;
                if (timeBasedRolling != null && timeBasedRolling.trim().isEmpty() == false) {
                    timeBoundary = stringToInteger("Time boundary", timeBasedRolling, timeBoundary);
                    rollingTypeMap.put(RollingTypeConstant.TIME_BASED_ROLLING, timeBoundary);
                }
            }

            String sizeBasedRolling = (csvDriverData.getSizeBasedRollingUnit()!=null)?csvDriverData.getSizeBasedRollingUnit().toString():null;
            if (sizeBasedRolling != null && sizeBasedRolling.trim().isEmpty() == false) {
                sizeBasedRollingUnit = stringToLong("size based rolling unit", sizeBasedRolling, sizeBasedRollingUnit);
                rollingTypeMap.put(RollingTypeConstant.SIZE_BASED_ROLLING, Integer.parseInt(String.valueOf(sizeBasedRollingUnit)));
            }

            String recordBasedRolling = (csvDriverData.getRecordBasedRollingUnit()!=null)?csvDriverData.getRecordBasedRollingUnit().toString():null;
            if (recordBasedRolling != null && recordBasedRolling.trim().isEmpty() == false) {
                recordBasedRollingUnit = stringToLong("Record based rolling unit", recordBasedRolling, recordBasedRollingUnit);
                rollingTypeMap.put(RollingTypeConstant.RECORD_BASED_ROLLING, Integer.parseInt(String.valueOf(recordBasedRollingUnit)));
            }
        }

        List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList = csvDriverData.getCsvDriverFieldMappingDataList();
        List<CSVFieldMapping> tempFieldMappings = new ArrayList<>(fieldMappings);
        for (CsvDriverFieldMappingData csvDriverFieldMappingData : csvDriverFieldMappingDataList) {
            tempFieldMappings.add(new CSVFieldMapping(csvDriverFieldMappingData.getHeaderField(), csvDriverFieldMappingData.getPcrfKey()));
        }
        fieldMappings = tempFieldMappings;

        List<CsvDriverStripMappingData> csvDriverStripMappingDataList = csvDriverData.getCsvDriverStripMappingDataList();
        Map<String, CSVStripMapping> tempStripMappings = new HashMap<>(stripMappings);

        for (CsvDriverStripMappingData csvDriverStripMappingData : csvDriverStripMappingDataList) {
            CSVStripMapping csvStripMapping = createCsvStripMapping(csvDriverStripMappingData);
            tempStripMappings.put(csvStripMapping.getKey(), csvStripMapping);
        }
        stripMappings = tempStripMappings;

        return new CSVDriverConfigurationImpl(driverData.getId(), driverData.getDriverType(), driverData.getName().trim(), header, csvDriverData.getDelimiter(), csvDriverData.getFileName(),
                csvDriverData.getFileLocation(), csvDriverData.getDefaultFolderName(), csvDriverData.getFolderName(), csvDriverData.getPrefixFileName(), csvDriverData.getSequenceRange(),
                csvDriverData.getSequencePosition(), sequenceGlobalization, csvDriverData.getAllocatingProtocol(), csvDriverData.getAddress(),
                port, csvDriverData.getRemoteFileLocation(), csvDriverData.getUserName(), csvDriverData.getPassword(), csvDriverData.getPostOperation(), csvDriverData.getArchiveLocation(),
                csvDriverData.getFailOverTime(), csvDriverData.getReportingType(), timeBoundary,timeBasedRollingUnit, sizeBasedRollingUnit, recordBasedRollingUnit, fieldMappings, stripMappings, rollingTypeMap, cdrTimeStampFormat);
    }

    private CSVStripMapping createCsvStripMapping(CsvDriverStripMappingData csvDriverStripMappingData) {
        String key = csvDriverStripMappingData.getPcrfKey();
        String pattern = csvDriverStripMappingData.getPattern();
        String separator = csvDriverStripMappingData.getSeparator();

        return new CSVStripMapping(key, pattern, separator);
    }
}
