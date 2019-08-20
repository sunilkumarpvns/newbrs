package com.elitecore.corenetvertex.sm.driver.csv;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Contains CSV Driver related information for Driver Management
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData")
@Table(name = "TBLM_CSV_DRIVER")
public class CsvDriverData implements Serializable {

    private String id;
    private String header;
    private String delimiter;
    private String timeStampFormat;
    private String fileName;
    private String fileLocation;
    private String prefixFileName;
    private String defaultFolderName;
    private String folderName;
    private String sequenceRange;
    private String sequencePosition;
    private String sequenceGlobalization;
    private String allocatingProtocol;
    private String address;
    private String remoteFileLocation;
    private String userName;
    private String password;
    private transient String postOperation;
    private String archiveLocation;
    private Integer failOverTime;
    private String reportingType;
    private Integer timeBoundary;
    private Long sizeBasedRollingUnit;
    private Long timeBasedRollingUnit;
    private Long recordBasedRollingUnit;
    private List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList;
    private List<CsvDriverStripMappingData> csvDriverStripMappingDataList;
    private transient DriverData driverData;

    public CsvDriverData() {
        csvDriverFieldMappingDataList = Collectionz.newArrayList();
        csvDriverStripMappingDataList = Collectionz.newArrayList();
    }

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "HEADER")
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(name = "DELIMITER")
    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Column(name = "TIMESTAMP_FORMAT")
    public String getTimeStampFormat() {
        return timeStampFormat;
    }

    public void setTimeStampFormat(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    @Column(name = "FILE_NAME")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "FILE_LOCATION")
    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Column(name = "PREFIX_FILE_NAME")
    public String getPrefixFileName() {
        return prefixFileName;
    }

    public void setPrefixFileName(String prefixFileName) {
        this.prefixFileName = prefixFileName;
    }

    @Column(name = "DEFAULT_FOLDER_NAME")
    public String getDefaultFolderName() {
        return defaultFolderName;
    }

    public void setDefaultFolderName(String defaultFolderName) {
        this.defaultFolderName = defaultFolderName;
    }

    @Column(name = "FOLDER_NAME")
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Column(name = "SEQUENCE_RANGE")
    public String getSequenceRange() {
        return sequenceRange;
    }

    public void setSequenceRange(String sequenceRange) {
        this.sequenceRange = sequenceRange;
    }

    @Column(name = "SEQUENCE_POSITION")
    public String getSequencePosition() {
        return sequencePosition;
    }

    public void setSequencePosition(String sequencePosition) {
        this.sequencePosition = sequencePosition;
    }

    @Column(name = "SEQUENCE_GLOBALIZATION")
    public String getSequenceGlobalization() {
        return sequenceGlobalization;
    }

    public void setSequenceGlobalization(String sequenceGlobalization) {
        this.sequenceGlobalization = sequenceGlobalization;
    }

    @Column(name = "ALLOCATING_PROTOCOL")
    public String getAllocatingProtocol() {
        return allocatingProtocol;
    }

    public void setAllocatingProtocol(String allocatingProtocol) {
        this.allocatingProtocol = allocatingProtocol;
    }

    @Column(name = "ADDRESS")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "REMOTE_FILE_LOCATION")
    public String getRemoteFileLocation() {
        return remoteFileLocation;
    }

    public void setRemoteFileLocation(String remoteFileLocation) {
        this.remoteFileLocation = remoteFileLocation;
    }

    @Column(name = "USERNAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "POST_OPERATION")
    public String getPostOperation() {
        return postOperation;
    }

    public void setPostOperation(String postOperation) {
        this.postOperation = postOperation;
    }

    @Column(name = "ARCHIVE_LOCATION")
    public String getArchiveLocation() {
        return archiveLocation;
    }

    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }

    @Column(name = "FAILOVER_TIME")
    public Integer getFailOverTime() {
        return failOverTime;
    }

    public void setFailOverTime(Integer failOverTime) {
        this.failOverTime = failOverTime;
    }

    @Column(name = "REPORTING_TYPE")
    public String getReportingType() {
        return reportingType;
    }

    public void setReportingType(String reportingType) {
        this.reportingType = reportingType;
    }
	
    @Column(name = "TIME_BOUNDARY")
    public Integer getTimeBoundary() {
        return timeBoundary;
    }

    public void setTimeBoundary(Integer timeBoundary) {
        this.timeBoundary = timeBoundary;
    }

    @Column(name = "SIZE_BASED_ROLLING_UNIT")
    public Long getSizeBasedRollingUnit() {
        return sizeBasedRollingUnit;
    }

    public void setSizeBasedRollingUnit(Long sizeBasedRollingUnit) {
        this.sizeBasedRollingUnit = sizeBasedRollingUnit;
    }

    @Column(name = "TIME_BASED_ROLLING_UNIT")
    public Long getTimeBasedRollingUnit() {
        return timeBasedRollingUnit;
    }

    public void setTimeBasedRollingUnit(Long timeBasedRollingUnit) {
        this.timeBasedRollingUnit = timeBasedRollingUnit;
    }

    @Column(name = "RECORD_BASED_ROLLING_UNIT")
    public Long getRecordBasedRollingUnit() {
        return recordBasedRollingUnit;
    }

    public void setRecordBasedRollingUnit(Long recordBasedRollingUnit) {
        this.recordBasedRollingUnit = recordBasedRollingUnit;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "CSV_DRIVER_ID")
    @OrderBy(clause="ORDER_NO")
    public List<CsvDriverFieldMappingData> getCsvDriverFieldMappingDataList() {
        return csvDriverFieldMappingDataList;
    }

    public void setCsvDriverFieldMappingDataList(List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList) {
        this.csvDriverFieldMappingDataList = csvDriverFieldMappingDataList;
    }

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "CSV_DRIVER_ID")
    public List<CsvDriverStripMappingData> getCsvDriverStripMappingDataList() {
        return csvDriverStripMappingDataList;
    }

    public void setCsvDriverStripMappingDataList(List<CsvDriverStripMappingData> csvDriverStripMappingDataList) {
        this.csvDriverStripMappingDataList = csvDriverStripMappingDataList;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "DRIVER_ID",updatable = false)
    public DriverData getDriverData() {
        return driverData;
    }

    public void setDriverData(DriverData driverData) {
        this.driverData = driverData;
    }

    public JsonObject toJson(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Header",header);
        jsonObject.addProperty("Delimiter",delimiter);
        jsonObject.addProperty("Time Stamp Format",timeStampFormat);
        jsonObject.addProperty("File Name",fileName);
        jsonObject.addProperty("File Location",fileLocation);
        jsonObject.addProperty("Prefix File Name",prefixFileName);
        jsonObject.addProperty("Default Folder Name",defaultFolderName);
        jsonObject.addProperty("Folder Name",folderName);
        jsonObject.addProperty("Sequence Range",sequenceRange);
        jsonObject.addProperty("Sequence Position",sequencePosition);
        jsonObject.addProperty("Sequence Globalization",sequenceGlobalization);
        jsonObject.addProperty("Allocating Protocol",allocatingProtocol);
        jsonObject.addProperty("Remote File Location",remoteFileLocation);
        jsonObject.addProperty("User Name",userName);
        jsonObject.addProperty("Post Operation",postOperation);
        jsonObject.addProperty("Archive Location",archiveLocation);
        jsonObject.addProperty("Fail Over Time",failOverTime);
        jsonObject.addProperty("Reporting Type",reportingType);
        jsonObject.addProperty("Time Boundary",timeBoundary);
        jsonObject.addProperty("Size Based Rolling Unit",sizeBasedRollingUnit);
        jsonObject.addProperty("Time Based Rolling Unit",timeBasedRollingUnit);
        jsonObject.addProperty("Record Based Rolling Unit",recordBasedRollingUnit);


        if(csvDriverFieldMappingDataList != null){
            JsonObject jsonObjectFieldMapping = new JsonObject();
            for(CsvDriverFieldMappingData csvDriverFieldMappingData : csvDriverFieldMappingDataList){
                if(csvDriverFieldMappingData != null)
                    jsonObjectFieldMapping.add(csvDriverFieldMappingData.getPcrfKey(),csvDriverFieldMappingData.toJson());
            }
            jsonObject.add("Field Mappings", jsonObjectFieldMapping);
        }

        if(csvDriverStripMappingDataList != null){
            JsonObject jsonObjectFieldMapping = new JsonObject();
            for(CsvDriverStripMappingData csvDriverStripMappingData : csvDriverStripMappingDataList){
                if(csvDriverStripMappingData != null) {
                    jsonObjectFieldMapping.add(csvDriverStripMappingData.getPcrfKey(), csvDriverStripMappingData.toJson());
                }
            }
            jsonObject.add("Strip Attributes", jsonObjectFieldMapping);
        }
        return jsonObject;

    }

}
