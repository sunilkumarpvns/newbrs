package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterConcurrencyHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterFilteredHandler;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "dia-concurrency-handler")
public class DiameterConcurrencyHandlerData extends DiameterApplicationHandlerDataSupport {
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
	public void postRead() {
		super.postRead();
	}

	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(DiameterServiceContext context) {
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> diameterConcurrencyHandler = new DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse>(context,diaConcurrencyId);  
		DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(getRuleSet(), diameterConcurrencyHandler);
		return filteredHandler;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Diameter Concurrency Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Ruleset", getRuleSet()));
		out.println(format("%-30s: %s", "Diameter Concurrency Policy Id", getDiaConcurrencyId()));
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

		jsonObject.put("Diameter Concurrency Policy",diaConcurrencyId);
		return jsonObject;
	}
}
