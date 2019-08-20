package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;

@XmlRootElement(name = "coa-dm-handler")
@XmlType(propOrder = {"ruleset", "packetType", "translationMapping"})
public class CoADMHandlerEntryData extends ServicePolicyHandlerDataSupport
implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private String ruleset;
	private String packetType;
	private String translationMapping;
	private int scheduleAfterInMillis;
	
	@XmlTransient
	public int getScheduleAfterInMillis() {
		return scheduleAfterInMillis;
	}

	public void setScheduleAfterInMillis(int scheduleAfterInMillis) {
		this.scheduleAfterInMillis = scheduleAfterInMillis;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "packet-type")
	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	@XmlElement(name = "translation-mapping")
	public String getTranslationMapping() {
		return translationMapping;
	}

	public void setTranslationMapping(String translationMapping) {
		this.translationMapping = translationMapping;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Ruleset", ruleset);
		object.put("Packet Type", packetType);
		object.put("Translation Mapping", translationMapping);
		return object;
	}

}
