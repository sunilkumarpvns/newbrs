package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.InitCapCaseAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlRootElement(name = "db-acct-driver-mapping-detail")
@XmlType (propOrder = {"attributeids", "dbfield", "datatype", "defaultvalue", "useDictionaryValue" })
@ValidObject
public class DBAcctFeildMapData extends BaseData implements IDBAcctFeildMapData, Differentiable, Validator{
	
	private String dbAcctFeildMapId;
	private String attributeids;
	private String dbfield;
	private String datatype;
	private String defaultvalue;
	private String useDictionaryValue;
	private String openDbAcctId;
	private Integer orderNumber;
	
	public DBAcctFeildMapData() {
	}
	
	@XmlTransient
	public String getDbAcctFeildMapId() {
		return dbAcctFeildMapId;
	}
	public void setDbAcctFeildMapId(String dbAcctFeildMapId) {
		this.dbAcctFeildMapId = dbAcctFeildMapId;
	}	
	
	@XmlElement(name = "attribute-id")
	public String getAttributeids() {
		return attributeids;
	}
	public void setAttributeids(String attributeids) {
		this.attributeids = attributeids;
	}
	
	@XmlElement(name = "db-field")
	public String getDbfield() {
		return dbfield;
	}
	public void setDbfield(String dbfield) {
		this.dbfield = dbfield;
	}
	
	@XmlElement(name = "data-type")
	@XmlJavaTypeAdapter(InitCapCaseAdapter.class)
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
	@XmlElement(name = "default-value")
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	
	@XmlElement(name = "use-dictionary-value")
	public String getUseDictionaryValue() {
		return useDictionaryValue;
	}
	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue.toLowerCase();
	}
	@XmlTransient
	public String getOpenDbAcctId() {
		return openDbAcctId;
	}
	public void setOpenDbAcctId(String openDbAcctId) {
		this.openDbAcctId = openDbAcctId;
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
		innerObject.put("Field Name",dbfield);
		innerObject.put("Data Type", datatype);
		innerObject.put("Default Value", defaultvalue);
		innerObject.put("Use Dictionary Value", useDictionaryValue);
		
		if(attributeids!=null){
			object.put(attributeids, innerObject);
		}
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if (Strings.isNullOrEmpty(this.attributeids) && Strings.isNullOrEmpty(this.dbfield)) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id and db-field must be specified.");
			isValid = false;

		} else if (Strings.isNullOrEmpty(this.attributeids) && Strings.isNullOrEmpty(this.dbfield) == false) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must be specified for db-field:[" + this.dbfield + "].");
			isValid = false;

		}	else if (Strings.isNullOrEmpty(this.attributeids) == false && Strings.isNullOrEmpty(this.dbfield)) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for attribute-id:[" + this.attributeids + "].");
			isValid = false;

		} else if (Strings.isNullOrEmpty(this.attributeids) == false && Strings.isNullOrEmpty(this.dbfield) == false ) {
			
			if (Strings.isNullOrEmpty(this.datatype)) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the data-type value for the db-field:[" + this.dbfield +"] must be specified and it can be 'String' or 'Date' only.");
				isValid = false;
			} else if (Pattern.matches("String|Date", this.datatype) == false) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the data-type value for the db-field:[" + this.dbfield +"] can be 'String' or 'Date' only.");
				isValid = false;
			}

			if (Strings.isNullOrEmpty(this.useDictionaryValue)) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value for the db-field:[" + this.dbfield +"] must be specified and it can be 'true' or 'false' only.");
				isValid = false;
			} else if (Pattern.matches("true|false", this.useDictionaryValue) == false) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value for the db-field:[" + this.dbfield + "] can be 'true' or 'false' only.");
				isValid = false;
			}

		}
		return isValid;
	}

}
