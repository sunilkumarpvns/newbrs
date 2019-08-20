package com.elitecore.netvertexsm.util.driver.cdr.conf;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.netvertexsm.util.driver.DriverConfiguration;

public interface CSVDriverConfiguration extends DriverConfiguration {

	public long getRollingUnit();
	public int getRollingType();
	public int getPort();
	public int getFailOverTime();
	public boolean isHeader();
	public boolean isSequenceGlobalization();
	public String getDelimiter();
	public String getFileName();
	public String getFileLocation();
	public String getPrefixFileName();
	public String getDefaultFolderName();
	public String getSequenceRange();
	public String getDirectoryName();
	public String getSequencePosition();
	public String getAllocatingProtocol();
	public String getAddress();
	public String getRemoteLocation();
	public String getUserName();
	public String getPassword();
	public String getPostOperation();
	public String getArchiveLocation();
	public String getReportingType();
	
	public String getUsageKeyHeader();
	public String getInputOctetsHeader();
	public String getOutputOctetsHeader();
	public String getTotalOctetsHeader();
	public String getUsageTimeHeader();
	
	public List<CSVFieldMapping> getCDRFieldMappings();
	public Map<String, CSVStripMapping> getStripMappings();
	public SimpleDateFormat getCDRTimeStampFormat();
	
}
