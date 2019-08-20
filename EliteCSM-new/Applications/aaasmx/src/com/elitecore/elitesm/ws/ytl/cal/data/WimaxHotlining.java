package com.elitecore.elitesm.ws.ytl.cal.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class WimaxHotlining {

	private String subscriberName;
	private String sendHAInfo;
	private String sendFAInfo;
	
	private List<FANode> faNodes = new ArrayList<FANode>();
	
	@XmlElement(name = "subscriber-name")
	public String getSubscriberName() {
		return subscriberName;
	}
	
	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}
	
	@XmlElement(name = "send-ha-info")
	public String getSendHAInfo() {
		return sendHAInfo;
	}
	
	public void setSendHAInfo(String sendHAInfo) {
		this.sendHAInfo = sendHAInfo;
	}
	
	@XmlElement(name = "send-fa-info")
	public String getSendFAInfo() {
		return sendFAInfo;
	}
	
	public void setSendFAInfo(String sendFAInfo) {
		this.sendFAInfo = sendFAInfo;
	}
	
	@XmlElement(name = "fa-node")
	public List<FANode> getFaNode() {
		return faNodes;
	}
}
