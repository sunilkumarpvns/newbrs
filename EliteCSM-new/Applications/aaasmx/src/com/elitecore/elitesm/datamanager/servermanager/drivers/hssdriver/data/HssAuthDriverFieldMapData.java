package com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;

import net.sf.json.JSONObject;
@XmlRootElement(name = "hss-driver-field-mapping")
@XmlType(propOrder = {"logicalName" , "attributeIds", "defaultValue", "valueMapping" })
public class HssAuthDriverFieldMapData extends BaseData implements Serializable,Differentiable{
	
	private String dbFieldMapId;
	private String logicalName;
	private String attributeIds;
	private String hssauthdriverid;
	private String defaultValue;
	private String valueMapping;
	private LogicalNameValuePoolData nameValuePoolData;
	private Integer orderNumber;
	
	@XmlTransient
	public String getDbFieldMapId() {
		return dbFieldMapId;
	}
	public void setDbFieldMapId(String dbFieldMapId) {
		this.dbFieldMapId = dbFieldMapId;
	}
	
	@XmlElement(name = "logical-name")
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlElement(name ="value-mapping")
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	@XmlTransient
	public LogicalNameValuePoolData getNameValuePoolData() {
		return nameValuePoolData;
	}
	public void setNameValuePoolData(LogicalNameValuePoolData nameValuePoolData) {
		this.nameValuePoolData = nameValuePoolData;
	}
	
	@XmlTransient
	public String getHssauthdriverid() {
		return hssauthdriverid;
	}
	public void setHssauthdriverid(String hssauthdriverid) {
		this.hssauthdriverid = hssauthdriverid;
	}
	
	@XmlElement(name = "attribute-ids")
	public String getAttributeIds() {
		return attributeIds;
	}
	public void setAttributeIds(String attributeIds) {
		this.attributeIds = attributeIds;
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
		innerObject.put("Attributes Ids", attributeIds);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);
		
		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
	
}
