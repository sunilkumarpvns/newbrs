package com.elitecore.elitesm.datamanager.rm.ippool.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class IPPoolDetailData extends BaseData implements IIPPoolDetailData, Serializable{

	private static final long serialVersionUID = 1L;
	private String ipPoolId;
    private long serialNumber;
    private String ipAddress;
    private String assigned;
    private Timestamp lastUpdatedTime;
    private String nasIPAddress;
    private String userIdentity;
    private String reserved;
    private String ipAddressRange;
    private String ipAddressRangeId;
    private String callingStationId;
    
    @XmlTransient
    public String getIpPoolId() {
        return ipPoolId;
    }
    public void setIpPoolId(String ipPoolId) {
        this.ipPoolId = ipPoolId;
    }
    
    @XmlTransient
    public long getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    @XmlTransient
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String iPAddress) {
        this.ipAddress = iPAddress;
    }
    
    @XmlTransient
    public String getAssigned() {
        return assigned;
    }
    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }
    
    @XmlTransient
	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	@XmlTransient
	public String getNasIPAddress() {
		return nasIPAddress;
	}
	public void setNasIPAddress(String nasIPAddress) {
		this.nasIPAddress = nasIPAddress;
		
	}
	
	@XmlTransient
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	
	@XmlTransient
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	
	@XmlElement(name="ip-addess-range")
	public String getIpAddressRange() {
		return ipAddressRange;
	}
	public void setIpAddressRange(String ipAddressRange) {
		this.ipAddressRange = ipAddressRange;
	}
	
	@XmlElement(name="range-id")
	public String getIpAddressRangeId() {
		return ipAddressRangeId;
	}
	public void setIpAddressRangeId(String ipAddressRangeId) {
		this.ipAddressRangeId = ipAddressRangeId;
	}
	
	@XmlTransient
	public String getCallingStationId() {
		return callingStationId;
	}
	public void setCallingStationId(String callingStationId) {
		this.callingStationId = callingStationId;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("----------- IP Pool Detail ------------------");
		writer.println("ipPoolId         : "+ipPoolId);
		writer.println("serialNumber     : "+serialNumber);
		writer.println("iPAddress        : "+ipAddress);
		writer.println("assigned         : "+assigned);
		writer.println("reserved         : "+reserved);
		writer.println("nasIPAddress     : "+nasIPAddress);
		writer.println("callingStationId : "+callingStationId);
		writer.println("userIdentity     : "+userIdentity);
		writer.println("---------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
}