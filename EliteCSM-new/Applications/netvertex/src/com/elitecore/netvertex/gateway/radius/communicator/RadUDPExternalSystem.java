package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.util.url.URLData;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This class is for the Radius UDP external system.
 * @author harsh
 *
 */
public class RadUDPExternalSystem extends UDPExternalSystemData {

	private boolean isICMPPingEnabled;
	private String sharedSecret;
	
	public RadUDPExternalSystem(String id,String targetSystemIPAddress,
			 int expCount, int communicationTimeout,
			int minLocalPort, String sharedSecret, int retryCount, int statusCheckDuration,String name, boolean isICMPPingEnabled){
		super(id, targetSystemIPAddress,expCount, communicationTimeout,
				minLocalPort, retryCount, statusCheckDuration,name, GENERIC_ESI_TYPE);
		this.isICMPPingEnabled = isICMPPingEnabled;
		this.sharedSecret = sharedSecret;
	}
	
	/**
	 * This method returns the shared secret.
	 * @return<code>String<code>
	 */
	public String getSharedSecret(){
		return sharedSecret;
	}
	
	public boolean isICMPPingEnabled(){
		return isICMPPingEnabled;
	}

	@Override
	@XmlTransient
	public URLData getURL() {
		URLData urlData = new URLData();
		urlData.setHost(getIPAddress().getHostAddress());
		urlData.setPort(getPort());
		return urlData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)  {
			return true;
		}


		if ((o instanceof RadUDPExternalSystem) == false) {
			return false;
		}

		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
