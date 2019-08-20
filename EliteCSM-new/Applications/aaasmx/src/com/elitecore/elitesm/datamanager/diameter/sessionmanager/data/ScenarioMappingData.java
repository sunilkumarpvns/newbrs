/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.sessionmanager.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlType(propOrder = {"name","description","ruleset","criteria","mappingName"})   
@ValidObject
public class ScenarioMappingData extends BaseData implements Differentiable,Serializable,Validator{

	private static final long serialVersionUID = 1L;
	private String scenarioId;

	private String ruleset;

	private String mappingName;

	private String criteria;
	private String sessionManagerId;

	private String name;
	private String description;
	
	private Integer orderNumber;

	@XmlTransient
	public String getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "mapping-name")
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	@XmlTransient
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "criteria")
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		JSONObject object = new  JSONObject();
		JSONObject innerObject = new JSONObject();
		innerObject.put("Ruleset", ruleset);
		innerObject.put("Mapping Name", mappingName);
		innerObject.put("Description", description);
		innerObject.put("Criteria", criteria);
		object.put(name, innerObject);
		return object;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if(Strings.isNullOrEmpty(this.ruleset) && Strings.isNullOrEmpty(this.mappingName) && Strings.isNullOrEmpty(this.criteria) && Strings.isNullOrEmpty(this.name)){
			RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, name field must be specified");
			isValid = false;
		}else if (Strings.isNullOrEmpty(this.ruleset)== false && Strings.isNullOrEmpty(this.name)) {
			RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, name field must be specified for ruleset:[" + this.ruleset + "].");
			isValid = false;
			
			if(Strings.isNullOrBlank(criteria)){
				RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, criteria field must be specified for ruleset:[" + this.ruleset + "].");
				isValid = false;
			}
			if(Strings.isNullOrBlank(mappingName)){
				RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, mappingName field must be specified for ruleset:[" + this.ruleset + "].");
				isValid = false;
			}
		} else if(Strings.isNullOrEmpty(this.mappingName)== false && Strings.isNullOrEmpty(this.name)){
			RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, name field must be specified for mapping-name:[" + this.mappingName + "].");
			isValid = false;
			
			if(Strings.isNullOrBlank(ruleset)){
				RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, ruleset field must be specified for mapping-name:[" + this.mappingName + "].");
				isValid = false;
			}
			if(Strings.isNullOrBlank(criteria)){
				RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, criteria field must be specified for mapping-name:[" + this.mappingName + "].");
				isValid = false;
			}
		} else if(Strings.isNullOrEmpty(this.criteria)== false && Strings.isNullOrEmpty(this.name)){
			RestUtitlity.setValidationMessage(context, "In Scenario Mapping List, name field must be specified for criteria:[" + this.criteria + "].");
			isValid = false;
		}
		return isValid;
	}
}
