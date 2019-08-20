package com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data;

import java.io.Serializable;
import java.util.Set;

public class CSVDriverData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long csvDriverId;	
	private Long driverInstanceId;
	private String header;
	private String delimiter;
	private String cdrTimestampFormat;
	
	private String fileName;
	private String fileLocation;
	private String prefixFileName;
	private String defaultDirName;
	private String folderName;	
	private Long fileRollingType;
	private Long rollingUnit;

	private String range;
	private String globalization;
	private String sequencePosition;
	
	private String allocatingProtocol;
	private String address;
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation;
	private String archiveLocation;
	private Long failOvertime;
	private String reportingType;
	private String inputOctetsHeader;
	private String outputOctetsHeader;
	private String totalOctetsHeader;
	private String usageTimeHeader;
	private String usageKeyHeader;
	private Set<CSVFieldMapData> csvFieldMapSet;
	private Set<CSVStripFieldMapData> csvStripFieldMapDataSet;
	
	public String getReportingType() {
		return reportingType;
	}

	public void setReportingType(String reportingType) {
		this.reportingType = reportingType;
	}

	public String getInputOctetsHeader() {
		return inputOctetsHeader;
	}

	public void setInputOctetsHeader(String inputOctetsHeader) {
		this.inputOctetsHeader = inputOctetsHeader;
	}

	public String getOutputOctetsHeader() {
		return outputOctetsHeader;
	}

	public void setOutputOctetsHeader(String outputOctetsHeader) {
		this.outputOctetsHeader = outputOctetsHeader;
	}

	public String getTotalOctetsHeader() {
		return totalOctetsHeader;
	}

	public void setTotalOctetsHeader(String totalOctetsHeader) {
		this.totalOctetsHeader = totalOctetsHeader;
	}

	public String getUsageTimeHeader() {
		return usageTimeHeader;
	}

	public void setUsageTimeHeader(String usageTimeHeader) {
		this.usageTimeHeader = usageTimeHeader;
	}
	
	public String getUsageKeyHeader() {
		return usageKeyHeader;
	}

	public void setUsageKeyHeader(String usageKeyHeader) {
		this.usageKeyHeader = usageKeyHeader;
	}

	public Set<CSVStripFieldMapData> getCsvStripFieldMapDataSet() {
		return csvStripFieldMapDataSet;
	}

	public void setCsvStripFieldMapDataSet(
			Set<CSVStripFieldMapData> csvStripFieldMapDataSet) {
		this.csvStripFieldMapDataSet = csvStripFieldMapDataSet;
	}

	public Long getCsvDriverId() {
		return csvDriverId;
	}

	public void setCsvDriverId(Long csvDriverId) {
		this.csvDriverId = csvDriverId;
	}

	public Long getDriverInstanceId() {
		return driverInstanceId;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getPrefixFileName() {
		return prefixFileName;
	}

	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}

	public String getDefaultDirName() {
		return defaultDirName;
	}

	public void setDefaultDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Long getFileRollingType() {
		return fileRollingType;
	}

	public void setFileRollingType(Long fileRollingType) {
		this.fileRollingType = fileRollingType;
	}

	public Long getRollingUnit() {
		return rollingUnit;
	}

	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getGlobalization() {
		return globalization;
	}

	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}

	public String getSequencePosition() {
		return sequencePosition;
	}

	public void setSequencePosition(String sequencePosition) {
		this.sequencePosition = sequencePosition;
	}

	public String getAllocatingProtocol() {
		return allocatingProtocol;
	}

	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemoteLocation() {
		return remoteLocation;
	}

	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPostOperation() {
		return postOperation;
	}

	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}

	public String getArchiveLocation() {
		return archiveLocation;
	}

	public void setArchiveLocation(String archiveLocation) {
		this.archiveLocation = archiveLocation;
	}

	public Long getFailOvertime() {
		return failOvertime;
	}

	public void setFailOvertime(Long failOvertime) {
		this.failOvertime = failOvertime;
	}

	
	
	public String getCdrTimestampFormat() {
		return cdrTimestampFormat;
	}

	public void setCdrTimestampFormat(String cdrTimestampFormat) {
		this.cdrTimestampFormat = cdrTimestampFormat;
	}

	public Set<CSVFieldMapData> getCsvFieldMapSet() {
		return csvFieldMapSet;
	}

	public void setCsvFieldMapSet(Set<CSVFieldMapData> csvFieldMapSet) {
		this.csvFieldMapSet = csvFieldMapSet;
	}

	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
}