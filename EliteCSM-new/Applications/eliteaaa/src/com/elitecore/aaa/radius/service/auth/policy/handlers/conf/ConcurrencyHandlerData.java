package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthFilteredHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.ConcurrencyHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "concurrency-handler")
public class ConcurrencyHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData {

	private String sessionManagerId;
	private String ruleSet = "";
	
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
		ConcurrencyHandler concurrencyHandler = new ConcurrencyHandler(serviceContext, this);
		AuthFilteredHandler filteredHandler = new AuthFilteredHandler(getRuleSet(), concurrencyHandler);
		return filteredHandler;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Concurrency Handler | Enabled: %s", 10, ' '), isEnabled()));
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

		jsonObject.put("Enabled", isEnabled());
		if ( Strings.isNullOrEmpty(ruleSet) == false )
			jsonObject.put("Ruleset", ruleSet);

		jsonObject.put("Session Manager",sessionManagerId);
		return jsonObject;
	}
}
