/**
 * 
 */
package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

/**
 * @author nayana.rathod
 *
 */
@XmlType(propOrder={})
public class Property implements Differentiable{

	private String key;
	private String value;
	
	@XmlElement(name="key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlElement(name="value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Key", key);
		jsonObject.put("Value", value);
		
		return jsonObject;
	}
	
}
