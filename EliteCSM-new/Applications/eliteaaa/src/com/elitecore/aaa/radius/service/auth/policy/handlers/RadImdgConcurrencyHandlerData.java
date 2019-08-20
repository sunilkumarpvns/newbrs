package com.elitecore.aaa.radius.service.auth.policy.handlers;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.commons.base.Strings;

import net.sf.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

@XmlRootElement(name = "rad-imdg-concurrency-handler")
public class RadImdgConcurrencyHandlerData  extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData {

	private String concurrencyIdentityField; 
	private String ruleSet = "";

	@XmlElement(name = "concurrency-identity-field")
	public String getConcurrencyIdentityField() {
		return concurrencyIdentityField;
	}

	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
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

		jsonObject.put("Enabled", isEnabled());
		if (Strings.isNullOrEmpty(ruleSet) == false) {
			jsonObject.put("Ruleset", ruleSet);
		}
		jsonObject.put("Index", concurrencyIdentityField);
		return jsonObject;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		RadImdgConcurrencyHandler radImdgConcurrencyHandler = new RadImdgConcurrencyHandler(serviceContext, this);
		return new AuthFilteredHandler(getRuleSet(), radImdgConcurrencyHandler);
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Radius Concurrency IMDG Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Ruleset", getRuleSet()));
		out.println(format("%-30s: %s", "Concurrency Identity Field ", getConcurrencyIdentityField()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

}
