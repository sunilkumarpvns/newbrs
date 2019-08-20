package com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
@XmlRootElement(name="ldap-field-mapping")
@XmlType(propOrder = { "logicalName", "ldapAttribute", "defaultValue", "valueMapping" })
public class LDAPAuthFieldMapData extends BaseData implements ILDAPAuthFieldMapData,Differentiable{
	
	private String ldapFieldMapId;
	private String logicalName;
	private String ldapAttribute;
	private String ldapDriverId;
	private String defaultValue;
	private String valueMapping;
	private LogicalNameValuePoolData nameValuePoolData;
	private Integer orderNumber;
	
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
	public String getLdapFieldMapId() {
		return ldapFieldMapId;
	}
	public void setLdapFieldMapId(String ldapFieldMapId) {
		this.ldapFieldMapId = ldapFieldMapId;
	}
	
	@XmlElement(name = "logical-name")
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	@XmlElement(name = "ldap-attribute")
	public String getLdapAttribute() {
		return ldapAttribute;
	}
	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}
	
	@XmlTransient
	public String getLdapDriverId() {
		return ldapDriverId;
	}
	public void setLdapDriverId(String ldapDriverId) {
		this.ldapDriverId = ldapDriverId;
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
		innerObject.put("Ldap Attribute", ldapAttribute);
		innerObject.put("Default Value", defaultValue);
		innerObject.put("Value Mapping", valueMapping);
		
		if(logicalName!=null){
			object.put(logicalName, innerObject);
		}
		return object;
	}
}