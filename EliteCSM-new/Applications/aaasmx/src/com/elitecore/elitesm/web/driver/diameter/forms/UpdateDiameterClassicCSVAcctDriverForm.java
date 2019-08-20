package com.elitecore.elitesm.web.driver.diameter.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDiameterClassicCSVAcctDriverForm extends BaseWebForm{
	
	private String classicCsvId;	
	private String driverInstanceId;
	private String allocatingprotocol;
	private String ipaddress;
	private String remotelocation;
	private String username;
	private String password;
	private String postoperation;
	private String archivelocation;
	private Long failovertime;
	private String filename;
	private String location;
	private String defaultdirname;
	private String createBlankFile;
	private String eventdateformat;
	private String prefixfilename;
	private String foldername;
	//private String writeattributes;
	private String usedictionaryvalue;
	private String avpairseparator;
	private String cdrtimestampFormat;
	private String header;
	private String delimeter;
	private String multivaluedelimeter;
	private String range;
	private String pattern;
	private String globalization;
	private String action;
	private String enclosingCharacter;
	
	private String driverRelatedId;
	private String driverInstanceName;
	private String driverInstanceDesp;
	
	private Long timeBoundry;
	private Long sizeBasedRollingUnit;
	private Long timeBasedRollingUnit;
	private Long recordBasedRollingUnit;
	private String auditUId;
	
	private String cdrTimestampPosition ;
	private String cdrTimestampHeader;
	
	public String getClassicCsvId() {
		return classicCsvId;
	}
	public void setClassicCsvId(String classicCsvId) {
		this.classicCsvId = classicCsvId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getAllocatingprotocol() {
		return allocatingprotocol;
	}
	public void setAllocatingprotocol(String allocatingprotocol) {
		this.allocatingprotocol = allocatingprotocol;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getEnclosingCharacter() {
		return enclosingCharacter;
	}
	public void setEnclosingCharacter(String enclosingCharacter) {
		this.enclosingCharacter = enclosingCharacter;
	}
	public String getRemotelocation() {
		return remotelocation;
	}
	public void setRemotelocation(String remotelocation) {
		this.remotelocation = remotelocation;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPostoperation() {
		return postoperation;
	}
	public void setPostoperation(String postoperation) {
		this.postoperation = postoperation;
	}
	public String getArchivelocation() {
		return archivelocation;
	}
	public void setArchivelocation(String archivelocation) {
		this.archivelocation = archivelocation;
	}
	public Long getFailovertime() {
		return failovertime;
	}
	public void setFailovertime(Long failovertime) {
		this.failovertime = failovertime;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDefaultdirname() {
		return defaultdirname;
	}
	public void setDefaultdirname(String defaultdirname) {
		this.defaultdirname = defaultdirname;
	}
	public String getCreateBlankFile() {
		return createBlankFile;
	}
	public void setCreateBlankFile(String createBlankFile) {
		this.createBlankFile = createBlankFile;
	}
	public String getEventdateformat() {
		return eventdateformat;
	}
	public void setEventdateformat(String eventdateformat) {
		this.eventdateformat = eventdateformat;
	}
	public String getPrefixfilename() {
		return prefixfilename;
	}
	public void setPrefixfilename(String prefixfilename) {
		this.prefixfilename = prefixfilename;
	}
	public String getFoldername() {
		return foldername;
	}
	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}	
	public String getUsedictionaryvalue() {
		return usedictionaryvalue;
	}
	public void setUsedictionaryvalue(String usedictionaryvalue) {
		this.usedictionaryvalue = usedictionaryvalue;
	}
	public String getAvpairseparator() {
		return avpairseparator;
	}
	public void setAvpairseparator(String avpairseparator) {
		this.avpairseparator = avpairseparator;
	}	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getDelimeter() {
		return delimeter;
	}
	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	public String getMultivaluedelimeter() {
		return multivaluedelimeter;
	}
	public void setMultivaluedelimeter(String multivaluedelimeter) {
		this.multivaluedelimeter = multivaluedelimeter;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
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
	public String getCdrtimestampFormat() {
		return cdrtimestampFormat;
	}
	public void setCdrtimestampFormat(String cdrtimestampFormat) {
		this.cdrtimestampFormat = cdrtimestampFormat;
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
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getCdrTimestampPosition() {
		return cdrTimestampPosition;
	}
	public void setCdrTimestampPosition(String cdrTimestampPosition) {
		this.cdrTimestampPosition = cdrTimestampPosition;
	}
	public String getCdrTimestampHeader() {
		return cdrTimestampHeader;
	}
	public void setCdrTimestampHeader(String cdrTimestampHeader) {
		this.cdrTimestampHeader = cdrTimestampHeader;
	}
	
}
