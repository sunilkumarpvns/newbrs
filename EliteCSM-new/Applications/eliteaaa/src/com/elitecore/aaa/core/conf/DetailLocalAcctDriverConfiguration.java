package com.elitecore.aaa.core.conf;

import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.core.commons.fileio.RollingTypeConstant;

public interface DetailLocalAcctDriverConfiguration extends DriverConfiguration{
	
	public String getAllocatingProtocol();

	public String getIpAddress();

	public int getPort();

	public String getDestinationLocation();

	public String getUsrName();

	public String getPassword();
	
	public String getPlainTextPassword();

	public String getPostOperation();

	public String getArchiveLocations();

	public int getFailOverTime();

	public String getFileName();

	public String getFileLocation();

	public String getDefauleDirName();

	public String getEventDateFormat();

	public String getPrefixFileName();

	public String getFolderName();

	public String getWriteAttributes();

	public boolean getUseDictionaryValue();

	public String getAvPairSeparator();
	
	public boolean getCreateBlankFile();

	public String getSequenceRange();

	public String getPattern();
	
	public boolean getGlobalization();
	
	public String[] getFileNameAttributes();
	
	public String[] getFolderNameAttributes();
	
	public String toString();
	
	public List<AttributeRelation> getAttributeRelationList();
	
	public Map<RollingTypeConstant, Integer> getRollingTypeMap();
}
