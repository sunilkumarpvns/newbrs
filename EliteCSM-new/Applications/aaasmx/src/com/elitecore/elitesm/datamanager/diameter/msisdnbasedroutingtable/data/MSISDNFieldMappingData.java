package com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data;

import net.sf.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;

public class MSISDNFieldMappingData implements Differentiable{

	private String msisdnFieldMapId;
	private String msisdnRange;
	private String routingTableId;
	private String primaryPeerName;
	private String secondaryPeerName;
	private String primaryPeerId;
	private String secondaryPeerId;
	private String tag;
	private Integer orderNumber;
	
	public String getMsisdnFieldMapId() {
		return msisdnFieldMapId;
	}
	public void setMsisdnFieldMapId(String msisdnFieldMapId) {
		this.msisdnFieldMapId = msisdnFieldMapId;
	}
	public String getMsisdnRange() {
		return msisdnRange;
	}
	public void setMsisdnRange(String msisdnRange) {
		this.msisdnRange = msisdnRange;
	}
	public String getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getPrimaryPeerName() {
		return primaryPeerName;
	}
	public void setPrimaryPeerName(String primaryPeerName) {
		this.primaryPeerName = primaryPeerName;
	}
	public String getSecondaryPeerName() {
		return secondaryPeerName;
	}
	public void setSecondaryPeerName(String secondaryPeerName) {
		this.secondaryPeerName = secondaryPeerName;
	}
	public String getPrimaryPeerId() {
		return primaryPeerId;
	}
	public void setPrimaryPeerId(String primaryPeerId) {
		this.primaryPeerId = primaryPeerId;
	}
	public String getSecondaryPeerId() {
		return secondaryPeerId;
	}
	public void setSecondaryPeerId(String secondaryPeerId) {
		this.secondaryPeerId = secondaryPeerId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public String toString() {
		return "MSISDNFieldMappingData [msisdnFieldMapId=" + msisdnFieldMapId
				+ ", msisdnRange=" + msisdnRange + ", routingTableId="
				+ routingTableId + ", primaryPeerName=" + primaryPeerName
				+ ", secondaryPeerName=" + secondaryPeerName
				+ ", primaryPeerId=" + primaryPeerId + ", secondaryPeerId="
				+ secondaryPeerId + ", tag=" + tag + "]";
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("MSISDN Prefix ", msisdnRange);
		object.put("Primary Peer", primaryPeerName);
		object.put("Secondary Peer", secondaryPeerName);
		object.put("Tag ", tag);
		return object;
	}
}
