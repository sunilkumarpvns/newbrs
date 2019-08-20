package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;

@XmlRootElement(name = "communicator")
public class CommunicatorData implements Differentiable {
	
	private String id;
	
	private String loadFactor;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "load-factor")
	public String getLoadFactor() {
		return loadFactor;
	}
	public void setLoadFactor(String loadFactor) {
		this.loadFactor = loadFactor;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Server Name", id);
		object.put("Load Factor", loadFactor);
		return object;
	}
}
