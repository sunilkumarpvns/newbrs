package com.elitecore.elitesm.datamanager.wsconfig.data;

import net.sf.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class WSKeyMappingData extends BaseData implements Differentiable{
	private String wsKeyMappingId;
	private String logicalName;
	private String wsConfigId;
	private String wsKey;
	private String dbField;
	private String request;
	private String response;
	private Integer orderNumber;
	
	public String getWsKeyMappingId() {
		return wsKeyMappingId;
	}

	public void setWsKeyMappingId(String wsAddFieldMapId) {
		this.wsKeyMappingId = wsAddFieldMapId;
	}

	public String getWsConfigId() {
		return wsConfigId;
	}

	public void setWsConfigId(String wsConfigId) {
		this.wsConfigId = wsConfigId;
	}

	public String getLogicalName() {
		return logicalName;
	}
	
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	public String getDbField() {
		return dbField;
	}
	
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	public String getWsKey() {
		return wsKey;
	}

	public void setWsKey(String wsKey) {
		this.wsKey = wsKey;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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
		JSONObject innerObject = new JSONObject();
		innerObject.put("DB Field", dbField);
		innerObject.put("Request", request);
		innerObject.put("Response", response);
		return innerObject;
	}
}
