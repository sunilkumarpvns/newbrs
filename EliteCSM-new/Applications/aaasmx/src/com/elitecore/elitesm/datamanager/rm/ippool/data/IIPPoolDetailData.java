package com.elitecore.elitesm.datamanager.rm.ippool.data;

import java.sql.Timestamp;

public interface IIPPoolDetailData {

    public String getIpPoolId();
    public void setIpPoolId(String iPPoolId);
    public long getSerialNumber();
    public void setSerialNumber(long serialNumber);
    public String getIpAddress();
    public void setIpAddress(String iPAddress);
    public String getAssigned();
    public void setAssigned(String assigned);
    public String getReserved();
    public void setReserved(String reserved);
    public String getNasIPAddress();
    public void setNasIPAddress(String nasIPAddress);
    public String getUserIdentity();
    public void setUserIdentity(String userIdentity);
    public String getIpAddressRange();
	public void setIpAddressRange(String ipAddressRange);
	public String getIpAddressRangeId();
	public void setIpAddressRangeId(String rangeId);
	 public Timestamp getLastUpdatedTime();
	 public void setLastUpdatedTime(Timestamp lastUpdatedTime);
    
}
