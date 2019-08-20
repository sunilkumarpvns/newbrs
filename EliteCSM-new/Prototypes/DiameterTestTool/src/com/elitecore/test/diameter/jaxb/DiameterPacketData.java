package com.elitecore.test.diameter.jaxb;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

 
@XmlRootElement(name = "packet")
public class DiameterPacketData {
	
	@SerializedName("App-Id") private int appId;
	@SerializedName("cc") 	  private int commandCode;
	@SerializedName("isReq")  private boolean isRequest;
	@SerializedName("attr")   private List<DiameterAttributeData> attributeDatas;
	

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
	
	public void setAttributeDatas(List<DiameterAttributeData> attributeDatas) {
		this.attributeDatas = attributeDatas;
	}

}
