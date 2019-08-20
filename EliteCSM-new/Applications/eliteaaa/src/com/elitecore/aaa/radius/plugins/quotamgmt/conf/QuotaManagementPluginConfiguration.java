package com.elitecore.aaa.radius.plugins.quotamgmt.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.plugins.PluginInfo;

@XmlRootElement(name = "quota-management-plugin")
public class QuotaManagementPluginConfiguration implements Differentiable {

	private String name;
	private String description;
	private String status;
	private List<QuotaManagementData> pluginsData = new ArrayList<QuotaManagementData>();
	
	@XmlTransient
	private PluginInfo pluginInfo;

	public QuotaManagementPluginConfiguration() {
	}
	
	@XmlElement(name = "quota-management-plugin-data")
	public List<QuotaManagementData> getPluginsData() {
		return pluginsData;
	}
	
	public void setPluginsData(List<QuotaManagementData> pluginsData) {
		this.pluginsData = pluginsData;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public JSONObject toJson() {
		
		JSONObject object = new JSONObject();
		
		object.put("Name", name);
		object.put("Description", description);
		object.put("Status", status);
		
		if (pluginsData != null) {
			JSONArray array = new JSONArray();
			for (QuotaManagementData element : pluginsData) {
				array.add(element.toJson());
			}
			object.put("Quota Manager Policy List ", array);
		}
		
		return object;
	}

	@PostRead
	public void postRead() {
		
		pluginInfo = new com.elitecore.core.commons.plugins.PluginInfo();
		pluginInfo.setPluginName(name);
		
		for (QuotaManagementData data : pluginsData) {
			data.postRead();
		}
	}

	@XmlTransient
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
}