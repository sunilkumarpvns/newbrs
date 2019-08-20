package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PluginEntryData;

@XmlRootElement(name = "plugin-handler")
public class DiameterPluginHandlerData extends DiameterApplicationHandlerDataSupport {
	
	@Valid
	private List<PluginEntryData> pluginEntries = new ArrayList<PluginEntryData>();
	
	@XmlElement(name = "plugin-entry")
	@Size(min = 1, message = "Atleast one mapping is required in Plugin Handler")
	public List<PluginEntryData> getPluginEntries() {
		return pluginEntries;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		if(Collectionz.isNullOrEmpty(pluginEntries) == false){
			JSONArray array = new JSONArray();
			for(PluginEntryData pluginEntryData : pluginEntries){
				array.add(pluginEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Plugin entry", array);
			}
		}
		return object;
	}
}
