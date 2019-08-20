package com.elitecore.elitesm.datamanager.diameter.sessionmanager.data;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.DiameterSessionManagerStringAndTimestampDataTypeAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"dbFieldName","referringAttr","dataType","defaultValue"})
@ValidObject
public class SessionManagerFieldMappingData extends BaseData implements Differentiable,Validator{

	 private String dbFieldMapId;
	 
	 private String dbFieldName;
	 
	 private String referringAttr;
	 private Long dataType;
	 private String defaultValue;
	 private String mappingId;
	 private Integer orderNumber;
	 
	@XmlTransient 
	public String getDbFieldMapId() {
		return dbFieldMapId;
	}
	public void setDbFieldMapId(String dbFieldMapId) {
		this.dbFieldMapId = dbFieldMapId;
	}
	
	@XmlElement(name = "db-field-name")
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	
	@XmlElement(name="referring-attribute")
	public String getReferringAttr() {
		return referringAttr;
	}
	public void setReferringAttr(String referringAttr) {
		this.referringAttr = referringAttr;
	}
	
	@XmlElement(name="data-type")
	@XmlJavaTypeAdapter(value = DiameterSessionManagerStringAndTimestampDataTypeAdapter.class)
	public Long getDataType() {
		return dataType;
	}
	public void setDataType(Long dataType) {
		this.dataType = dataType;
	}
	
	@XmlElement(name ="default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@XmlTransient
	public String getMappingId() {
		return mappingId;
	}
	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
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
		innerObject.put("Referring Attribute", referringAttr);
		innerObject.put("Data Type", (dataType.equals(0L)) ? "String" :"Timestamp");
		innerObject.put("Default value", defaultValue);
		object.put(dbFieldName, innerObject);
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;
		if((this.dataType == null) && Strings.isNullOrEmpty(this.referringAttr) && Strings.isNullOrEmpty(this.dbFieldName)){
			RestUtitlity.setValidationMessage(context, "In Mapping List, db-field-name must be specified");
			isValid = false;
		}else if (Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(this.referringAttr) == false) {
			RestUtitlity.setValidationMessage(context, "In Mapping List, db-field-name must be specified for referring-attribute:[" + this.referringAttr + "].");
			isValid = false;
			
			if(this.dataType == null){
				RestUtitlity.setValidationMessage(context, "In Mapping List, db-type field must be specified for referring-attribute:[" + this.referringAttr + "].");
				isValid = false;
			}
		}  else if(Strings.isNullOrEmpty(this.dbFieldName) && (this.dataType != null)){
			
			String strDataType = null;
			if(this.dataType == 0){
				strDataType = "String";
			} else if(this.dataType == 1){
				strDataType = "Timestamp";
			}
			
			if(Strings.isNullOrBlank(strDataType) == false){
				RestUtitlity.setValidationMessage(context, "In Mapping List, db-field-name must be specified for data-type:[" + strDataType + "].");
				isValid = false;
				if(Strings.isNullOrBlank(referringAttr)){
					RestUtitlity.setValidationMessage(context, "In Mapping List, referring-attribute must be specified for data-type:[" + strDataType + "].");
					isValid = false;
				}
			}
		} 
		return isValid;
	}
}
