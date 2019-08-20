package com.elitecore.elitesm.web.driver.diameter.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAttrRelationData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDiameterClassicCSVAcctDriverForm extends BaseWebForm{
	
	private Long classicCsvId;	
	private long driverInstanceId;
	private String allocatingprotocol="NONE";
	private String ipaddress;
	private String remotelocation;
	private String username;
	private String password;
	private String postoperation="rename";
	private String archivelocation="data/csvfiles/archive";
	private Long failovertime=3L;
	private String filename="CDRs.csv";
	private String location="data/csvfiles";
	private String defaultdirname="no_nas_ip_address";
	private String createBlankFile="false";
	private Long timeBoundry=1440L;
	private Long sizeBasedRollingUnit;
	private Long timeBasedRollingUnit;
	private Long recordBasedRollingUnit;
	private String eventdateformat;
	private String prefixfilename;
	private String foldername="0:4";	
	private String usedictionaryvalue="false";
	private String avpairseparator="=";
	private String cdrtimestampFormat="EEE dd MMM yyyy hh:mm:ss aaa";
	private String header;
	private String delimeter=",";
	private String multivaluedelimeter=";";
	private String range;
	private String pattern="suffix";
	private String globalization="false";
	private Set csvAttrRelSet;
	private Set csvPattRelSet;
	private int patterncount;
	private int feildmapcount;
	private String enclosingCharacter;
	// driver instance related properties
	
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private String action;
	private int itemIndex;
	private String driverInstanceDesp;
	
	private String cdrTimestampPosition = "SUFFIX";
	private String cdrTimestampHeader = "CDRTimeStamp";
	
	public Long getClassicCsvId() {
		return classicCsvId;
	}
	public void setClassicCsvId(Long classicCsvId) {
		this.classicCsvId = classicCsvId;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
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
	public String getEnclosingCharacter() {
		return enclosingCharacter;
	}
	public void setEnclosingCharacter(String enclosingCharacter) {
		this.enclosingCharacter = enclosingCharacter;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
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
	public Set getCsvAttrRelSet() {
		return csvAttrRelSet;
	}
	public void setCsvAttrRelSet(Set csvAttrRelSet) {
		this.csvAttrRelSet = csvAttrRelSet;
	}
	public Set getCsvPattRelSet() {
		return csvPattRelSet;
	}
	public void setCsvPattRelSet(Set csvPattRelSet) {
		this.csvPattRelSet = csvPattRelSet;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
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
	public String getDriverInstanceDesp() {
		return driverInstanceDesp;
	}
	public void setDriverInstanceDesp(String driverInstanceDesp) {
		this.driverInstanceDesp = driverInstanceDesp;
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
	public String getCdrtimestampFormat() {
		return cdrtimestampFormat;
	}
	public void setCdrtimestampFormat(String cdrtimestampFormat) {
		this.cdrtimestampFormat = cdrtimestampFormat;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<ClassicCSVAttrRelationData> getDefaultmapping(){
		List<ClassicCSVAttrRelationData> defaultMappingList = new ArrayList<ClassicCSVAttrRelationData>();
		String[] headerList = {"UserName","Calling-Station-ID","NAS-IP-Address","NAS-Identifier","NAS-Port","NAS-Port-ID"
				,"Acct-Session-ID","Acct-Multi-Session-ID","Acct-Status-Type","Acct-Session-Time ","Acct-Input-Octets"
				,"Acct-Output-Octets","Acct-Input-Gigawards","Acct-Output-Gigawords","Acct-Input-Packets","Acct-Output-Packets"
				,"Acct-Delay-Time","Event-Timestamp"};		
		String[] attridList = {"0:1","0:31","0:4","0:32","0:5","0:87","0:44","0:50","0:40","0:46","0:42","0:43","0:52","0:53","0:47","0:48"
				,"0:41","0:55"};
		String [] useDicValList = new String[18];
		for(int i=0;i<18;i++){
			useDicValList[i] = "false";
		}
		for(int index = 0 ; index < headerList.length ; index++){
			ClassicCSVAttrRelationData classicCSVAttrRelationData = new ClassicCSVAttrRelationData();
			classicCSVAttrRelationData.setHeader(headerList[index]);
			classicCSVAttrRelationData.setAttributeids(attridList[index]);
			classicCSVAttrRelationData.setUsedictionaryvalue(useDicValList[index]);
			defaultMappingList.add(classicCSVAttrRelationData);
		}
		return defaultMappingList;
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
