package com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailLocalAcctDriverData extends BaseData implements IDetailLocalAcctDriver,Differentiable{
	
	private String detailLocalId;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Event Date Format")
	private String eventDateFormat;
	
	@Expose
	@SerializedName("Write Attributes")
	private String writeAttributes;
	
	@Expose
	@SerializedName("Use Dictionary Value")
	private String useDictionaryValue;
	
	@Expose
	@SerializedName("AVPair Separator")
	private String avpairSeperator;
	
	@Expose
	@SerializedName("File Name")
	private String fileName;
	
	@Expose
	@SerializedName("Location")
	private String location;
	
	@Expose
	@SerializedName("Default Folder Name")
	private String defaultDirName;
	
	@Expose
	@SerializedName("Prefix File Name")
	private String prefixFileName;
	
	@Expose
	@SerializedName("Folder Name")
	private String folderName;
	
	private String allocatingProtocol;
	private String ipaddress;
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation;
	private String archiveLocation;
	private long failOverTime;
	private String range;
	private String pattern;
	private String globalization;
	private Set<DetailLocalAttrRelationData> detailLocalSet;
	private List<DetailLocalAttrRelationData> mappingList;
	private Set mappingDataSet;
	private Long timeBoundry;
	private Long sizeBasedRollingUnit;
	private Long timeBasedRollingUnit;
	private Long recordBasedRollingUnit;
	
	public String getDetailLocalId() {
		return detailLocalId;
	}
	public void setDetailLocalId(String detailLocalId) {
		this.detailLocalId = detailLocalId;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getAllocatingProtocol() {
		return allocatingProtocol;
	}
	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getPostOperation() {
		return postOperation;
	}
	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}
	public Set<DetailLocalAttrRelationData> getDetailLocalSet() {
		return detailLocalSet;
	}
	public void setDetailLocalSet(Set<DetailLocalAttrRelationData> detailLocalSet) {
		this.detailLocalSet = detailLocalSet;
	}
	public List<DetailLocalAttrRelationData> getMappingList() {
		return mappingList;
	}
	public void setMappingList(List<DetailLocalAttrRelationData> mappingList) {
		this.mappingList = mappingList;
	}
	public Set getmappingDataSet() {
		return mappingDataSet;
	}
	public void setmappingDataSet(Set mappingDataSet) {
		this.mappingDataSet = mappingDataSet;
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
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Event Date Format", eventDateFormat);
		object.put("Write Attributes", writeAttributes);
		object.put("Use Dictionary Value", useDictionaryValue);
		object.put("AVPair Separator", avpairSeperator);
		object.put("File Name", fileName);
		object.put("Location", location);
		object.put("Default Folder Name", defaultDirName);
		object.put("Prefix File Name", prefixFileName);
		object.put("Folder Name", folderName);
		if(sizeBasedRollingUnit!=null){
			object.put("Size Based Rolling Unit", sizeBasedRollingUnit);
		}
		if(timeBasedRollingUnit!=null){
			object.put("Time Based Rolling Unit ", timeBasedRollingUnit);
		}
		if(recordBasedRollingUnit!=null){
			object.put("Record Based Rolling Unit ", recordBasedRollingUnit);
		}
		object.put("Sequence Range", range);
		object.put("Pattern", pattern);
		object.put("Sequence Globalization", globalization);
		object.put("Allocating Protocol", allocatingProtocol);
		object.put("Address", ipaddress);
		object.put("Destination Location", remoteLocation);
		object.put("Username", userName);
		object.put("Password", password);
		object.put("Post Operation", postOperation);
		object.put("Archive Locations", archiveLocation);
		object.put("Fail Over Time", failOverTime);
		object.put("Time Boundary",  getTimeBoundryString(timeBoundry));
		if(detailLocalSet!=null){
			JSONObject fields = new JSONObject();
			for (DetailLocalAttrRelationData element : detailLocalSet) {
				fields.putAll(element.toJson());
			}
			object.put("Driver Attribute Relational Data", fields);
		}
		return object;
	}
	
	private String getTimeBoundryString(Long timeBoundry) {
		if(timeBoundry == 0){
			return "NONE";
		}else if(timeBoundry == 1){
			return "1 Min";
		}else if(timeBoundry == 2){
			return "2 Min";
		}else if(timeBoundry == 3){
			return "3 Min";
		}else if(timeBoundry == 5){
			return "5 Min";
		}else if(timeBoundry == 10){
			return "10 Min";
		}else if(timeBoundry == 20){
			return "20 Min";
		}else if(timeBoundry == 30){
			return "30 Min";
		}else if(timeBoundry == 60){
			return "Hourly";
		}else if(timeBoundry == 1440){
			return "Daily";
		}
		return "";
	}
	
}

