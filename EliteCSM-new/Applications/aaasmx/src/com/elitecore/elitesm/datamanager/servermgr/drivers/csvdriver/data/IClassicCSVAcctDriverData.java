package com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data;

import java.util.List;

public interface IClassicCSVAcctDriverData {
	
	public String getClassicCsvId() ;
	public String getDriverInstanceId() ;
	public String getAllocatingprotocol() ;
	public String getIpaddress() ;
	public String getRemotelocation() ;
	public String getUsername() ;
	public String getPassword();
	public String getPostoperation() ;
	public String getArchivelocation() ;
	public Long getFailovertime() ;
	public String getFilename() ;
	public String getLocation() ;
	public String getDefaultdirname() ;
	public Long getTimeBoundry();
	public Long getSizeBasedRollingUnit();
	public Long getTimeBasedRollingUnit();
	public Long getRecordBasedRollingUnit();
	public String getEventdateformat() ;
	public String getPrefixfilename() ;
	public String getCreateBlankFile();
	public void setCreateBlankFile(String createBlankFile);
	public String getFoldername() ;
//	public String getWriteattributes() ;
	public String getUsedictionaryvalue() ;
	public String getAvpairseparator() ;
	public String getHeader() ;
	public String getDelimeter();
	public String getMultivaluedelimeter() ;
	public String getRange() ;
	public String getPattern();
	public String getGlobalization() ;
	public List getCsvAttrRelList() ;
	public List getCsvPattRelList() ;
	public String getCdrtimestampFormat();

}
