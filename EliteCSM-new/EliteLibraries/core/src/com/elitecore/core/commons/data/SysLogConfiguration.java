package com.elitecore.core.commons.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;

@XmlType(propOrder = {})
public class SysLogConfiguration {
	private String hostIpAddress;
	private String facility = "USER";
	
	public SysLogConfiguration(String hostIpAddress, String facility) {
		this.hostIpAddress =hostIpAddress;
		if(facility!=null && facility.trim().length()>0)
			this.facility = facility;
	}
	public SysLogConfiguration() {
		// required by Jaxb.
	}
	
	@Reloadable(type=String.class)
	@XmlElement(name = "facility",type = String.class,defaultValue ="USER")
	public String getFacility() {
		return facility;
	}
	
	public void setFacility(String facility){
		this.facility = facility;
	}
	
	@XmlElement(name = "address",type =String.class)
	public String getHostIp() {
		return hostIpAddress;
	}
	
	public void setHostIp(String hostIpAddress){
		this.hostIpAddress = hostIpAddress;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Address  = " + getHostIp());
		out.println("Facility = " + getFacility());
		out.close();
		return stringBuffer.toString();
	}
}
