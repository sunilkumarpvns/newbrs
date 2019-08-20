package com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data;

import net.sf.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;

public class IMSIFieldMappingData implements Differentiable{

	private String imsiFieldMapId;
	private String imsiRange;
	private String routingTableId;
	private String primaryPeerName;
	private String secondaryPeerName;
	private String primaryPeerId;
	private String secondaryPeerId;
	private String tag;
	private Integer orderNumber;
	
	public String getImsiFieldMapId() {
		return imsiFieldMapId;
	}
	public void setImsiFieldMapId(String imsiFieldMapId) {
		this.imsiFieldMapId = imsiFieldMapId;
	}
	public String getImsiRange() {
		return imsiRange;
	}
	public void setImsiRange(String imsiRange) {
		this.imsiRange = imsiRange;
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
		return "IMSIFieldMappingData [imsiFieldMapId=" + imsiFieldMapId
				+ ", imsiRange=" + imsiRange + ", routingTableId="
				+ routingTableId + ", primaryPeerName=" + primaryPeerName
				+ ", secondaryPeerName=" + secondaryPeerName
				+ ", primaryPeerId=" + primaryPeerId + ", secondaryPeerId="
				+ secondaryPeerId + ", tag=" + tag + "]";
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("IMSI Prefix ", imsiRange);
		object.put("Primary Peer", primaryPeerName);
		object.put("Secondary Peer", secondaryPeerName);
		object.put("Tag ", tag);
		return object;
	}
	
}
