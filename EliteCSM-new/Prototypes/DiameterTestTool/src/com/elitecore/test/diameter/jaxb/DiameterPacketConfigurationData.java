package com.elitecore.test.diameter.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.annotations.SerializedName;


@XmlRootElement(name = "diameter-packet-configuration")
@XmlType(propOrder = { "name", "ipAddress", "port", "timeOut", "packetData" })
public class DiameterPacketConfigurationData {
	private String name;
	@SerializedName("ip-address") private String ipAddress;
	private Long port;
	@SerializedName("timeout") private Long timeOut;
	@SerializedName("packet") private DiameterPacketData packetData;
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="ip-address")
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	@XmlElement(name="port")
	public Long getPort() {
		return port;
	}
	public void setPort(Long port) {
		this.port = port;
	}
	
	@XmlElement(name="timeout")
	public Long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}
	
	@XmlElement(name="packet")
	public DiameterPacketData getPacketData() {
		return packetData;
	}
	public void setPacketData(DiameterPacketData packetData) {
		this.packetData = packetData;
	}
}
