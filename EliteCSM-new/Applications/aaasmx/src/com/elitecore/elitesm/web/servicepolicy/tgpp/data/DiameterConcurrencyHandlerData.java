package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "dia-concurrency-handler")
@ValidObject
@XmlType(propOrder = {"ruleSet", "diaConcurrencyId"})
public class DiameterConcurrencyHandlerData extends DiameterApplicationHandlerDataSupport implements Validator {
	
	@NotEmpty(message = "Specify Diameter Concurrency policy name in Concurrency Handler")
	private String diaConcurrencyId;
	private String ruleSet = "";
	
	@XmlElement(name = "dia-concurrency")
	public String getDiaConcurrencyId() {
		return diaConcurrencyId;
	}

	public void setDiaConcurrencyId(String diaConcurrencyId) {
		this.diaConcurrencyId = diaConcurrencyId;
	}

	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("Enabled", getEnabled());
		if ( Strings.isNullOrEmpty(ruleSet) == false )
			jsonObject.put("Ruleset", ruleSet);

		jsonObject.put("Diameter Concurrency Policy",diaConcurrencyId);
		return jsonObject;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if( Strings.isNullOrBlank(diaConcurrencyId) == false ){
			try {
				DiameterConcurrencyBLManager concurrencyBLManager = new DiameterConcurrencyBLManager();
				concurrencyBLManager.getDiameterConcurrencyDataByName(diaConcurrencyId);
			} catch(Exception e) {
				RestUtitlity.setValidationMessage(context, "Configured Diameter Concurrency is invalid in Diameter Concurrency handler");
				isValid = false;
			}
		}
		
		return isValid;
	}
}
