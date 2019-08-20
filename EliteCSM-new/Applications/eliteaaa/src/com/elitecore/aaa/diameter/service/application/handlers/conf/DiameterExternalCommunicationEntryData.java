package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.conf.impl.RadEsiGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.RadEsiGroupData;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

public class DiameterExternalCommunicationEntryData implements Differentiable {

	private String ruleset;
	private String translationMapping;
	private String script;
	private List<Integer> acceptableResultCodes = new ArrayList<Integer>();
	private String peerGroupId;

	/* Transient fields */
	private String policyName;
	private List<Integer> priorityResultCodes = new ArrayList<Integer>();
	private String peerGroupName;
	private String radEsiGroupName;
	private String protocol;

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

	@XmlElementWrapper(name = "acceptable-result-codes")
	@XmlElement(name = "acceptable-result-code")
	public List<Integer> getAcceptableResultCodes() {
		return acceptableResultCodes;
	}

	@XmlElement(name = "peer-group")
	public String getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
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

	@XmlTransient
	public List<Integer> getPriorityResultCodes() {
		return priorityResultCodes;
	}

	public void setPriorityResultCodes(List<Integer> priorityResultCodes) {
		this.priorityResultCodes = priorityResultCodes;
	}

	public int getPrioritizedResultCode(Integer existingResultCode, Integer remoteResultCode) {
		if(priorityResultCodes.isEmpty()) {
			return remoteResultCode;
		}
		
		int priorityExistingResultCode = priorityOf(existingResultCode);
		int priorityRemoteResultCode = priorityOf(remoteResultCode);

		if (priorityRemoteResultCode == -1) {
			return existingResultCode;
		}

		return priorityExistingResultCode > priorityRemoteResultCode ?
				existingResultCode : remoteResultCode;
	}

	public boolean isAcceptableResultCode(int resultCode) {
		return acceptableResultCodes.contains(resultCode) ? true : 
			acceptableResultCodes.contains(ResultCodeCategory.getResultCodeCategory(resultCode).value);
	}

	public void postRead(ConfigurationContext context) {
		if(ProtocolType.DIAMETER.name().equals(protocol)) {
			DiameterPeerGroupConfigurable diameterPeerGroupConfigurable = context.get(DiameterPeerGroupConfigurable.class);
			PeerGroupData peerGroupData = diameterPeerGroupConfigurable.getPeerGroup(getPeerGroupId());
			if (peerGroupData != null) {
				peerGroupName = peerGroupData.getName();
			}
		}
		/*
		 * temporary workaround to print proper logs at time of initialization
		 */
		else {
			RadEsiGroupConfigurable radEsiGroupConfigurable = context.get(RadEsiGroupConfigurable.class);
			RadEsiGroupData radEsiGroupData = radEsiGroupConfigurable.getRadEsiGroup(getPeerGroupId());
			if (radEsiGroupData != null) {
				radEsiGroupName = radEsiGroupData.getName();
			}
		}
	}

	/**
	 * @return the name of the peer group attached.
	 */
	@XmlTransient
	public @Nullable String getRadEsiGroupName() {
		return radEsiGroupName;
	}

	/**
	 * @return the name of the peer group attached.
	 */
	@XmlTransient
	public @Nullable String getPeerGroupName() {
		return peerGroupName;
	}

	public void setPeerGroupName(String peerGroupName) {
		this.peerGroupName = peerGroupName;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		if (ProtocolType.RADIUS.name().equals(protocol)) {
			out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
			out.println(format("%-30s: %s", "Translation Mapping",
					getTranslationMapping() != null ? getTranslationMapping() : ""));
			out.println(format("%-30s: %s", "Script", getScript() != null ? getScript() : ""));
			out.println(format("%-30s: %s", "Acceptable result codes", getAcceptableResultCodes()));
			out.println(format("%-30s: %s", "ESI Group Name", getRadEsiGroupName() != null ? getRadEsiGroupName() : ""));

		} else {
			out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
			out.println(format("%-30s: %s", "Translation Mapping",
					getTranslationMapping() != null ? getTranslationMapping() : ""));
			out.println(format("%-30s: %s", "Script", getScript() != null ? getScript() : ""));
			out.println(format("%-30s: %s", "Acceptable result codes", getAcceptableResultCodes()));
			out.println(format("%-30s: %s", "Peer Group Name", getPeerGroupName() != null ? getPeerGroupName() : ""));
		}
		out.close();
		return writer.toString();
	}

	int priorityOf(int resultCode) {
		int priority = priorityResultCodes.indexOf(resultCode);
		return priority != -1 
				? priority 
				: priorityResultCodes.indexOf(ResultCodeCategory.getResultCodeCategory(resultCode).value);
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Rule Set", ruleset.trim());
		object.put("Translation Mapping", translationMapping);
		object.put("Peer Group", peerGroupId);
		object.put("Script", script.trim());

		if(Collectionz.isNullOrEmpty(acceptableResultCodes) == false){
			List<String> resultCodes = new ArrayList<String>(); 

			for(Integer codes : acceptableResultCodes){
				resultCodes.add(codes.toString());
			}

			String codesString = resultCodes.toString().replaceAll("[\\s\\[\\]]", "");
			object.put("Accepted Result Codes", codesString);
		}
		return object;
	}


}
