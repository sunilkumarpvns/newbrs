package com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data;

import java.util.Set;

public interface IDetailLocalAcctDriver {
	
	public String getDetailLocalId() ;
	public void setDetailLocalId(String detailLocalId) ;
	public String getDriverInstanceId() ;
	public void setDriverInstanceId(String driverInstanceId) ;
	public String getAllocatingProtocol() ;
	public void setAllocatingProtocol(String allocatingProtocol) ;
	public String getIpaddress() ;
	public void setIpaddress(String ipaddress) ;
	public String getPassword();
	public void setPassword(String password);
	public String getRemoteLocation() ;
	public void setRemoteLocation(String remoteLocation) ;
	public String getUserName() ;
	public void setUserName(String userName) ;
	public String getArchiveLocation() ;
	public void setArchiveLocation(String archiveLocation) ;
	public long getFailOverTime() ;
	public void setFailOverTime(long failOverTime) ;
	public String getFileName() ;
	public void setFileName(String fileName) ;
	public String getLocation() ;
	public void setLocation(String location) ;
	public String getDefaultDirName() ;
	public void setDefaultDirName(String defaultDirName) ;
	public String getEventDateFormat() ;
	public void setEventDateFormat(String eventDateFormat) ;
	public String getPrefixFileName() ;
	public void setPrefixFileName(String prefixFileName) ;
	public String getFolderName() ;
	public void setFolderName(String folderName) ;
	public String getWriteAttributes() ;
	public void setWriteAttributes(String writeAttributes) ;
	public String getUseDictionaryValue() ;
	public void setUseDictionaryValue(String useDictionaryValue) ;
	public String getAvpairSeperator() ;
	public void setAvpairSeperator(String avpairSeperator) ;
	public String getRange() ;
	public void setRange(String range) ;
	public String getPattern() ;
	public void setPattern(String pattern) ;
	public String getGlobalization() ;
	public void setGlobalization(String globalization) ;
	public Set<DetailLocalAttrRelationData> getDetailLocalSet() ;
	public void setDetailLocalSet(Set<DetailLocalAttrRelationData> detailLocalSet) ;
	public Long getTimeBoundry();
	public void setTimeBoundry(Long timeBoundry);
	public Long getSizeBasedRollingUnit();
	public void setSizeBasedRollingUnit(Long sizeBasedRollingUnit);
	public Long getTimeBasedRollingUnit();
	public void setTimeBasedRollingUnit(Long timeBasedRollingUnit);
	public Long getRecordBasedRollingUnit();
	public void setRecordBasedRollingUnit(Long recordBasedRollingUnit);

}
