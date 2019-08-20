package com.elitecore.test.diameter.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.dependecy.diameter.Application;
import com.elitecore.test.dependecy.diameter.EndToEndPool;
import com.elitecore.test.dependecy.diameter.HopByHopPool;
import com.elitecore.test.dependecy.diameter.packet.CommandCode;
import com.google.gson.annotations.SerializedName;


@XmlRootElement(name = "packet")
public class PacketData {
	
	@SerializedName("App-Id") private String appId=Application.BASE.getDisplayName();
	@SerializedName("cc") 	  private String commandCode = CommandCode.CAPABILITIES_EXCHANGE.displayName;
	@SerializedName("isReq")  private boolean isRequest;
	@SerializedName("attr")   private List<AttributeData> attributeDatas;
	@SerializedName("h2h")   private int h2h = HopByHopPool.get();
	@SerializedName("e2e")   private int e2e = EndToEndPool.get();
	
	
	@XmlAttribute(name = "h2h",required=true)
	public int getH2h() {
		return h2h;
	}
	
	@XmlAttribute(name = "e2e",required=true)
	public int getE2e() {
		return e2e;
	}

	@XmlAttribute(name = "request",required=true)
	public boolean getIsRequest() {
		return isRequest;
	}
	
	@XmlAttribute(name = "cc",required=true)
	public String getCommandCode() {
		return commandCode;
	}
	
	@XmlElement(name = "attribute")
	public List<AttributeData> getAttributeDatas() {
		return attributeDatas;
	}
	
	@XmlAttribute(name = "app-id",required=true)
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		if(appId != null) {			
			this.appId = appId;
		}
	}
	
	public void setIsRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}

	public void setCommandCode(String commandCode) {
		if(commandCode != null) {
			this.commandCode = commandCode;
		}
	}
	
	public void setAttributeDatas(List<AttributeData> attributeDatas) {
		this.attributeDatas = attributeDatas;
	}
	public void setH2h(int h2h) {
		this.h2h = h2h;
	}

	public void setE2e(int e2e) {
		this.e2e = e2e;
	}

	@Override
	public String toString() {
		return "PacketData [appId=" + appId + ", commandCode=" + commandCode
				+ ", isRequest=" + isRequest + ", attributeDatas="
				+ attributeDatas + ", h2h=" + h2h + ", e2n=" + e2e + "]";
	}

	

	


}
