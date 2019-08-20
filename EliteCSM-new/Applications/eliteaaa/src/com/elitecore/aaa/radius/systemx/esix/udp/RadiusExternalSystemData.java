package com.elitecore.aaa.radius.systemx.esix.udp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.util.url.URLData;


public abstract class RadiusExternalSystemData extends UDPExternalSystemData {

	private String sharedSecret ="secret";
	private StatusCheckMethod statusCheckMethod;
	private String scannerPacket;

	public RadiusExternalSystemData(){
		//required By Jaxb.
	}
	
	public RadiusExternalSystemData(String id, String targetSystemIPAddress,
			int expCount, int communicationTimeout,
			int minLocalPort, String sharedSecret, int retryCount, int statusCheckDuration,
			String name, int esiType,
			StatusCheckMethod scannerType, String scannerPacket) {
		
		super(id, targetSystemIPAddress, expCount, communicationTimeout,
				minLocalPort, retryCount, statusCheckDuration,name,esiType);

		this.sharedSecret = sharedSecret;
		this.scannerPacket = scannerPacket;
		this.statusCheckMethod = scannerType;
	}

	@XmlElement(name = "shared-secret",type = String.class,defaultValue ="secret")
	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret){
		this.sharedSecret = sharedSecret;
	}

	@XmlElement(name = "status-check-method", type = StatusCheckMethod.class, defaultValue = "ICMP-Ping")
	public StatusCheckMethod getStatusCheckMethod() {
		return this.statusCheckMethod;
	}
	public void setStatusCheckMethod(StatusCheckMethod method) {
		this.statusCheckMethod = method;
	}
	
	@XmlElement(name = "packet-bytes")
	public String getScannerPacket() {
		return scannerPacket;
	}
	public void setScannerPacket(String scannerPacket) {
		this.scannerPacket = scannerPacket;
	}
	
	@Override
	@XmlTransient
	public URLData getURL() {
		URLData urlData = new URLData();
		urlData.setHost(getIPAddress().getHostAddress());
		urlData.setPort(getPort());
		return urlData;
	}
}
