package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;

@XmlRootElement(name = "db-field-mapping")
@XmlType(propOrder = {"logicalName", "dbField", "defaultValue", "valueMapping"})
public class DBAuthFieldMapData extends BaseData implements IDBAuthFieldMapData,Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String dbFieldMapId;
	private String logicalName;
	private String dbField;
	private String dbAuthId;
	private String defaultValue;
	private String valueMapping;
	private LogicalNameValuePoolData nameValuePoolData;
	private Integer orderNumber;
	
	@XmlElement(name = "default-value")
	public String getDefaultValue() {
	    return this.defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
        this.defaultValue=defaultValue;
	}
	
	@XmlElement(name = "value-mapping")
	public String getValueMapping() {
        return this.valueMapping;
	}
	public void setValueMapping(String valueMapping) {
        this.valueMapping=valueMapping;
	}
	
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
	
	@XmlElement(name = "db-field")
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	
	@XmlTransient
	public String getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
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
		innerObject.put("DB Field", dbField);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);
		
		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
}