package com.elitecore.netvertex.core.driver.cdr.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.driver.cdr.MsccCdrFields;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CSVDriverConfigurationImpl implements CSVDriverConfiguration {

    public static final String MODULE = "CSV-CDRD-CNF";

    private String driverInstanceId;
    private String driverType;
    private int rollingType = EliteFileWriter.TIME_BASED_ROLLING;
    private long rollingUnit = EliteFileWriter.TIME_BASED_ROLLING_EVERY_DAY;
    private int port;
    private int failOverTime = 3;

    private boolean header = true;
    private boolean sequenceGlobalization = false;

    private String driverName;
    private String delimiter = ",";
    private String fileName = "Netvertex-CDR.csv";
    private String fileLocation = "data/csvfiles";
    private String prefixFileName;
    private String defaultDirName;
    private String dirName;
    private String sequenceRange;
    private String sequencePosition;
    private String allocatingProtocol = "Local";
    private String address = "0.0.0.0";
    private String remoteLocation;
    private String userName;
    private String password;
    private String postOperation = "Delete";
    private String archiveLocation;
    private SimpleDateFormat cdrTimeStampFormat;

    private String reportingType;
    private int timeBoundary;
    private long timeBasedRollingUnit;
    private long sizeBasedRollingUnit;
    private long recordBasedRollingUnit;

    private List<CSVFieldMapping> fieldMappings;
    private Map<String, CSVStripMapping> stripMappings;
    private EnumMap<RollingTypeConstant, Integer> rollingTypeMap;

    private String usageKeyHeader = "USAGE";
    private String inputOctetsHeader = "UPLOAD";
    private String outputOctetsHeader = "DOWNLOAD";
    private String totalOctetsHeader = "TOTAL";
    private String usageTimeHeader = "TIME";

    private String coreSessionIdHeader = "SESSION_ID";
    private String ratingGroupHeader = "RATING_GROUP";
    private String serviceIdentifierHeader = "SERVICES";
    private String reportedVolumeHeader = "REPORTED_VOLUME";
    private String reportedTimeHeader = "REPORTED_TIME";
    private String reportedEventHeader = "REPORTED_EVENT";
    private String volumePulseHeader = "VOLUME_PULSE";
    private String timePulseHeader = "TIME_PULSE";
    private String calculatedVolumePulseHeader = "CALCULATED_VOLUME_PULSE";
    private String calculatedTimePulseHeader = "CALCULATED_TIME_PULSE";
    private String unAccountedVolumeHeader = "UNACCOUNTED_VOLUME";
    private String unAccountedTimeHeader = "UNACCOUNTED_TIME";
    private String accountedNonMonetaryVolumeHeader = "ACCOUNTED_NON_MONETARY_VOLUME";
    private String accountedNonMonetaryTimeHeader = "ACCOUNTED_NON_MONETARY_TIME";
    private String accountedMonetaryHeader= "ACCOUNTED_MONETARY";
    private String rateHeader = "RATE";
    private String discountHeader = "DISCOUNT(%)";
    private String discountAmountHeader = "DISCOUNTED_AMOUNT";
    private String calledPartyHeader = "CALLED_PARTY";
    private String callingPartyHeader = "CALLING_PARTY";
    private String callStartHeader = "CALL_START";
    private String callStopHeader = "CALL_STOP";
    private String rateCardHeader = "RATECARD";
    private String rateCardGroupHeader = "RATECARD_GROUP";
    private String rateMinorUnitHeader = "RATE_MINOR_UNIT";
    private String serviceHeader = "SERVICE";
    private String callTypeHeader = "CALL_TYPE";
    private String tariffTypeHeader = "TARIFF_TYPE";
    private String eventAction = "EVENT_ACTION";
    private String currencyHeader = MsccCdrFields.CURRENCY.getHeader();
    private String exponentHeader = MsccCdrFields.EXPONENT.getHeader();
    private String previousUnAccountedVolume = MsccCdrFields.PREVIOUS_UNACCOUNTED_VOUME.getHeader();
    private String previousUnAccountedTime = MsccCdrFields.PREVIOUS_UNACCOUNTED_TIME.getHeader();
    private String packageNameHeader = MsccCdrFields.PACKAGE_NAME.getHeader();
    private String productOfferNameHeader = MsccCdrFields.PRODUCT_OFFER_NAME.getHeader();
    private String quotaProfileNameHeader = MsccCdrFields.QUOTA_PROFILE.getHeader();
    private String rateCardNameHeader = MsccCdrFields.RATE_CARD.getHeader();
    private String revenueCodeHeader = MsccCdrFields.REVENUE_CODE.getHeader();
    private String levelHeader = MsccCdrFields.LEVEL.getHeader();
    private String chargingOperationHeader = MsccCdrFields.OPERATION.getHeader();

    public CSVDriverConfigurationImpl(String driverInstanceId,
                                      String driverType,
                                      String driverName,
                                      boolean header,
                                      String delimiter,
                                      String fileName,
                                      String fileLocation,
                                      String defaultDirName,
                                      String dirName,
                                      String prefixFileName,
                                      String sequenceRange,
                                      String sequencePosition,
                                      boolean sequenceGlobalization,
                                      String allocatingProtocol,
                                      String address,
                                      int port,
                                      String remoteLocation,
                                      String userName,
                                      String password,
                                      String postOperation,
                                      String archiveLocation,
                                      int failOverTime,
                                      String reportingType,
                                      int timeBoundary,
                                      long timeBasedRollingUnit,
                                      long sizeBasedRollingUnit,
                                      long recordBasedRollingUnit,
                                      List<CSVFieldMapping> fieldMappings,
                                      Map<String, CSVStripMapping> stripMappings,
                                      EnumMap<RollingTypeConstant, Integer> rollingTypeMap,
                                      SimpleDateFormat cdrTimeStampFormat) {
        this.driverInstanceId = driverInstanceId;
        this.driverType = driverType;
        this.driverName = driverName;
        this.port = port;
        this.failOverTime = failOverTime;
        this.header = header;
        this.sequenceGlobalization = sequenceGlobalization;
        this.delimiter = delimiter;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
        this.prefixFileName = prefixFileName;
        this.defaultDirName = defaultDirName;
        this.dirName = dirName;
        this.sequenceRange = sequenceRange;
        this.sequencePosition = sequencePosition;
        this.allocatingProtocol = allocatingProtocol;
        this.address = address;
        this.remoteLocation = remoteLocation;
        this.userName = userName;
        this.password = password;
        this.postOperation = postOperation;
        this.archiveLocation = archiveLocation;
        this.reportingType = reportingType;
        this.timeBoundary = timeBoundary;
        this.timeBasedRollingUnit = timeBasedRollingUnit;
        this.sizeBasedRollingUnit = sizeBasedRollingUnit;
        this.recordBasedRollingUnit = recordBasedRollingUnit;
        this.fieldMappings = fieldMappings;
        this.stripMappings = stripMappings;
        this.rollingTypeMap = rollingTypeMap;
        this.cdrTimeStampFormat = cdrTimeStampFormat;
    }

    @Override
    public String getDriverInstanceId() {
        return driverInstanceId;
    }

    @Override
    public String getDriverType() {
        return driverType;
    }

    @Override
    public String getDriverName() {
        return driverName;
    }

    @Override
    public long getRollingUnit() {
        return rollingUnit;
    }

    @Override
    public int getRollingType() {
        return rollingType;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getFailOverTime() {
        return failOverTime;
    }

    @Override
    public boolean isHeader() {
        return header;
    }

    @Override
    public SimpleDateFormat getCDRTimeStampFormat() {
        return cdrTimeStampFormat;
    }

    @Override
    public boolean isSequenceGlobalization() {
        return sequenceGlobalization;
    }

    @Override
    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFileLocation() {
        return fileLocation;
    }

    @Override
    public String getPrefixFileName() {
        return prefixFileName;
    }

    @Override
    public String getDefaultFolderName() {
        return defaultDirName;
    }

    @Override
    public String getDirectoryName() {
        return dirName;
    }

    @Override
    public String getSequenceRange() {
        return sequenceRange;
    }

    @Override
    public String getSequencePosition() {
        return sequencePosition;
    }

    @Override
    public String getAllocatingProtocol() {
        return allocatingProtocol;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getRemoteLocation() {
        return remoteLocation;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPostOperation() {
        return postOperation;
    }

    @Override
    public String getArchiveLocation() {
        return archiveLocation;
    }

    @Override
    public String getReportingType() {
        return reportingType;
    }

    @Override
    public String getUsageKeyHeader() {
        return usageKeyHeader;
    }

    @Override
    public String getInputOctetsHeader() {
        return inputOctetsHeader;
    }

    @Override
    public String getOutputOctetsHeader() {
        return outputOctetsHeader;
    }

    @Override
    public String getTotalOctetsHeader() {
        return totalOctetsHeader;
    }

    @Override
    public String getUsageTimeHeader() {
        return usageTimeHeader;
    }

    @Override
    public String getRatingGroupHeader() {
        return ratingGroupHeader;
    }

    @Override
    public String getServiceIdentifierHeader() {
        return serviceIdentifierHeader;
    }

    @Override
    public List<CSVFieldMapping> getCDRFieldMappings() {
        return fieldMappings;
    }

    @Override
    public Map<String, CSVStripMapping> getStripMappings() {
        return stripMappings;
    }

    @Override
    public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
        return rollingTypeMap;
    }

    @Override
    public String getUnAccountedVolumeHeader() {
        return unAccountedVolumeHeader;
    }

    @Override
    public String getUnAccountedUsageTimeHeader() {
        return unAccountedTimeHeader;
    }

    @Override
    public String getAccountedNonMonetaryVolumeHeader() {
        return accountedNonMonetaryVolumeHeader;
    }

    @Override
    public String getAccountedNonMonetaryUsageTimeHeader() {
        return accountedNonMonetaryTimeHeader;
    }

    @Override
    public String getAccountedMonetaryHeader() {
        return accountedMonetaryHeader;
    }

    @Override
    public String getRateHeader() {
        return rateHeader;
    }

    @Override
    public String getDiscountHeader() { return discountHeader; }

    @Override
    public String getDiscountAmountHeader() { return discountAmountHeader; }

    @Override
    public String getReportedVolumeHeader() {
        return reportedVolumeHeader;
    }

    @Override
    public String getReportedUsageTimeHeader() {
        return reportedTimeHeader;
    }

    @Override
    public String getReportedEventHeader() {
        return reportedEventHeader;
    }

    @Override
    public String getCoreSessionIdHeader() {
        return coreSessionIdHeader;
    }

    @Override
    public String getVolumePulseHeader() {
        return volumePulseHeader;
    }

    @Override
    public String getTimePulseHeader() {
        return timePulseHeader;
    }

    @Override
    public String getCalculatedVolumePulseHeader() {
        return calculatedVolumePulseHeader;
    }

    @Override
    public String getCalculatedTimePulseHeader() {
        return calculatedTimePulseHeader;
    }

    @Override
    public String getCalledPartyHeader() {
        return calledPartyHeader;
    }

    @Override
    public String getCallingPartyHeader() {
        return callingPartyHeader;
    }

    @Override
    public String getCallStartHeader() {
        return callStartHeader;
    }

    @Override
    public String getCallStopHeader() {
        return callStopHeader;
    }

    @Override
    public String getRateCardHeader() {
        return rateCardHeader;
    }

    @Override
    public String getCurrencyHeader() {
        return currencyHeader;
    }

    @Override
    public String getExponentHeader() {
        return exponentHeader;
    }

    @Override
    public String getPreviousUnAccountedVolumeHeader() {
        return previousUnAccountedVolume;
    }

    @Override
    public String getPreviousUnAccountedTimeHeader() {
        return previousUnAccountedTime;
    }

    @Override
    public String getPackageNameHeader() {
        return packageNameHeader;
    }

    @Override
    public String getProductOfferNameHeader() {
        return productOfferNameHeader;
    }

    @Override
    public String getEventActionHeader() {
        return eventAction;
    }

    @Override
    public String getQuotaProfileNameHeader() {
        return quotaProfileNameHeader;
    }

    @Override
    public String getRateCardNameHeader() { return rateCardNameHeader; }

    @Override
    public String getChargingOperationHeader() {
        return chargingOperationHeader;
    }

    @Override
    public String getRateCardGroupHeader() {
        return rateCardGroupHeader;
    }

    @Override
    public String getRateMinorUnitHeader() {
        return rateMinorUnitHeader;
    }

    @Override
    public String getLevelHeader() {
        return levelHeader;
    }

    @Override
    public int getTimeBoundary() {
        return timeBoundary;
    }

    @Override
    public long getTimeBasedRollingUnit() {
        return timeBasedRollingUnit;
    }

    @Override
    public long getSizeBasedRollingUnit() {
        return sizeBasedRollingUnit;
    }

    @Override
    public long getRecordBasedRollingUnit() {
        return recordBasedRollingUnit;
    }

    @Override
    public String getServiceHeader(){
        return serviceHeader;
    }

    @Override
    public String getCallTypeHeader() { return callTypeHeader; }

    @Override
    public String getTariffTypeHeader() { return tariffTypeHeader; }

    public String getRevenueCodeHeader() {
        return revenueCodeHeader;
    }

    @Override
    public String toString() {

        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- CSV CDR Driver Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.incrementIndentation();
        builder.append("Driver Name", driverName);
        builder.append("Driver Id", driverInstanceId);
        builder.append("Driver Type", driverType);
        builder.append("Header", header);
        builder.append("Delimiter", delimiter);
        builder.append("CSV File Location", fileLocation);
        builder.append("File Name", fileName);
        builder.append("Prefix File Name", prefixFileName);
        builder.append("Default Directory Name", defaultDirName);
        builder.append("Rolling Unit", rollingUnit);
        builder.append("Rolling type Map", rollingTypeMap);
        builder.append("Port", port);
        builder.append("Allocating Protocol", allocatingProtocol);
        builder.append("Archive Location", archiveLocation);
        builder.append("FailOver Time", failOverTime);
        builder.append("Address", remoteLocation);
        builder.append("Post Operation", postOperation);
        builder.append("UserName", userName);
        builder.append("CDR Sequencing Range", sequenceRange);
        builder.append("Pattern", sequencePosition);
        builder.append("GlobaliZation", sequenceGlobalization);
        builder.append("Usage Key Header", usageKeyHeader);
        builder.append("Input Octets field", inputOctetsHeader);
        builder.append("Output Octets field", outputOctetsHeader);
        builder.append("Total Octets", totalOctetsHeader);
        builder.append("Usage Time field", usageTimeHeader);
        builder.append("Time Boundary", timeBoundary);
        builder.append("Time Based Rolling Unit", timeBasedRollingUnit);
        builder.append("Size Based Rolling Unit", sizeBasedRollingUnit);
        builder.append("Record Based Rolling Unit", recordBasedRollingUnit);
        builder.append("Time Stamp Format", cdrTimeStampFormat.toPattern());
        printCSVFiledMappings(builder, fieldMappings);
        printCSVStripMappings(builder, stripMappings.values());
        builder.decrementIndentation();
    }

    @Override
    public void reloadDriverConfiguration() throws LoadConfigurationException {
        //reload not supported

    }

    public void printCSVStripMappings(IndentingToStringBuilder builder, Collection<CSVStripMapping> csvStripMappings) {
        builder.appendField("Strip Mapping");
        builder.incrementIndentation();
        if (Collectionz.isNullOrEmpty(csvStripMappings) == false) {
            for (CSVStripMapping csvStripMapping : csvStripMappings) {
                printStripMapping(builder, csvStripMapping);
            }
        } else {
            builder.appendValue(null); //will be printed based on setNullText
        }
        builder.decrementIndentation();
    }

    public void printStripMapping(IndentingToStringBuilder builder, CSVStripMapping csvStripMapping) {

        builder.incrementIndentation();
        builder.append("Key", csvStripMapping.getKey());
        builder.append("Pattern", csvStripMapping.getPattern());
        builder.append("Seperator", csvStripMapping.getSeperator());
        builder.decrementIndentation();
    }

    public void printCSVFiledMappings(IndentingToStringBuilder builder, List<CSVFieldMapping> csvFieldMappings) {
        builder.appendField("Field Mapping");
        builder.incrementIndentation();
        if (Collectionz.isNullOrEmpty(csvFieldMappings) == false) {
            for (CSVFieldMapping csvFieldMapping : csvFieldMappings) {
                printFieldMapping(builder, csvFieldMapping);
            }
        } else {
            builder.appendValue(null); //will be printed based on setNullText
        }
        builder.decrementIndentation();
    }

    public void printFieldMapping(IndentingToStringBuilder builder, CSVFieldMapping csvFieldMapping) {

        builder.incrementIndentation();
        builder.append("Header Field", csvFieldMapping.getHeaderField());
        builder.append("Key", csvFieldMapping.getKey());
        builder.decrementIndentation();
    }

    public static class CSVDriverConfigurationBuilder {
        private String driverInstanceId;
        private String driverType;
        private int port;
        private int failOverTime = 3;

        private boolean header = true;
        private boolean sequenceGlobalization = false;

        private String driverName;
        private String delimiter = ",";
        private String fileName = "Netvertex-CDR.csv";
        private String fileLocation = "data/csvfiles";
        private String prefixFileName;
        private String defaultDirName;
        private String dirName;
        private String sequenceRange;
        private String sequencePosition;
        private String allocatingProtocol = "Local";
        private String address = "0.0.0.0";
        private String remoteLocation;
        private String userName;
        private String password;
        private String postOperation = "Delete";
        private String archiveLocation;
        private SimpleDateFormat cdrTimeStampFormat;

        private String reportingType;
        private int timeBoundary;
        private int timeBasedRollingUnit;
        private int sizeBasedRollingUnit;
        private int recordBasedRollingUnit;

        private List<CSVFieldMapping> fieldMappings;
        private Map<String, CSVStripMapping> stripMappings;
        private EnumMap<RollingTypeConstant, Integer> rollingTypeMap;

        public CSVDriverConfigurationBuilder(String driverInstanceId, String driverName) {
            this.driverInstanceId = driverInstanceId;
            this.driverName = driverName;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withDriverInstanceId(String driverInstanceId) {
            this.driverInstanceId = driverInstanceId;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withCSVDriverType() {
            this.driverType = DriverType.CSV_DRIVER.name();
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withDBCDRDriverType() {
            this.driverType = DriverType.DB_CDR_DRIVER.name();
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withPort(int port){
            this.port = port;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withPrefixFileName(String prefixFileName){
            this.prefixFileName = prefixFileName;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withDefaultDirName(String defaultDirName){
            this.defaultDirName = defaultDirName;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withDirName(String dirName){
            this.dirName = dirName;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withSequenceRange(String sequenceRange){
            this.sequenceRange = sequenceRange;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withSequencePosition(String sequencePosition){
            this.sequencePosition = sequencePosition;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withRemoteLocation(String remoteLocation){
            this.remoteLocation = remoteLocation;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withUserName(String userName){
            this.userName = userName;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withPassword(String password){
            this.password = password;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withArchiveLocation(String archiveLocation){
            this.archiveLocation = archiveLocation;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withCDRTimeStampFormat(SimpleDateFormat cdrTimeStampFormat){
            this.cdrTimeStampFormat = cdrTimeStampFormat;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withReportingType(String reportingType){
            this.reportingType = reportingType;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withTimeBoundary(int timeBoundary){
            this.timeBoundary = timeBoundary;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withTimeBasedRollingUnit(int timeBasedRollingUnit){
            this.timeBasedRollingUnit = timeBasedRollingUnit;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withSizeBasedRollingUnit(int sizeBasedRollingUnit){
            this.sizeBasedRollingUnit = sizeBasedRollingUnit;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withRecordBasedRollingUnit(int recordBasedRollingUnit){
            this.recordBasedRollingUnit = recordBasedRollingUnit;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withFieldMappings(List<CSVFieldMapping> fieldMappings){
            this.fieldMappings = fieldMappings;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withStripMappings(Map<String, CSVStripMapping> stripMappings){
            this.stripMappings = stripMappings;
            return this;
        }

        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withRollingTypeMap(EnumMap<RollingTypeConstant, Integer> rollingTypeMap){
            this.rollingTypeMap = rollingTypeMap;
            return this;
        }
        public CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder withDelimiter(String delimiter){
            this.delimiter = delimiter;
            return this;
        }


        public CSVDriverConfigurationImpl build() {
            return new CSVDriverConfigurationImpl(this.driverInstanceId,
                    this.driverType,
                    this.driverName,
                    this.header,
                    this.delimiter,
                    this.fileName,
                    this.fileLocation,
                    this.defaultDirName,
                    this.dirName,
                    this.prefixFileName,
                    this.sequenceRange,
                    this.sequencePosition,
                    this.sequenceGlobalization,
                    this.allocatingProtocol,
                    this.address,
                    this.port,
                    this.remoteLocation,
                    this.userName,
                    this.password,
                    this.postOperation,
                    this.archiveLocation,
                    this.failOverTime,
                    this.reportingType,
                    this.timeBoundary,
                    this.timeBasedRollingUnit,
                    this.sizeBasedRollingUnit,
                    this.recordBasedRollingUnit,
                    this.fieldMappings,
                    this.stripMappings,
                    this.rollingTypeMap,
                    this.cdrTimeStampFormat
            );
        }
    }
}
