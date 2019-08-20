package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

@XmlRootElement(name = "esi-entry-detail")
public class CommunicatorData implements Differentiable{
	private String id;
	private int loadFactor;
	private String name;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "load-factor")
	public int getLoadFactor() {
		return loadFactor;
	}
	public void setLoadFactor(int loadFactor) {
		this.loadFactor = loadFactor;
	}

	@Override
	public String toString() {
		return "Name = " + this.name + ", Load-Factor = " + this.loadFactor + "";
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Esi-id", id);
		object.put("Load Factor", loadFactor);
		return object;
	}
}
