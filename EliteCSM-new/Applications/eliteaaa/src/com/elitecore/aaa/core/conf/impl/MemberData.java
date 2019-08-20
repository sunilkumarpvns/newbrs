package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

/**
 * @author malav
 *
 */
@XmlType(propOrder = {})
public class MemberData implements Differentiable {

	
	private String ip;
	private String name;

	@XmlElement(name="ip")
	public String getIp() {
		return ip;
	}

	@XmlElement(name="name")
	public String getName() {
		return name;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Instance Name", name);
		jsonObject.put("IP Address", ip);
		
		return jsonObject;
	}
	
	

}
