/**
 * 
 */
package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.config.BooleanAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author malav
 *
 */
@XmlRootElement(name = "imdg")
@XmlType(propOrder = {})
public class ImdgConfigData implements Differentiable{

	private Properties properties;
	private List<String> outbounds;

	// basic configuration
	private List<ClusterGroupData> clusterGroups;
	private boolean active = false;
	private List<Property> propertyList;

	//Advance Configuration
	private String inMemoryFormat;
	private int startPortCount;
	private int startPort;
	private String outboundPorts;
	private String mancenterUrl;

	private ImdgRadiusSessionConfigData imdgRadiusConfig;
	private ImdgDiameterSessionConfigData imdgDiameterConfig;

	public ImdgConfigData() {
		this.properties = new Properties();
		this.outbounds = new ArrayList<>();
		this.propertyList = new ArrayList<>();
		this.setImdgRadiusConfig(new ImdgRadiusSessionConfigData());
	}

	@XmlJavaTypeAdapter(value=BooleanAdapter.class)
	@XmlElement(name="active")
	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@XmlElementWrapper(name="groups")
	@XmlElement(name="group", type=ClusterGroupData.class)
	public List<ClusterGroupData> getClusterGroups() {
		return clusterGroups;
	}

	public void setClusterGroups(List<ClusterGroupData> clusterGroups) {
		this.clusterGroups = clusterGroups;
	}

	@XmlElement(name="in-memory-format")
	public String getInMemoryFormat() {
		return inMemoryFormat;
	}

	public void setInMemoryFormat(String inMemoryFormat) {
		this.inMemoryFormat = inMemoryFormat;
	}

	@XmlElement(name="start-port-count")
	public int getStartPortCount() {
		return startPortCount;
	}

	public void setStartPortCount(int startPortCount) {
		this.startPortCount = startPortCount;
	}

	@XmlElement(name="start-port")
	public int getStartPort() {
		return startPort;
	}

	public void setStartPort(int start) {
		this.startPort = start;
	}

	@XmlElement(name="mancenter-url")
	public String getMancenterUrl() {
		return mancenterUrl;
	}

	public void setMancenterUrl(String mancenterUrl) {
		this.mancenterUrl = mancenterUrl;
	}

	@XmlElementWrapper(name="properties")
	@XmlElement(name="property")
	public List<Property> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<Property> propertyList) {
		this.propertyList = propertyList;
	}

	@XmlElement(name="outbound-ports")
	public String getOutboundPorts() {
		return outboundPorts;
	}

	public void setOutboundPorts(String outboundPorts) {
		this.outboundPorts = outboundPorts;
	}

	@XmlTransient
	public Properties getProperties() {
		return properties;
	}

	@XmlTransient
	public List<String> getOutbounds() {
		return outbounds;
	}

	@XmlElement(name="imdg-radius")
	public ImdgRadiusSessionConfigData getImdgRadiusConfig() {
		return imdgRadiusConfig;
	}

	public void setImdgRadiusConfig(ImdgRadiusSessionConfigData imdgRadiusConfig) {
		this.imdgRadiusConfig = imdgRadiusConfig;
	}

	@XmlElement(name="imdg-diameter")
	public ImdgDiameterSessionConfigData getImdgDiameterConfig() {
		return imdgDiameterConfig;
	}

	public void setImdgDiameterConfig(ImdgDiameterSessionConfigData imdgDiameterConfig) {
		this.imdgDiameterConfig = imdgDiameterConfig;
	}
	
	public void postRead() {
		for (Property property : propertyList) {
			this.properties.put(property.getKey(), property.getValue());
		}
		this.outbounds = Splitter.on(',').split(outboundPorts);
		imdgRadiusConfig.postRead();
	}

	@Override
	public JSONObject toJson() {

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("In-Memory Data Grid Status", (active) ? "Enabled" : "Disabled");

		if( Collectionz.isNullOrEmpty(clusterGroups) == false ) {

			JSONArray jsonArray = new JSONArray();
			for( ClusterGroupData clusterGroupData : clusterGroups ) {
				jsonArray.add(clusterGroupData.toJson());
			}

			jsonObject.put("Group Configurations", jsonArray);
		}

		if( Collectionz.isNullOrEmpty(propertyList) == false ) {
			JSONArray jsonArray = new JSONArray();
			for (Property property : propertyList) {
				jsonArray.add(property.toJson());
			}

			jsonObject.put("Properties", jsonArray);
		}

		if(imdgRadiusConfig != null) {
			jsonObject.put("Radius imdg Configuration", imdgRadiusConfig.toJson());
		}
		if(imdgDiameterConfig != null) {
		jsonObject.put("Diameter imdg Configuration", imdgDiameterConfig.toJson());
		}

		JSONObject advacedConfigJson = new JSONObject();

		advacedConfigJson.put("In-Memory Format", inMemoryFormat);
		advacedConfigJson.put("Start Port", startPort);
		advacedConfigJson.put("Start Port Count", startPortCount);
		advacedConfigJson.put("Outbound Ports", outboundPorts);
		advacedConfigJson.put("Mancenter URL", mancenterUrl);

		jsonObject.put("Advanced Configuration", advacedConfigJson);

		return jsonObject;
	}

}
