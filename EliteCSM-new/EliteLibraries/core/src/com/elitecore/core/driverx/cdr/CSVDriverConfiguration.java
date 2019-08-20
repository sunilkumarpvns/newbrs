package com.elitecore.core.driverx.cdr;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.driverx.cdr.data.CSVFieldMapping;

/**
 * 
 * 
 * @author ishani.bhatt
 *
 */
public interface CSVDriverConfiguration {
	
	public  String getCounterFileName();
	public String getDriverName();
	public int getDriverType();
	public  SimpleDateFormat getCDRTimeStampFormat();
	public Map<RollingTypeConstant, Integer> getRollingTypeMap();
	public int getPort();
	public int getFailOverTime();
	public boolean isHeader();
	public boolean isSequenceGlobalization();
	public String getDelimiter();
	public String getMultipleDelimiter();
	public String getFileName();
	public String getFileLocation();
	public String getPrefixFileName();
	public String getDefaultDirectoryName();
	public String getDirectoryName();
	public String getSequenceRange();
	public String getSequencePosition();
	public String getAllocatingProtocol();
	public String getIPAddress();
	public String getRemoteLocation();
	public String getUserName();
	public String getPassword();
	public String decrypt(String encriptedPassword, int encriptionType);
	public String getPostOperation();
	public String getArchiveLocation();
	public List<CSVFieldMapping> getCSVFieldMappings();
	public boolean getCreateBlankFile();
	public String getEnclosingCharacter();

}
