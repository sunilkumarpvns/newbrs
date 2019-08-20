package com.elitecore.elitesm.web.driver.diameter.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDiameterDetailLocalAcctDriverForm  extends BaseWebForm{
	
	private long detailLocalId;
	private long driverInstanceId;
	private String allocatingProtocol;
	private String ipaddress;
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation;
	private String archiveLocation="data/detail-local-files/archive";
	private long failOverTime = 3;
	private String fileName="detail.local";
	private String location="data/detail-local-files";
	private String defaultDirName="no_nas_ip_address";
	private String eventDateFormat="EEE dd MMM yyyy hh:mm:ss aaa";
	private String prefixFileName="0:4";
	private String folderName="0:4";
	private String writeAttributes;
	private String useDictionaryValue;
	private String avpairSeperator="=";
	private String range;
	private String pattern;
	private String globalization;	
	private String action;
	private int itemIndex;
	
	// driver instance related
	
	private String driverInstanceName;
	private String driverInstanceDesp;
	private String driverRelatedId;
	
	// attribute relational mapping related
	private String attrIds = "0:1";
	private String defaultValue;
	private String useDictionaryValueForRel;
	
	//File Rolling Parameters
	private Long timeBoundry=1440L;
	private Long sizeBasedRollingUnit;
	private Long timeBasedRollingUnit;
	private Long recordBasedRollingUnit;

	public long getDetailLocalId() {
		return detailLocalId;
	}
	public void setDetailLocalId(long detailLocalId) {
		this.detailLocalId = detailLocalId;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getAllocatingProtocol() {
		return allocatingProtocol;
	}
	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	public String getRemoteLocation() {
		return remoteLocation;
	}
	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public long getFailOverTime() {
		return failOverTime;
	}
	public void setFailOverTime(long failOverTime) {
		this.failOverTime = failOverTime;
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
	public String getWriteAttributes() {
		return writeAttributes;
	}
	public void setWriteAttributes(String writeAttributes) {
		this.writeAttributes = writeAttributes;
	}
	public String getUseDictionaryValue() {
		return useDictionaryValue;
	}
	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}
	public String getAvpairSeperator() {
		return avpairSeperator;
	}
	public void setAvpairSeperator(String avpairSeperator) {
		this.avpairSeperator = avpairSeperator;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getGlobalization() {
		return globalization;
	}
	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverInstanceDesp() {
		return driverInstanceDesp;
	}
	public void setDriverInstanceDesp(String driverInstanceDesp) {
		this.driverInstanceDesp = driverInstanceDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}	
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public String getAttrIds() {
		return attrIds;
	}
	public void setAttrIds(String attrIds) {
		this.attrIds = attrIds;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getUseDictionaryValueForRel() {
		return useDictionaryValueForRel;
	}
	public void setUseDictionaryValueForRel(String useDictionaryValueForRel) {
		this.useDictionaryValueForRel = useDictionaryValueForRel;
	}
	public Long getTimeBoundry() {
		return timeBoundry;
	}
	public void setTimeBoundry(Long timeBoundry) {
		this.timeBoundry = timeBoundry;
	}
	public Long getSizeBasedRollingUnit() {
		return sizeBasedRollingUnit;
	}
	public void setSizeBasedRollingUnit(Long sizeBasedRollingUnit) {
		this.sizeBasedRollingUnit = sizeBasedRollingUnit;
	}
	public Long getTimeBasedRollingUnit() {
		return timeBasedRollingUnit;
	}
	public void setTimeBasedRollingUnit(Long timeBasedRollingUnit) {
		this.timeBasedRollingUnit = timeBasedRollingUnit;
	}
	public Long getRecordBasedRollingUnit() {
		return recordBasedRollingUnit;
	}
	public void setRecordBasedRollingUnit(Long recordBasedRollingUnit) {
		this.recordBasedRollingUnit = recordBasedRollingUnit;
	}
}
