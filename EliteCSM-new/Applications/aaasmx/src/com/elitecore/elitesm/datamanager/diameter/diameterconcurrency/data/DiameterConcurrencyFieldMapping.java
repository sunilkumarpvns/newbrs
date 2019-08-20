package com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.StringDataTypeAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlType(propOrder = {"logicalField","dbFieldName","referringAttribute","dataType","defaultValue","includeInASR"})
@ValidObject
public class DiameterConcurrencyFieldMapping extends BaseData implements Differentiable, Validator {
	
	public DiameterConcurrencyFieldMapping(){
		includeInASR = "false";
	}
	private String dbFieldMappingId;
	
	private String logicalField;
	
	private String dbFieldName;
	
	private String referringAttribute;
	
	private Long dataType;
	private String defaultValue;
	private String includeInASR;
	private String diaConConfigId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getDbFieldMappingId() {
		return dbFieldMappingId;
	}

	public void setDbFieldMappingId(String dbFieldMappingId) {
		this.dbFieldMappingId = dbFieldMappingId;
	}

	@XmlElement(name="logical-field")
	public String getLogicalField() {
		return logicalField;
	}

	public void setLogicalField(String logicalField) {
		this.logicalField = logicalField;
	}

	@XmlElement(name="db-field-name")
	public String getDbFieldName() {
		return dbFieldName;
	}

	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}

	@XmlElement(name="referring-attribute")
	public String getReferringAttribute() {
		return referringAttribute;
	}

	public void setReferringAttribute(String referringAttribute) {
		this.referringAttribute = referringAttribute;
	}

	@XmlElement(name = "data-type")
	@XmlJavaTypeAdapter(value = StringDataTypeAdapter.class)
	public Long getDataType() {
		return dataType;
	}

	public void setDataType(Long dataType) {
		this.dataType = dataType;
	}

	@XmlElement(name="default-value")
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@XmlElement(name="include-in-asr")
	@Pattern(regexp = "true|false", message = "Invalid value of include In ASR. Value could be 'true' or 'false'.")
	public String getIncludeInASR() {
		return includeInASR;
	}

	public void setIncludeInASR(String includeInASR) {
		this.includeInASR = includeInASR;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Logical Field", logicalField);
		object.put("DB Field Name", dbFieldName);
		object.put("Referring Attribute", referringAttribute);
		object.put("Data Type", "String");
		object.put("Default Value",(defaultValue != null && defaultValue.length() > 0) ? defaultValue : "");
		object.put("Include In ASR", includeInASR);
		return object;
	}

	@XmlTransient
	public String getDiaConConfigId() {
		return diaConConfigId;
	}

	public void setDiaConConfigId(String diaConConfigId) {
		this.diaConConfigId = diaConConfigId;
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if(Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(referringAttribute) && Strings.isNullOrEmpty(this.logicalField) == false){
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field and referring-attribute must be specified for logical-name:[" + this.logicalField + "].");
			isValid = false;
		}else if (Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(this.logicalField) == false) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for logical-name:[" + this.logicalField + "].");
			isValid = false;
		} else if(Strings.isNullOrEmpty(this.dbFieldName) && Strings.isNullOrEmpty(this.referringAttribute) == false){
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for referring-attribute:[" + this.referringAttribute + "].");
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
}
