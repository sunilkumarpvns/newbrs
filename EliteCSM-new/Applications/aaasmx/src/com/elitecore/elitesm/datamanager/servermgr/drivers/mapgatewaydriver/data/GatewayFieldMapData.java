package com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
@XmlType(propOrder={"logicalName","profileField","defaultValue","valueMapping"})
@XmlRootElement(name = "map-profile-field")
public class GatewayFieldMapData extends BaseData implements Differentiable{
	private String profileFieldMapId;
	private String logicalName;
	private String profileField;
	private String mapAuthId;
	private String defaultValue;
	private String valueMapping;
	private LogicalNameValuePoolData nameValuePoolData;
	private ProfileFieldValuePoolData profileFieldValuePoolData;
	private Integer orderNumber;
	
	public GatewayFieldMapData(){};
	
	
	public GatewayFieldMapData(String defaultValue, String valueMapping,
			LogicalNameValuePoolData nameValuePoolData,
			ProfileFieldValuePoolData profileFieldValuePoolData) {
		this.defaultValue = defaultValue;
		this.valueMapping = valueMapping;
		this.nameValuePoolData = nameValuePoolData;
		this.profileFieldValuePoolData = profileFieldValuePoolData;
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
	public String getProfileFieldMapId() {
		return profileFieldMapId;
	}
	public void setProfileFieldMapId(String profileFieldMapId) {
		this.profileFieldMapId = profileFieldMapId;
	}
	
	@XmlElement(name = "logical-name")
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	@XmlElement(name = "profile-field")
	public String getProfileField() {
		return profileField;
	}
	public void setProfileField(String profileField) {
		this.profileField = profileField;
	}
	
	@XmlTransient
	public String getMapAuthId() {
		return mapAuthId;
	}
	public void setMapAuthId(String mapAuthId) {
		this.mapAuthId = mapAuthId;
	}
	
	@XmlTransient
	public LogicalNameValuePoolData getNameValuePoolData() {
		return nameValuePoolData;
	}
	public void setNameValuePoolData(LogicalNameValuePoolData nameValuePoolData) {
		this.nameValuePoolData = nameValuePoolData;
	}
	
	@XmlTransient
	public ProfileFieldValuePoolData getProfileFieldValuePoolData() {
		return profileFieldValuePoolData;
	}
	public void setProfileFieldValuePoolData(
			ProfileFieldValuePoolData profileFieldValuePoolData) {
		this.profileFieldValuePoolData = profileFieldValuePoolData;
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
		innerObject.put("Profile Field", profileField);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);

		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
}