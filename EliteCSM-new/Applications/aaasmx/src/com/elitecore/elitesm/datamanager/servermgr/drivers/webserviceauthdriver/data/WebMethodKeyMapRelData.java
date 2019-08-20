package com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;

import net.sf.json.JSONObject;
@XmlType(propOrder = { "logicalName", "webMethodKey" , "defaultValue", "valueMapping" } )
@XmlRootElement(name = "web-method-key-mapping")
public class WebMethodKeyMapRelData extends BaseData implements Differentiable{
	private String keyMapId;
	private String logicalName;
	private String webMethodKey;
	private String defaultValue;
	private String valueMapping;
	private String wsAuthDriverId;
	private LogicalNameValuePoolData nameValuePoolData;
	private Integer orderNumber;
	
	@XmlTransient
	public String getKeyMapId() {
		return keyMapId;
	}
	public void setKeyMapId(String keyMapId) {
		this.keyMapId = keyMapId;
	}
	
	@XmlElement(name = "logical-name")
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	@XmlElement(name = "webservice-method-key")
	public String getWebMethodKey() {
		return webMethodKey;
	}
	public void setWebMethodKey(String webMethodKey) {
		this.webMethodKey = webMethodKey;
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
	public String getWsAuthDriverId() {
		return wsAuthDriverId;
	}
	public void setWsAuthDriverId(String wsAuthDriverId) {
		this.wsAuthDriverId = wsAuthDriverId;
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
		innerObject.put("WebService Method Key", webMethodKey);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);

		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
}
