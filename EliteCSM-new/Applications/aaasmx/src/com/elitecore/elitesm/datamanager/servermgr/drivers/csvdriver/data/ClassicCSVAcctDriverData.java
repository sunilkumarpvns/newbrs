package com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.DriverConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.UpperCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.drivers.radius.classiccsvacct.TimeBoundryAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "classic-csv-acct-driver")
@XmlType(propOrder = { "header", "delimeter", "multivaluedelimeter", "enclosingCharacter", "cdrtimestampFormat", "filename", "location",
		"createBlankFile", "prefixfilename", "defaultdirname", "foldername", "allocatingprotocol", "ipaddress", "remotelocation", "username"
		,"password", "postoperation", "archivelocation", "failovertime", "timeBoundry", "sizeBasedRollingUnit", "timeBasedRollingUnit", "recordBasedRollingUnit"
		 ,"usedictionaryvalue", "range", "pattern", "globalization", "cdrTimestampHeader", "cdrTimestampPosition"
		,"csvAttrRelList", "csvPattRelList"})

@ValidObject
public class ClassicCSVAcctDriverData extends BaseData implements IClassicCSVAcctDriverData,Differentiable,Serializable,Validator{
	//Sets all following fields default values for REST API	
	public ClassicCSVAcctDriverData() {
		this.allocatingprotocol = DriverConstants.ALLOCATING_PROTOCOL;
		this.postoperation = DriverConstants.POST_OPERATION;
		this.archivelocation = DriverConstants.ARCHIVE_LOCATION;
		this.failovertime = DriverConstants.FAILOVER_TIME;
		this.defaultdirname = DriverConstants.DEFAULT_DIRNAME;
		this.timeBoundry = DriverConstants.TIMEBOUNDRY;
		this.createBlankFile = DriverConstants.CREATE_BLANK_FILE;
		this.foldername  =DriverConstants.FOLDER_NAME;
		this.usedictionaryvalue = DriverConstants.USE_DICTIONARY_VALUE;
		this.avpairseparator = DriverConstants.AV_PAIRSEPARATOR;
		this.cdrtimestampFormat= DriverConstants.CDR_TIMESTAMP_FORMAT;
		this.delimeter = DriverConstants.DELIMETER ;
		this.multivaluedelimeter = DriverConstants.MULTIVALUE_DELIMETER;
		this.pattern = DriverConstants.PATTERN;
		this.globalization = DriverConstants.GLOBALIZATION;
		this.cdrTimestampHeader = DriverConstants.CDR_TIMESTAMP_HEADER;
		this.cdrTimestampPosition = DriverConstants.CDR_TIMESTAMP_POSITION;
	}
	private static final long serialVersionUID = 1L;
	private String classicCsvId;	
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Header")
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value of Header of CDR Detail. Value could be 'true' or 'false'.")
	private String header;
	
	@Expose
	@SerializedName("Delimiter")
	private String delimeter;
	
	@Expose
	@SerializedName("MultiValue Delimiter")
	private String multivaluedelimeter;
	
	@Expose
	@SerializedName("Enclosing Character")
	private String enclosingCharacter;
	
	@Expose
	@SerializedName("CDR TimeStamp Format")
	private String cdrtimestampFormat;
	
	@Expose
	@SerializedName("File Name")
	@NotEmpty(message = "File Name must be specified")
	private String filename;
	
	@Expose
	@SerializedName("Location")
	@NotEmpty(message = "Location must be specified")
	private String location;
	 
	@Expose
	@SerializedName("Create Blank File")
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Create Blank File of File Detail. Value could be 'true' or 'false'.")
	private String createBlankFile;
	
	@Expose
	@SerializedName("Prefix File Name")
	private String prefixfilename;
	
	@Expose
	@SerializedName("Default Folder Name")
	private String defaultdirname;
	
	@Expose
	@SerializedName("Folder Name")
	private String foldername;
	@Pattern(regexp = "(?i)(NONE|LOCAL|FTP|SMTP)", message = "Invalid value for Allocationg Protocol of File Transfer Detail. Value could be 'NONE' or 'LOCAL' or 'FTP' or 'SMTP'.")
	private String allocatingprotocol;
	private String ipaddress;	
	private String remotelocation;
	private String username;
	private String password;
	@Pattern(regexp = "(?i)(archive|rename|delete|smtp)", message = "Invalid value for Post Operation of File Transfer Detail. Value could be 'none' or 'archive' or 'rename' or 'delete'.")
	private String postoperation;
	private String archivelocation;
	private Long failovertime;
	@Min(value = 0, message = "Invalid value of time boundry")
	private Long timeBoundry;
	
