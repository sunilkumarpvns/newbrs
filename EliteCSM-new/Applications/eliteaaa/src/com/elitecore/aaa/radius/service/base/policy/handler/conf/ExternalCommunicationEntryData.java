package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;


public class ExternalCommunicationEntryData implements Differentiable{
	private String ruleset;
	private String translationMapping;
	private String script;
	private boolean acceptOnTimeout;
	private CommunicatorGroupData communicatorGroupData;
	
	/* Transient fields */
	private String policyName;

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "script")
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}

	@XmlAttribute(name = "accept-on-timeout")
	public boolean isAcceptOnTimeout() {
		return acceptOnTimeout;
	}
	public void setAcceptOnTimeout(boolean acceptOnTimeout) {
		this.acceptOnTimeout = acceptOnTimeout;
	}

	@XmlElement(name = "communicator-group")
	public CommunicatorGroupData getCommunicatorGroupData() {
		return communicatorGroupData;
	}

	public void setCommunicatorGroupData(CommunicatorGroupData serverGroupData) {
		this.communicatorGroupData = serverGroupData;
	}
	
	@XmlElement(name = "translation-mapping")
	public String getTranslationMapping() {
		return translationMapping;
	}
	public void setTranslationMapping(String translationMapping) {
		this.translationMapping = translationMapping;
	}

	@XmlTransient
	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
		out.println(format("%-30s: %s", "Translation Mapping",
				getTranslationMapping() != null ? getTranslationMapping() : ""));
		out.println(format("%-30s: %s", "Script", getScript() != null ? getScript() : ""));
		out.println(format("%-30s: %s", "Accept on Timeout", isAcceptOnTimeout()));
		out.close();
		return writer.toString();
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Rule Set", ruleset.trim());
		object.put("Server/Group", communicatorGroupData.toJson());
		object.put("Translation Mapping", translationMapping);
		object.put("Script", script.trim());
		object.put("Accept On Timeout", acceptOnTimeout);
		return object;
	}
}
