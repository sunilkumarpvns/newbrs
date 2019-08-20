package com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form;

import java.util.Set;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CSVDriverForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	
	
	private Long csvDriverId;	
	private Long driverInstanceId;
	private String allocatingProtocol;
	private String address;
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation;
	private String archiveLocation;
	private Long failOvertime;
	private String fileName;
	private String location;
	private String defaultDirName;
	private Long fileRollingType;
	private String createBlankFile;
	private Long rollingUnit;
	private Long rollingUnitOther;
	private String eventDateFormat;
	private String prefixFileName;
	private String folderName;	
	private String useDictionaryValue;
	private String avpairSeparator;
	private String cdrTimestampFormat;
	private String header;
	private String delimiter;
	private String range;
	private String sequencePosition;
	private String globalization;
	private Set<CSVFieldMapData> csvFieldMapSet;
	private int patterncount;
	private int feildmapcount;
	private String pattern="suffix";
	private String reportingType;
	private String inputOctetsHeader;
	private String outputOctetsHeader;
	private String totalOctetsHeader;
	private String usageTimeHeader;
	private String usageKeyHeader;
	private Set<CSVStripFieldMapData> csvStripFieldMapDataSet;
	
	public String getUsageKeyHeader() {
		return usageKeyHeader;
	}
	public void setUsageKeyHeader(String usageKeyHeader) {
		this.usageKeyHeader = usageKeyHeader;
	}
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
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
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
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDefaultDirName() {
		return defaultDirName;
	}
	public void setDefaultDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}
	public Long getFileRollingType() {
		return fileRollingType;
	}
	public void setFileRollingType(Long fileRollingType) {
		this.fileRollingType = fileRollingType;
	}
	public String getCreateBlankFile() {
		return createBlankFile;
	}
	public void setCreateBlankFile(String createBlankFile) {
		this.createBlankFile = createBlankFile;
	}
	public Long getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	public String getEventDateFormat() {
		return eventDateFormat;
	}
	public void setEventDateFormat(String eventDateFormat) {
		this.eventDateFormat = eventDateFormat;
	}
	public String getPrefixFileName() {
		return prefixFileName;
	}
	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getUseDictionaryValue() {
		return useDictionaryValue;
	}
	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}
	public String getAvpairSeparator() {
		return avpairSeparator;
	}
	public void setAvpairSeparator(String avpairSeparator) {
		this.avpairSeparator = avpairSeparator;
	}
	public String getCdrTimestampFormat() {
		return cdrTimestampFormat;
	}
	public void setCdrTimestampFormat(String cdrTimestampFormat) {
		this.cdrTimestampFormat = cdrTimestampFormat;
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
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getSequencePosition() {
		return sequencePosition;
	}
	public void setSequencePosition(String sequencePosition) {
		this.sequencePosition = sequencePosition;
	}
	public String getGlobalization() {
		return globalization;
	}
	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}
	
	public Set<CSVFieldMapData> getCsvFieldMapSet() {
		return csvFieldMapSet;
	}
	public void setCsvFieldMapSet(Set<CSVFieldMapData> csvFieldMapSet) {
		this.csvFieldMapSet = csvFieldMapSet;
	}
	public int getPatterncount() {
		return patterncount;
	}
	public void setPatterncount(int patterncount) {
		this.patterncount = patterncount;
	}
	public int getFeildmapcount() {
		return feildmapcount;
	}
	public void setFeildmapcount(int feildmapcount) {
		this.feildmapcount = feildmapcount;
	}
	public Set<CSVStripFieldMapData> getCsvStripFieldMapDataSet() {
		return csvStripFieldMapDataSet;
	}
	public void setCsvStripFieldMapDataSet(Set<CSVStripFieldMapData> csvStripFieldMapDataSet) {
		this.csvStripFieldMapDataSet = csvStripFieldMapDataSet;
	}
	public Long getRollingUnitOther() {
		return rollingUnitOther;
	}
	public void setRollingUnitOther(Long rollingUnitOther) {
		this.rollingUnitOther = rollingUnitOther;
	}
	

}
