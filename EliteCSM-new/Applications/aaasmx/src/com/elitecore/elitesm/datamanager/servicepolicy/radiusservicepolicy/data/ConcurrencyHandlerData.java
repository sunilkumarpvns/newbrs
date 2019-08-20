package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "concurrency-handler")
@XmlType(propOrder = {"ruleSet", "sessionManagerId"})
@ValidObject
public class ConcurrencyHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData, Validator {

	private String sessionManagerId;
	private String ruleSet = "";
	
	@NotEmpty(message = "Session Manager must be specified in Concurrency handler of Authentication Flow")
	@XmlElement(name = "session-manager")
	public String getSessionManagerId() {
		return sessionManagerId;
	}

	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Concurrency Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Ruleset", getRuleSet()));
		out.println(format("%-30s: %s", "Session Manager Id", getSessionManagerId()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("Enabled", getEnabled());
		if ( Strings.isNullOrEmpty(ruleSet) == false )
			jsonObject.put("Ruleset", ruleSet);

		jsonObject.put("Session Manager",sessionManagerId);
		return jsonObject;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if( Strings.isNullOrBlank(sessionManagerId) == false ){
			try{
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				sessionManagerBLManager.getSessionManagerDataByName(sessionManagerId);
			}catch(Exception e){
				RestUtitlity.setValidationMessage(context, "Configured Session Manager is invalid in Concurrency handler of Authentication Flow");
				isValid = false;
			}
		}
		
		return isValid;
	}

	@Override
	public RadAcctServiceHandler createHandler(
			RadAcctServiceContext serviceContext) {
		return null;
	}
}
