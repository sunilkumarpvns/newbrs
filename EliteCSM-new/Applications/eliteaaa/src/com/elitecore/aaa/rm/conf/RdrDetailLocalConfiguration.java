package com.elitecore.aaa.rm.conf;

import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

public interface RdrDetailLocalConfiguration extends DriverConfiguration {

	public void readConfiguration() throws LoadConfigurationException;	
	
	//Detail Local Driver Details
	
	public String getEventDateFormat();

	public String getWriteAttributes();

	public boolean getUseDictionaryValue();
	
	public String getAvPairSeparator();
	
	//File Details
	
	public String getFileName();

	public String getFileLocation();

	public String getDefauleDirName();
	
	public String getPrefixFileName();

	public String getFolderName();
	
	//File Rolling Parameters
	public int getFileRollingType();

	public int getRollingUnit();
	
	public String getRange();

	public String getPattern();
	
	public boolean getGlobalization();
	
	//File Allocator Details
	
	public String getAllocatingProtocol();

	public String getIpAddress();

	public int getPort();
	
	public String getRemoteLocation();

	public String getUsrName();

	public String getPassword();

	public String getPostOperation();

	public String getArchieveLocation();

	public int getFailOverTime();
}