	@Min(value=0,message="Only Numeric allow in Size Based Rolling Unit")
	private Long sizeBasedRollingUnit;
	@Min(value=0,message="Only Numeric allow in Time Based Rolling Unit")
	private Long timeBasedRollingUnit;
	@Min(value=0,message="Only Numeric allow in Record Based Rolling Unit")
	private Long recordBasedRollingUnit;
	private String eventdateformat;
	//private String writeattributes;
	private String usedictionaryvalue;
	private String avpairseparator;
	private String range;
	@Pattern(regexp = "(?i)(suffix|prefix)", message = "Invalid value for Sequence Position of File Rolling Parameter. Value could be 'suffix' or 'prefix'.")
	private String pattern;
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Sequence Globalization of File Rolling Parameter. Value could be 'true' or 'false'.")
	private String globalization;	
	private String cdrTimestampHeader;
	@Pattern(regexp = "(?i)(SUFFIX|PREFIX)", message = "Invalid value for CDR Timestamp Position of File detail. Value could be 'SUFFIX' or 'PREFIX'.")
	private String cdrTimestampPosition;
	@Valid
	private List<ClassicCSVAttrRelationData> csvAttrRelList;
	@Valid	
	private List<ClassicCSVStripPattRelData> csvPattRelList;
	
	private List<ClassicCSVAttrRelationData> mappingList;
	private List<ClassicCSVStripPattRelData> stripMappingList;
	
	@XmlTransient
	public String getClassicCsvId() {
		return classicCsvId;
	}
	public void setClassicCsvId(String classicCsvId) {
		this.classicCsvId = classicCsvId;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "enclosing-character")
	public String getEnclosingCharacter() {
		return enclosingCharacter;
	}
	public void setEnclosingCharacter(String enclosingCharacter) {
		this.enclosingCharacter = enclosingCharacter;
	}
	
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement(name = "allocating-protocol")
	@XmlJavaTypeAdapter(UpperCaseConvertAdapter.class)
	public String getAllocatingprotocol() {
		return allocatingprotocol;
	}
	public void setAllocatingprotocol(String allocatingprotocol) {
		this.allocatingprotocol = allocatingprotocol;
	}
	
	@XmlElement(name = "address")
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	@XmlElement(name = "destination-location")
	public String getRemotelocation() {
		return remotelocation;
	}
	public void setRemotelocation(String remotelocation) {
		this.remotelocation = remotelocation;
	}
	
	@XmlElement(name = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlElement(name = "post-operation")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getPostoperation() {
		return postoperation;
	}
	public void setPostoperation(String postoperation) {
		this.postoperation = postoperation;
	}
	
	@XmlElement(name = "archive-locations")
	public String getArchivelocation() {
		return archivelocation;
	}
	public void setArchivelocation(String archivelocation) {
		this.archivelocation = archivelocation;
	}
	
	@XmlElement(name = "failover-time")
	public Long getFailovertime() {
		return failovertime;
	}
	public void setFailovertime(Long failovertime) {
		this.failovertime = failovertime;
	}
	
	@XmlElement(name = "file-name")	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@XmlElement(name = "location")
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@XmlElement(name = "default-folder-name")
	public String getDefaultdirname() {
		return defaultdirname;
	}
	public void setDefaultdirname(String defaultdirname) {
		this.defaultdirname = defaultdirname;
	}
	
	@XmlElement(name = "create-blank-file")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getCreateBlankFile() {
		return createBlankFile;
	}
	public void setCreateBlankFile(String createBlankFile) {
		this.createBlankFile = createBlankFile;
	}
	
	@XmlTransient
	public String getEventdateformat() {
		return eventdateformat;
	}
	public void setEventdateformat(String eventdateformat) {
		this.eventdateformat = eventdateformat;
	}
	
	@XmlElement(name = "prefix-file-name")
	public String getPrefixfilename() {
		return prefixfilename;
	}
	public void setPrefixfilename(String prefixfilename) {
		this.prefixfilename = prefixfilename;
	}
	
	@XmlElement(name = "folder-name")
	public String getFoldername() {
		return foldername;
	}
	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}	
	
