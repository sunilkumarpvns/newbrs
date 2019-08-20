package com.elitecore.aaa.radius.systemx.esix.udp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;

public class DefaultExternalSystemData extends RadiusExternalSystemData {

	private String strRealmName;
	private List<String> realmNames = new ArrayList<String>();
	private String supportedAttributeStr;
	private String unsupportedAttributeStr;
	
	private int loadFactor = 1;
	
	public DefaultExternalSystemData(){
		//required By Jaxb.
	}
	
	public DefaultExternalSystemData(String id, String targetSystemIPAddress,
			int expCount, int communicationTimeout,
			int minLocalPort, String sharedSecret, int retryCount, int statusCheckDuration,
			String name,String realmNames,int esiType,
			String supportedAttributesStr,String unSupportedAttributesStr, 
			StatusCheckMethod scannerType, String scannerPacket) {
		
		super(id, targetSystemIPAddress, expCount, communicationTimeout,
				minLocalPort, sharedSecret, retryCount, statusCheckDuration,name,esiType, scannerType, scannerPacket);

		this.strRealmName = realmNames;
		this.supportedAttributeStr = supportedAttributesStr;
		this.unsupportedAttributeStr = unSupportedAttributesStr;
	}
	
	@XmlElement(name = "supported-attribute",type = String.class)
	public String getSupportedAttributesStr() {
		return supportedAttributeStr;
	}
	public void setSupportedAttributesStr(String supportedAttributeStr){
		this.supportedAttributeStr = supportedAttributeStr;
	}

	@XmlElement(name = "unsupported-attribute",type = String.class)
	public String getUnsupportedAttributesStr() {
		return unsupportedAttributeStr;
	}
	public void setUnsupportedAttributesStr(String unsupportedAttributeStr){
		this.unsupportedAttributeStr = unsupportedAttributeStr;
	}
	
	@XmlElement(name = "realms")
	public String getStrRealmName() {
		return strRealmName;
	}
	public void setStrRealmName(String strRealmName) {
		this.strRealmName = strRealmName;
	}

	@XmlTransient
	public List<String> getRealmNames(){
		return this.realmNames;
	}

	public void setRealmNames(List<String> realmNames){
		this.realmNames = realmNames;
	}
	
	public int getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(int loadFactor) {
		this.loadFactor = loadFactor;
	}

	@Override
	public String toString() {
		String toString = super.toString();
		StringBuilder strBuilder = new StringBuilder(toString);
		strBuilder.append("realms: ");
		strBuilder.append(getStrRealmName());
		strBuilder.append("\n");
		strBuilder.append("supported attributes: ");
		strBuilder.append(getSupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("un-supported attributes: ");
		strBuilder.append(getUnsupportedAttributesStr());
		strBuilder.append("\n");
		strBuilder.append("Status-Check-Method: ");
		strBuilder.append(getStatusCheckMethod());
		return strBuilder.toString();
	}
}
