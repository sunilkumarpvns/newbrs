package com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data;

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
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlRootElement(name = "classic-csv-field-mapping")
@XmlType(propOrder = {"header", "attributeids", "defaultvalue", "usedictionaryvalue"})
@ValidObject
public class ClassicCSVAttrRelationData extends BaseData implements IClassicCSVAttrRelData,Differentiable,Validator{
	
	private String attrRelId;	
	private String header;
	
	private String attributeids;
	private String defaultvalue;
	private String usedictionaryvalue;
	private String classicCsvId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getAttrRelId() {
		return attrRelId;
	}
	public void setAttrRelId(String attrRelId) {
		this.attrRelId = attrRelId;
	}
	
	@XmlElement(name = "header")
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	
	@XmlElement(name = "attribute-id")
	public String getAttributeids() {
		return attributeids;
	}
	public void setAttributeids(String attributeids) {
		this.attributeids = attributeids;
	}
	
	@XmlElement(name = "default-value")
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	
	@XmlElement(name = "use-dictionary-value")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getUsedictionaryvalue() {
		return usedictionaryvalue;
	}
	public void setUsedictionaryvalue(String usedictionaryvalue) {
		this.usedictionaryvalue = usedictionaryvalue;
	}
	
	@XmlTransient
	public String getClassicCsvId() {
		return classicCsvId;
	}
	public void setClassicCsvId(String classicCsvId) {
		this.classicCsvId = classicCsvId;
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

		if (Strings.isNullOrEmpty(this.usedictionaryvalue) && Strings.isNullOrEmpty(this.attributeids) == false) {
			
			if(Strings.isNullOrEmpty(this.header) == false ) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value  must be specified for the header ["+ this.header +"].");
			} else if (Strings.isNullOrEmpty(this.attributeids) == false ) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value  must be specified for the attribute-id ["+ this.attributeids +"].");
			} else {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value  must be specified.");
			}
			
			isValid = false;
			return isValid;

		}else if (Strings.isNullOrEmpty(this.attributeids) && Strings.isNullOrEmpty(this.usedictionaryvalue)) {
			if(Strings.isNullOrEmpty(this.header) == false ) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must and use-dictionary-value must be specified for header :[" + this.header + "].");
			} else {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id and use-dictionary-value must be specified.");
			}	
			isValid = false;
			return isValid;

		} else if (Strings.isNullOrEmpty(this.attributeids) && Pattern.matches("true|false", this.usedictionaryvalue)) {
			
			if(Strings.isNullOrEmpty(this.header) == false ) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must be specified for header :[" + this.header + "].");
			} else {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must be specified.");
			}
			
			isValid = false;
			return isValid;

		}	else if (Strings.isNullOrEmpty(this.attributeids) == false && Pattern.matches("true|false", this.usedictionaryvalue) == false) {
			
			if(Strings.isNullOrEmpty(this.header) ==  false ) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value must be specified for header:[" + this.header + "] and it can be only 'true' or 'false' ");
			}else {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the use-dictionary-value must be specified for attribute-id:[" + this.attributeids + "] and it can be only 'true' or 'false' ");
			}
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Header", header);
		innerObject.put("Default Value", defaultvalue);
		innerObject.put("Use Dictionary Value", usedictionaryvalue);

		if(attributeids!=null){
			object.put(attributeids, innerObject);
		}
		return object;
	}
	
}
