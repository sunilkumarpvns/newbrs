package com.elitecore.aaa.core.diameter.packet.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.aaa.core.commons.util.exception.InvalidPacketException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

 
@XmlRootElement(name = "diameter-packet")
public class DiameterPacketData {
	
	private int appId;
	private int commandCode;
	private boolean isRequest;
	private List<DiameterAttributeData> attributeDatas = new ArrayList<DiameterAttributeData>();
	
	@XmlAttribute(name = "request",required=true)
	public boolean getIsRequest() {
		return isRequest;
	}
	
	@XmlAttribute(name = "cc",required=true)
	public int getCommandCode() {
		return commandCode;
	}
	
	@XmlElement(name = "attribute")
	public List<DiameterAttributeData> getAttributeDatas() {
		return attributeDatas;
	}
	
	@XmlAttribute(name = "app-id",required=true)
	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}
	
	public void setIsRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}

	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
	}
	
	public DiameterPacket create() throws InvalidPacketException, AttributeNotFoundException {
		
		if (this.getCommandCode() <= 0) {
			throw new InvalidPacketException("Invalid command code: " + getCommandCode() + 
					", Command code must be positive numeric");
		}
		if (this.getAppId() < 0) {
			throw new InvalidPacketException("Invalid application id: " + getAppId() + 
					", Application id must be positive numeric or 0");
		}
		DiameterPacket diameterPacket = null;
		if (this.getIsRequest()) {
			diameterPacket = new DiameterRequest(false);
		} else {
			diameterPacket = new DiameterAnswer();
		}
		diameterPacket.setCommandCode(this.getCommandCode());
		diameterPacket.setApplicationID(this.getAppId());
		
		if (Collectionz.isNullOrEmpty(this.getAttributeDatas())) {
			return diameterPacket;
		}
		for(DiameterAttributeData attributeData : this.getAttributeDatas()){
			diameterPacket.addAvp(attributeData.create());
		}
		return diameterPacket;
	}

}