	@XmlElement(name = "use-dictionary-value")
	public String getUsedictionaryvalue() {
		return usedictionaryvalue;
	}
	public void setUsedictionaryvalue(String usedictionaryvalue) {
		this.usedictionaryvalue = usedictionaryvalue;
	}
	
	@XmlTransient
	public String getAvpairseparator() {
		return avpairseparator;
	}
	public void setAvpairseparator(String avpairseparator) {
		this.avpairseparator = avpairseparator;
	}	
	
	@XmlElement(name = "header")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	
	@XmlElement(name = "delimeter")
	public String getDelimeter() {
		return delimeter;
	}
	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	
	@XmlElement(name = "multivalue-delimeter")
	public String getMultivaluedelimeter() {
		return multivaluedelimeter;
	}
	public void setMultivaluedelimeter(String multivaluedelimeter) {
		this.multivaluedelimeter = multivaluedelimeter;
	}
	
	@XmlElement(name = "sequence-range")
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	
	@XmlElement(name = "sequence-position")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@XmlElement(name = "sequence-globalization")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getGlobalization() {
		return globalization;
	}
	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}
	
	@XmlTransient
	public List<ClassicCSVAttrRelationData> getMappingList() {
		return mappingList;
	}
	public void setMappingList(List<ClassicCSVAttrRelationData> mappingList) {
		this.mappingList = mappingList;
	}
	
	@XmlElementWrapper(name = "classic-csv-strip-pattern-relation-mappings")
	@XmlElement(name = "classic-csv-strip-pattern-relation-mapping")
	public List<ClassicCSVStripPattRelData> getCsvPattRelList() {
		return csvPattRelList;
	}
	public void setCsvPattRelList(List<ClassicCSVStripPattRelData> csvPattRelList) {
		this.csvPattRelList = csvPattRelList;
	}

	@XmlElementWrapper(name = "classic-csv-field-mappings")
	@XmlElement(name = "classic-csv-field-mapping")
	public List<ClassicCSVAttrRelationData> getCsvAttrRelList() {
		return csvAttrRelList;
	}
	public void setCsvAttrRelList(List<ClassicCSVAttrRelationData> csvAttrRelList) {
		this.csvAttrRelList = csvAttrRelList;
	}
	
	@XmlTransient
	public List<ClassicCSVStripPattRelData> getStripMappingList() {
		return stripMappingList;
	}
	public void setStripMappingList(List<ClassicCSVStripPattRelData> stripMappingList) {
		this.stripMappingList = stripMappingList;
	}
	
	@XmlElement(name = "cdr-timestamp-format")
	public String getCdrtimestampFormat() {
		return cdrtimestampFormat;
	}
	public void setCdrtimestampFormat(String cdrtimestampFormat) {
		this.cdrtimestampFormat = cdrtimestampFormat;
	}
	
	@XmlElement(name = "time-boundry")
	@XmlJavaTypeAdapter(value = TimeBoundryAdapter.class)
	public Long getTimeBoundry() {
		return timeBoundry;
	}
	public void setTimeBoundry(Long timeBoundry) {
		this.timeBoundry = timeBoundry;
	}
	
	@XmlElement(name = "size-based-rolling-unit")
	public Long getSizeBasedRollingUnit() {
		return sizeBasedRollingUnit;
	}
	public void setSizeBasedRollingUnit(Long sizeBasedRollingUnit) {
		this.sizeBasedRollingUnit = sizeBasedRollingUnit;
	}
	
	@XmlElement(name = "time-based-rolling-unit")
	public Long getTimeBasedRollingUnit() {
		return timeBasedRollingUnit;
	}
	public void setTimeBasedRollingUnit(Long timeBasedRollingUnit) {
		this.timeBasedRollingUnit = timeBasedRollingUnit;
	}
	
	@XmlElement(name = "record-based-rolling-unit")
	public Long getRecordBasedRollingUnit() {
		return recordBasedRollingUnit;
	}
	public void setRecordBasedRollingUnit(Long recordBasedRollingUnit) {
		this.recordBasedRollingUnit = recordBasedRollingUnit;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Header", header);
		object.put("Delimiter", delimeter);
		object.put("MultiValue Delimiter", multivaluedelimeter);
		object.put("CDR TimeStamp Format", cdrtimestampFormat);
		object.put("Enclosing Character", enclosingCharacter);
		object.put("CDR TimeStamp Header", cdrTimestampHeader);
		object.put("CDR TimeStamp Position", cdrTimestampPosition);
		object.put("File Name", filename);
		object.put("Location", location);
		object.put("Create Blank File", createBlankFile);
		object.put("Prefix File Name", prefixfilename);
		object.put("Default Folder Name", defaultdirname);
		object.put("Folder Name", foldername);
		if(sizeBasedRollingUnit!=null){
			object.put("Size Based Rolling Unit", sizeBasedRollingUnit);
		}
		if(timeBasedRollingUnit!=null){
			object.put("Time Based Rolling Unit", timeBasedRollingUnit);
		}
		if(recordBasedRollingUnit!=null){
			object.put("Record Based Rolling Unit", recordBasedRollingUnit);
		}
		object.put("Sequence Range", range);
		object.put("Sequence Position", pattern);
		object.put("Sequence Globalization", globalization);
		object.put("Allocating Protocol", allocatingprotocol);
		object.put("Address", ipaddress);
		object.put("Destination Location", remotelocation);
		object.put("Username", username);
		object.put("Password", password);
		object.put("Post Operation", postoperation);
		object.put("Archive Locations", archivelocation);
		object.put("Fail Over Time", failovertime);
		object.put("Time-Boundary", getTimeBoundryString(timeBoundry));
		if(csvAttrRelList!=null){
			JSONObject fields = new JSONObject();
			for (ClassicCSVAttrRelationData element : csvAttrRelList) {
				fields.putAll(element.toJson());
			}
			object.put("Classic CSV field Mapping", fields);
		}
		if(csvPattRelList!=null){
			JSONObject fields = new JSONObject();
			for (ClassicCSVStripPattRelData element : csvPattRelList) {
				fields.putAll(element.toJson());
			}
			object.put("Classic CSV Strip Pattern Relation Mapping", fields);
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
	@XmlElement(name = "cdr-timestamp-header")
	public String getCdrTimestampHeader() {
		return cdrTimestampHeader;
	}
	public void setCdrTimestampHeader(String cdrTimestampHeader) {
		this.cdrTimestampHeader = cdrTimestampHeader;
	}
	
	@XmlElement(name ="cdr-timestamp-position")
	@XmlJavaTypeAdapter(UpperCaseConvertAdapter.class)
	public String getCdrTimestampPosition() {
		return cdrTimestampPosition;
	}
	public void setCdrTimestampPosition(String cdrTimestampPosition) {
		this.cdrTimestampPosition = cdrTimestampPosition;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(this.enclosingCharacter != null && this.enclosingCharacter.length() >= 9) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "enclosing character size must be less than 9");
			return isValid;
		}
		
		if(Collectionz.isNullOrEmpty(this.csvAttrRelList) == false){
			LinkedHashSet<ClassicCSVAttrRelationData> classicCSVAttrRelationDataSet = new LinkedHashSet<ClassicCSVAttrRelationData>(this.csvAttrRelList);
			Set<String> checkDuplicateAttrRelSet = getDuplicateAttributeRelationData(classicCSVAttrRelationDataSet);
			if(checkDuplicateAttrRelSet.isEmpty() == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Mapping with Attribute Ids "+checkDuplicateAttrRelSet+" exits multiple times");
				return isValid;
			}
			
		}
		
		if(Collectionz.isNullOrEmpty(this.csvPattRelList) == false){
			LinkedHashSet<ClassicCSVStripPattRelData> classicCSVStripPattRelDataSet = new LinkedHashSet<ClassicCSVStripPattRelData>(this.csvPattRelList);
			Set<String> checkDuplicatePattRelSet = getDuplicatePatternRelationData(classicCSVStripPattRelDataSet);
			
			if(checkDuplicatePattRelSet.isEmpty() == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Mapping with Attribute Ids "+checkDuplicatePattRelSet+" exits multiple times");
				return isValid;
			}
		}
		
		if("FTP".equals(this.allocatingprotocol) || "SMTP".equals(this.allocatingprotocol)){
			if(Strings.isNullOrBlank(this.ipaddress) == false){
				isValid = isValidIpAndPort(this.ipaddress);
				if(isValid == false){
					RestUtitlity.setValidationMessage(context, "Please provide Valid Address [HOST:PORT]");
					return isValid;
				}
			}else{
				RestUtitlity.setValidationMessage(context, "Address (in File Transfer Details) must be specified");
				return false;
			}
		}
		isValid = EliteUtility.checkNullOrEmpty(this.timeBoundry);
		if(isValid == false){return true;}
		
		isValid = EliteUtility.checkNullOrEmpty(this.timeBasedRollingUnit);
		if(isValid == false){return true;}
		
		isValid = EliteUtility.checkNullOrEmpty(this.sizeBasedRollingUnit);
		if(isValid == false){return true;}
		
		isValid = EliteUtility.checkNullOrEmpty(this.recordBasedRollingUnit);
		if(isValid == false){return true;}
		
		if(isValid){
			RestUtitlity.setValidationMessage(context, "One Field is Mandatory Out of  "
					+ "1) Time-Boundry "
					+ "2) Sized Based Rolling Unit"
					+ " 3) Time Based Rolling Unit"
					+ " 4) Record Based Rolling Unit");
			isValid = false;
		}
		return isValid;
	}
	
	
	private Set<String> getDuplicateAttributeRelationData(Set<ClassicCSVAttrRelationData> classicCSVAttrRelationDataSet){
		Set<String> validAttrRelationDatas = new HashSet<String>();
		Set<String> invalidAttrRelationDatas = new HashSet<String>();
		for(ClassicCSVAttrRelationData classicCSVAttrRelationData : classicCSVAttrRelationDataSet){
			String attributeids = classicCSVAttrRelationData.getAttributeids();
			if(Strings.isNullOrBlank(attributeids) == false) {
				boolean	flag = validAttrRelationDatas.add(attributeids); 
				if(flag == false){
					invalidAttrRelationDatas.add(attributeids);
				}
			}
		}
		return invalidAttrRelationDatas;
	}
	private Set<String> getDuplicatePatternRelationData(Set<ClassicCSVStripPattRelData> classicCSVStripPattRelDataSet){
		Set<String> validAttrRelationDatas = new HashSet<String>();
		Set<String> invalidAttrRelationDatas = new HashSet<String>();
		for(ClassicCSVStripPattRelData classicCSVStripPattRelData : classicCSVStripPattRelDataSet){
			String attributeid = classicCSVStripPattRelData.getAttributeid();
			if(Strings.isNullOrBlank(attributeid) == false) {
				boolean	flag = validAttrRelationDatas.add(attributeid);
				if(flag == false){
					invalidAttrRelationDatas.add(attributeid);
				}
			}
		}
		return invalidAttrRelationDatas;
	}
	
	public boolean isValidIpAndPort(String ipAndPort) {
		
		java.util.regex.Pattern ipPortPattern = java.util.regex.Pattern.compile(RestValidationMessages.IPV4_IPV6_REGEX);
		boolean validIp = false;
		boolean validPort = false;
		
		if(ipAndPort == null){
			return false;
		}
		
		try {

			int ipPortPortion = ipAndPort.split(":").length - 1;

			if (ipPortPortion == 1) {
				String[] smallportion = ipAndPort.split(":");
				String port = smallportion[1];
				String tempIpAddress = smallportion[0];
				validPort = isValidPort(port);
				validIp = isValidIp(tempIpAddress,ipPortPattern);
			} else if (ipPortPortion > 1) {
				int portDivisorPos = ipAndPort.lastIndexOf(":");
				String port = ipAndPort.substring(portDivisorPos + 1);
				String tempIpAddress = ipAndPort.substring(0, portDivisorPos);

				if (tempIpAddress.startsWith("[") && tempIpAddress.endsWith("]")) {
					String finalIpddress = tempIpAddress.substring(1,tempIpAddress.length() - 1);
					validPort = isValidPort(port);
					validIp = isValidIp(finalIpddress,ipPortPattern);
				} else {
					validIp = false;
					validPort = false;
				}
			}

			if (validIp && validPort) {
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	private boolean isValidIp(String ipAddress , java.util.regex.Pattern ipPortPattern){
		return ipPortPattern.matcher(ipAddress).matches();
	}
	
	private boolean isValidPort(String port){
		Long portValue;
		try {
			portValue = Long.parseLong(port);
		}catch(NumberFormatException nbe){
			return false;
		}
		if (portValue >= 0 && portValue <= 65535) {
			return true;
		}
		return false;
	}
}
