package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctCoADMHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthCoADMHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;

@XmlRootElement(name = "coa-dm-handler")
public class CoADMHandlerEntryData extends ServicePolicyHandlerDataSupport
implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private String ruleset;
	private int packetType;
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
	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
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
		return new AcctCoADMHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthCoADMHandler(serviceContext, this);
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
