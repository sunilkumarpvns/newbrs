package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlRootElement(name="peer-group-data")
@XmlType(propOrder = { "ruleset", "diameterPeerGroupRelDataSet" })
public class DiameterPeerGroupData extends BaseData implements Differentiable{
	private String peerGroupId;
	private String ruleset;
	private String routingConfigId;
	private DiameterRoutingConfData RoutingConfData;
	private Set<DiameterPeerGroupRelData> diameterPeerGroupRelDataSet;
	private Integer orderNumber;
	
	@XmlElementWrapper(name = "peer")
	@XmlElement(name = "peer-data")
	public Set<DiameterPeerGroupRelData> getDiameterPeerGroupRelDataSet() {
		return diameterPeerGroupRelDataSet;
	}

	public void setDiameterPeerGroupRelDataSet(Set<DiameterPeerGroupRelData> diameterPeerGroupRelDataSet) {
		this.diameterPeerGroupRelDataSet = diameterPeerGroupRelDataSet;
	}

	@XmlTransient
	public String getPeerGroupId() {
		return peerGroupId;
	}
	
	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
	
	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlTransient
	public String getRoutingConfigId() {
		return routingConfigId;
	}
	
	public void setRoutingConfigId(String routingConfigId) {
		this.routingConfigId = routingConfigId;
	}

	@XmlTransient
	public DiameterRoutingConfData getRoutingConfData() {
		return RoutingConfData;
	}
	
	public void setRoutingConfData(DiameterRoutingConfData routingConfData) {
		RoutingConfData = routingConfData;
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("RuleSet", ruleset);
		
		if(diameterPeerGroupRelDataSet!=null){
			JSONArray array = new JSONArray();
			for (DiameterPeerGroupRelData element : diameterPeerGroupRelDataSet) {
				array.add(element.toJson());
			}
			object.put("Peer", array);
		}
		return object;
	}
}