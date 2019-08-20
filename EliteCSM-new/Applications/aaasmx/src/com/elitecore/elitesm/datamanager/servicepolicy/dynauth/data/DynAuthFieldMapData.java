package com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "attribute-mapping")
@XmlType(propOrder = {"attributeid", "dbfield", "defaultvalue", "mandatory"})
@ValidObject
public class DynAuthFieldMapData extends BaseData implements Serializable, Differentiable, Validator {

	private static final long serialVersionUID = 1L;
	private String dynAuthFieldMapId;
	private String dynAuthPolicyId;
	private String attributeid;
	private String dbfield;
	private String defaultvalue;
	private Integer orderNumber;
	
	@NotEmpty(message = "Mandatory field of DB Field Mapping must be specified")
	@Pattern(regexp = "true|false", message = "Invalid value of Mandatory field of DB Field Mapping. Value could be 'True' or 'False'.")
	private String mandatory;
	
	@XmlTransient
	public String getDynAuthFieldMapId() {
		return dynAuthFieldMapId;
	}

	public void setDynAuthFieldMapId(String dynAuthFieldMapId) {
		this.dynAuthFieldMapId = dynAuthFieldMapId;
	}

	@XmlTransient
	public String getDynAuthPolicyId() {
		return dynAuthPolicyId;
	}

	public void setDynAuthPolicyId(String dynAuthPolicyId) {
		this.dynAuthPolicyId = dynAuthPolicyId;
	}

	@XmlElement(name = "attribute-ids")
	public String getAttributeid() {
		return attributeid;
	}
	
	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}
	
	@XmlElement(name = "db-field")
	public String getDbfield() {
		return dbfield;
	}
	
	public void setDbfield(String dbfield) {
		this.dbfield = dbfield;
	}

	@XmlElement(name = "default-value")
	public String getDefaultvalue() {
		return defaultvalue;
	}
	
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	
	@XmlElement(name = "mandatory")
	public String getMandatory() {
		return mandatory;
	}
	
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory.toLowerCase();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Attribute Ids", attributeid);
		object.put("DB Field", dbfield);
		object.put("Default Value", defaultvalue);
		object.put("Mandatory", mandatory);
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (Strings.isNullOrEmpty(attributeid) && Strings.isNullOrEmpty(dbfield)) {
			RestUtitlity.setValidationMessage(context, "Attribute Ids and Db Field value must be specified");
			isValid = false;
		} else if (Strings.isNullOrEmpty(attributeid)) {
			RestUtitlity.setValidationMessage(context, "Attribute Ids value must be specified for Db Field:[" + dbfield + "].");
			isValid = false;
		} else if (Strings.isNullOrEmpty(dbfield)) {
			RestUtitlity.setValidationMessage(context, "Db Field value must be specified for Attribute Id:[" + attributeid + "].");
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
