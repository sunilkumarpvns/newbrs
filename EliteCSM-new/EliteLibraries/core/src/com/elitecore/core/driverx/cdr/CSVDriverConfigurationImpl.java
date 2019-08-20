package com.elitecore.core.driverx.cdr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVFieldMapping;
/**
 * 
 * 
 * @author ishani.bhatt
 * This class provides the configuration for CSVDriver
 */
public abstract class CSVDriverConfigurationImpl implements CSVDriverConfiguration {

	private int driverInstanceId;
	private Map<RollingTypeConstant,Integer> rollingTypeMap;
	private int port;
	private int failOverTime;
	private String headerLine;
	private String counterFileName;
	private int driverType;
	
	private boolean header;
	private boolean sequenceGlobalization;

	private String driverName;
	private String delimiter;
	private String fileName ;
	private String fileLocation; 
	private String prefixFileName;
	private String defaultDirName;
	private String directoryName;
	private String sequenceRange;
	private String sequencePosition;
	private String allocatingProtocol;
	private String ipAddress;
	private String remoteLocation;
	private String userName;
	private String password;
	private String postOperation;
	private String archiveLocation;
	private SimpleDateFormat cdrTimeStampFormat;
	private String enclosingCharacter;
	private boolean createBlankFile;
	private String multiValueDelimiter;

	private List<CSVFieldMapping> fieldMappings;


	public CSVDriverConfigurationImpl(){
		this.rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
		this.fieldMappings = new ArrayList<CSVFieldMapping>();
	}


	@Override
	public SimpleDateFormat getCDRTimeStampFormat() {
		return cdrTimeStampFormat;
	}

	@Override
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return rollingTypeMap;
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
	public boolean isSequenceGlobalization() {
		return sequenceGlobalization;
	}

	@Override
	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public String getMultipleDelimiter() {
		return multiValueDelimiter;
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
	public String getDefaultDirectoryName() {
		return defaultDirName;
	}

	@Override
	public String getDirectoryName() {
		return directoryName;
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
	public String getIPAddress() {
		return ipAddress;
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
	public List<CSVFieldMapping> getCSVFieldMappings() {
		return fieldMappings;
	}

	@Override
	public boolean getCreateBlankFile() {
		return createBlankFile;
	}

	@Override
	public String getEnclosingCharacter() {
		return enclosingCharacter;
	}

	public int getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(int driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	@Override
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}



	public void setDefaultDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}


	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}


	public void setAddress(String address) {
		this.ipAddress = address;
	}


	public void setCdrTimeStampFormat(SimpleDateFormat cdrTimeStampFormat) {
		this.cdrTimeStampFormat = cdrTimeStampFormat;
	}


	public void setMultiValueDelimiter(String multiValueDelimiter) {
		this.multiValueDelimiter = multiValueDelimiter;
	}


	public void setFieldMappings(List<CSVFieldMapping> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}

	public void setRollingTypeMap(Map<RollingTypeConstant, Integer> rollingTypeMap) {
		this.rollingTypeMap = rollingTypeMap;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setFailOverTime(int failOverTime) {
		this.failOverTime = failOverTime;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public void setSequenceGlobalization(boolean sequenceGlobalization) {
		this.sequenceGlobalization = sequenceGlobalization;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}

	public void setSequenceRange(String sequenceRange) {
		this.sequenceRange = sequenceRange;
	}

	public void setSequencePosition(String sequencePosition) {
		this.sequencePosition = sequencePosition;
	}

	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}

	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}

	public void setArchiveLocation(String archiveLocation) {
		this.archiveLocation = archiveLocation;
	}

	public void setEnclosingCharacter(String enclosingCharacter) {
		this.enclosingCharacter = enclosingCharacter;
	}

	public void setCreateBlankFile(boolean createBlankFile) {
		this.createBlankFile = createBlankFile;
	}


	public String getHeaderLine() {
		return headerLine;
	}

	public void setCounterFileName(String counterFileName) {
		this.counterFileName = counterFileName;
	}
	@Override
	public String getCounterFileName() {
		return counterFileName;
	}

	@Override
	public int getDriverType() {
		return driverType;
	}


	public void setDriverType(int driverType) {
		this.driverType = driverType;
	}
	
}
