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
import javax.validation.constraints.Pattern;
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

@XmlType(propOrder = {"name","description","ruleset","actions"})   
@ValidObject
public class SessionOverideActionData extends BaseData implements Differentiable,Serializable,Validator{

	private static final long serialVersionUID = 1L;
	private String overrideId;

	private String ruleset;

	@Pattern(regexp = "^$|NONE|INSERT|DELETE|UPDATE", message = "Invalid Session Overide Action. It can be NONE, INSERT, DELETE or UPDATE.")
	private String actions;
	private String sessionManagerId;

	private String name;
	private String description;
	private Integer orderNumber;

	@XmlTransient
	public String getOverrideId() {
		return overrideId;
	}
	public void setOverrideId(String overrideId) {
		this.overrideId = overrideId;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlTransient
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "actions")
	public String getActions() {
		return actions;
	}
	public void setActions(String actions) {
		this.actions = actions;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
		innerObject.put("Ruleset", ruleset);
		innerObject.put("Action", actions);
		innerObject.put("Description", description);
		object.put(name, innerObject);
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if(Strings.isNullOrEmpty(this.name) && Strings.isNullOrEmpty(this.actions) && Strings.isNullOrEmpty(this.ruleset)){
			RestUtitlity.setValidationMessage(context, "In Session Override Action, name must be specified ");
			isValid = false;
		}else if(Strings.isNullOrEmpty(this.name) && Strings.isNullOrEmpty(this.actions) == false){
			RestUtitlity.setValidationMessage(context, "In Session Override Action, name field must be specified for actions:[" + this.actions + "].");
			isValid = false;
			
			if(Strings.isNullOrBlank(ruleset)){
				RestUtitlity.setValidationMessage(context, "In Session Override Action, ruleset field must be specified for actions:[" + this.actions + "].");
				isValid = false;
			}
		} else if(Strings.isNullOrEmpty(this.name) && Strings.isNullOrEmpty(this.ruleset) == false){
			RestUtitlity.setValidationMessage(context, "In Session Override Action, name field must be specified for ruleset:[" + this.ruleset + "].");
			isValid = false;
			
			if(Strings.isNullOrBlank(actions)){
				RestUtitlity.setValidationMessage(context, "In Session Override Action, actions field must be specified for ruleset:[" + this.ruleset + "].");
				isValid = false;
			}
		}
		return isValid;
	}
}
