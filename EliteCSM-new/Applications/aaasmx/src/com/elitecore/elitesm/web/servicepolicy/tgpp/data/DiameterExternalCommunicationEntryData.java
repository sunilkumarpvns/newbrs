package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;

@XmlType(propOrder = {"ruleset", "translationMapping", "peerGroupId", 
		"script", "acceptableResultCodes"})
public class DiameterExternalCommunicationEntryData implements Differentiable {

	private String ruleset;
	private String translationMapping;
	private String script;
	private List<Integer> acceptableResultCodes = new ArrayList<Integer>();
	private String peerGroupId;

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