package com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data;

import java.util.HashSet;
import java.util.Set;
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
@XmlRootElement(name = "classic-csv-strip-pattern-relation-mapping")
@XmlType(propOrder = {"attributeid", "pattern", "separator"})
@ValidObject
public class ClassicCSVStripPattRelData extends BaseData implements Differentiable,Validator{
	
	private static final String SUFFIX_PREFIX = "suffix|prefix";
	private String id;	
	private String attributeid;
	private String pattern;
	private String separator;
	private String classicCsvId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "attribute-id")
	public String getAttributeid() {
		return attributeid;
	}
	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}
	
	@XmlElement(name = "pattern")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@XmlElement(name = "separator")
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
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
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Pattern", pattern);
		innerObject.put("Seperator", separator);
		object.put(attributeid, innerObject);
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(Strings.isNullOrEmpty(this.pattern) && Strings.isNullOrEmpty(this.attributeid) == false) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the pattern  must be specified for attribute id : [ "+this.attributeid+" ].");
			isValid = false;
			return isValid;
		}
		
		if (Strings.isNullOrEmpty(this.attributeid) && Strings.isNullOrEmpty(this.pattern)) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id and pattern must be specified.");
			isValid = false;
			return isValid;

		} else if (Strings.isNullOrEmpty(this.attributeid) && Pattern.matches(SUFFIX_PREFIX, this.pattern)) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must be specified ");
			isValid = false;
			return isValid;

		}	else if (Strings.isNullOrEmpty(this.attributeid) == false &&  Pattern.matches(SUFFIX_PREFIX, this.pattern) == false) {
			RestUtitlity.setValidationMessage(context, "In the Mapping List, the pattern must be specified for attribute-id:[" + this.attributeid + "] and it can be only 'suffix' or 'prefix'.");
			isValid = false;
			return isValid;
		}
		
		Set<String> checkDuplicateAttributesIds = new HashSet<String>();
		isValid = checkDuplicateAttributesIds.add(this.attributeid);
		if(isValid == false){
			RestUtitlity.setValidationMessage(context,"Mapping with Attribute Ids "+this.attributeid+" exits multiple times");
			return isValid;
		}
		
		return isValid;
	}
}
