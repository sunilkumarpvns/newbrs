package com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
@XmlRootElement(name = "http-response-mapping")
@XmlType(propOrder = { "logicalName", "responseParamIndex", "defaultValue",	"valueMapping" })
public class HttpAuthDriverFieldMapData extends BaseData implements Differentiable {
	private String httpAuthFieldMapId;
	private String logicalName;
	private Long responseParamIndex;
	private String httpAuthDriverId;
	private String defaultValue;
	private String valueMapping;
	private LogicalNameValuePoolData nameValuePoolData;
	private Integer orderNumber;
	
	public HttpAuthDriverFieldMapData(){}
	
	public HttpAuthDriverFieldMapData(Long responseParamIndex,String defaultValue, String valueMapping,LogicalNameValuePoolData nameValuePoolData) {
		this.responseParamIndex = responseParamIndex;
		this.defaultValue = defaultValue;
		this.valueMapping = valueMapping;
		this.nameValuePoolData = nameValuePoolData;
	}
	
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@XmlElement(name = "value-mapping")
	public String getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}

	@XmlTransient
	public String getHttpAuthFieldMapId() {
		return httpAuthFieldMapId;
	}
	
	public void setHttpAuthFieldMapId(String httpAuthFieldMapId) {
		this.httpAuthFieldMapId = httpAuthFieldMapId;
	}
	
	@XmlElement(name = "logical-name")
	public String getLogicalName() {
		return logicalName;
	}
	
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	@XmlElement(name = "response-parameter-index")
	public Long getResponseParamIndex() {
		return responseParamIndex;
	}
	
	public void setResponseParamIndex(Long responseParamIndex) {
		this.responseParamIndex = responseParamIndex;
	}
	
	@XmlTransient
	public String getHttpAuthDriverId() {
		return httpAuthDriverId;
	}
	
	public void setHttpAuthDriverId(String httpAuthDriverId) {
		this.httpAuthDriverId = httpAuthDriverId;
	}
	
	@XmlTransient
	public LogicalNameValuePoolData getNameValuePoolData() {
		return nameValuePoolData;
	}
	
	public void setNameValuePoolData(LogicalNameValuePoolData nameValuePoolData) {
		this.nameValuePoolData = nameValuePoolData;
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
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Response Parameter Index", responseParamIndex);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);

		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
}
