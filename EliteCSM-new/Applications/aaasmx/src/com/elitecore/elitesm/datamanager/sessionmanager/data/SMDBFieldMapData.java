package com.elitecore.elitesm.datamanager.sessionmanager.data;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.StringAndTimestampDataTypeAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
public class SMDBFieldMapData extends BaseData implements ISMDBFieldMapData,Differentiable,Validator{
	
	private String dbFieldMapId;
	private String smConfigId;
	
	private String dbFieldName;
	
	private String referringEntity;
	
	private Integer dataType;
	private String defaultValue;
	private String field;
	private Integer orderNumber;

	@XmlElement(name="field")
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	@XmlTransient
	public String getDbFieldMapId() {
		return dbFieldMapId;
	}
	public void setDbFieldMapId(String dbFieldMapId) {
		this.dbFieldMapId = dbFieldMapId;
	}
	
	@XmlTransient
	public String getSmConfigId() {
		return smConfigId;
	}
	public void setSmConfigId(String smConfigId) {
		this.smConfigId = smConfigId;
	}
	
	@XmlElement(name="db-field-name")
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	
	@XmlElement(name="referring-entity")
	public String getReferringEntity() {
		return referringEntity;
	}
	public void setReferringEntity(String referringEntity) {
		this.referringEntity = referringEntity;
	}
	
	@XmlElement(name="data-type")
	@XmlJavaTypeAdapter(value = StringAndTimestampDataTypeAdapter.class)
	public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	
	@XmlElement(name="default-value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();

		JSONObject innerObject = new JSONObject();
		innerObject.put("Referring Attribute", referringEntity);
		if(dataType == 0){
			innerObject.put("Data Type", "String");
		}else if(dataType == 1){
			innerObject.put("Data Type", "Timestamp");
		}
		innerObject.put("Default Value", defaultValue);
	
		if(field != null){
			innerObject.put("DB Field Name", dbFieldName);
			object.put(field, innerObject);
		} else if(dbFieldName != null) {
			object.put(dbFieldName, innerObject);
		}
		
		return object;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if (Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(this.field) == false) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for field:[" + this.field + "].");
			isValid = false;
		} else if(Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(this.referringEntity) == false){
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for referring-attribute:[" + this.referringEntity + "].");
			isValid = false;
		} else if(Strings.isNullOrEmpty(this.dbFieldName) && (this.dataType == null) == false){
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for data-type:[" + this.dataType + "].");
			isValid = false;
		} else if(Strings.isNullOrEmpty(this.dbFieldName)){
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified ");
			isValid = false;
		}
		return isValid;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
