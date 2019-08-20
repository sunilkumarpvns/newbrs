package com.elitecore.netvertex.gateway.diameter.af;

import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.io.StringWriter;

public class AFSessionRule {

	private String srId;
	private long mediaType;
	private long mediaComponentNumber;
	private long flowNumber;
	private String pccRule;
	private String afSessionId;
	private String uplinkFlow;
	private String downlinkFlow;
	private long mbrdl;
	private long mbrul;
	private long gbrul;
	private long gbrdl;
	private QCI qci;
	private FlowStatus flowStatus;


	
	public AFSessionRule(String srId,
			long flowNumber,
			long mediaIdentifier, 
			String pccRule,
			String uplinkFlow,
			String downlinkFlow,
			long mediaComponentNumber, 
			String afSessionId,
	        long mbrdl,long mbrul,long gbrdl,long gbrul,QCI qci, FlowStatus flowStatus) {
		super();
		this.srId = srId;
		this.flowNumber = flowNumber;
		this.mediaType = mediaIdentifier;
		this.pccRule = pccRule;
		this.uplinkFlow = uplinkFlow;
		this.downlinkFlow = downlinkFlow;
		this.mediaComponentNumber = mediaComponentNumber;
		this.afSessionId = afSessionId;
		this.mbrdl = mbrdl;
		this.mbrul = mbrul;
		this.gbrdl = gbrdl;
		this.gbrul = gbrul;
		this.qci = qci;
		this.flowStatus = flowStatus;
	}
	
	private AFSessionRule() {
	}

	public static AFSessionRule create(SessionData sessionData) {

		AFSessionRule afSessionRule = new AFSessionRule();
		afSessionRule.srId = sessionData.getSessionId();
		afSessionRule.pccRule = sessionData.getValue(PCRFKeyConstants.PCC_RULE_LIST.val);
		afSessionRule.mediaType = Numbers.parseLong(sessionData.getValue(PCRFKeyConstants.CS_MEDIA_TYPE.val), 0);
		afSessionRule.mediaComponentNumber = Numbers.parseLong(sessionData.getValue(PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.val), 0);
		afSessionRule.flowNumber = Numbers.parseLong(sessionData.getValue(PCRFKeyConstants.CS_FLOW_NUMBSER.val), 0);
		afSessionRule.downlinkFlow = sessionData.getValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.val);
		afSessionRule.uplinkFlow = sessionData.getValue(PCRFKeyConstants.CS_UPLINK_FLOW.val);
		afSessionRule.afSessionId  = sessionData.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.val);
		setPCCAdditionalParams(afSessionRule, sessionData.getValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val));

		return afSessionRule;

	}

	private static void setPCCAdditionalParams(AFSessionRule afSessionRule, String pccAdditionalParamsJsonString) {
		if(Strings.isNullOrBlank(pccAdditionalParamsJsonString) == false) {

			JsonObject jsonObject = GsonFactory.defaultInstance().fromJson(pccAdditionalParamsJsonString, JsonObject.class);

			afSessionRule.mbrdl = getVal(jsonObject, PCRFKeyConstants.PCC_RULE_MBRDL.val);
			afSessionRule.mbrul = getVal(jsonObject, PCRFKeyConstants.PCC_RULE_MBRUL.val);
			afSessionRule.gbrdl = getVal(jsonObject, PCRFKeyConstants.PCC_RULE_GBRDL.val);
			afSessionRule.gbrul = getVal(jsonObject, PCRFKeyConstants.PCC_RULE_GBRUL.val);
			afSessionRule.flowStatus = getFlowStatusVal(jsonObject);
			afSessionRule.qci = getQCIVal(jsonObject);
		}
	}

	private static QCI getQCIVal(JsonObject jsonObject) {
		JsonElement jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_QCI.val);

		if (isJsonNull(jsonElement)) {
			return null;
		}

		return  QCI.fromId(jsonElement.getAsInt());
	}

	private static boolean isJsonNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull();
	}

	private static FlowStatus getFlowStatusVal(JsonObject jsonObject) {
		JsonElement jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val);

		if (isJsonNull(jsonElement)) {
			return null;
		}

		return  FlowStatus.fromValue(jsonElement.getAsLong());
	}

	private static long getVal(JsonObject jsonObject, String key) {

		JsonElement jsonElement = jsonObject.get(key);

		if (isJsonNull(jsonElement)) {
			return 0;
		}

		return jsonElement.getAsLong();
	}

	public void toSessionData(@Nonnull SessionData sessionData) {

		sessionData.addValue(PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.val, String.valueOf(mediaComponentNumber));
		sessionData.addValue(PCRFKeyConstants.CS_MEDIA_TYPE.val, String.valueOf(mediaType));
		sessionData.addValue(PCRFKeyConstants.CS_FLOW_NUMBSER.val, String.valueOf(flowNumber));
		sessionData.addValue(PCRFKeyConstants.PCC_RULE_LIST.val, pccRule);
		sessionData.addValue(PCRFKeyConstants.CS_AF_SESSION_ID.val, afSessionId);
		sessionData.addValue(PCRFKeyConstants.CS_UPLINK_FLOW.val, uplinkFlow);
		sessionData.addValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.val, downlinkFlow);

		JsonObject jsonObject = new JsonObject();

		if (mbrdl > 0) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_MBRDL.val, mbrdl);
		}
		if (mbrul > 0) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_MBRUL.val, mbrul);
		}
		if (gbrdl > 0) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_GBRDL.val, gbrdl);
		}
		if (gbrul > 0) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_GBRUL.val, gbrul);
		}
		if (qci != null) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_QCI.val, String.valueOf(qci.val));
		}
		if (flowStatus != null ) {
			jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val, String.valueOf(flowStatus.val));
		}

		sessionData.addValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val, jsonObject.toString());
	}


	public long getMediaType() {
		return mediaType;
	}
	
	public String getPccRule() {
		return pccRule;
	}
	
	public String getUplinkFlow() {
		return uplinkFlow;
	}
	
	public String getDownlinkFlow() {
		return this.downlinkFlow;
	}

	public long getMediaComponentNumber() {
		return mediaComponentNumber;
	}

	public String getAfSessionId() {
		return afSessionId;
	}

	public String getSrId() {
		return srId;
	}
	
	public long getFlowNumber() {
		return flowNumber;
	}

	public long getMbrdl() {
		return mbrdl;
	}

	public long getMbrul() {
		return mbrul;
	}

	public long getGbrul() {
		return gbrul;
	}

	public QCI getQci() {
		return qci;
	}

	public long getGbrdl() {
		return gbrdl;
	}

	public FlowStatus getFlowStatus() {
		return flowStatus;
	}

	public void toString(IndentingWriter out) {
		out.println("Id= "+ srId + ", Media Comp No = "+ mediaComponentNumber 
								 + ", AF App Id = " + afSessionId
								 + ", Flow Number = " + flowNumber
								 + ", Media Type = " + mediaType 
								 + ", PCCRule =" + pccRule
								 + ", Uplink Flow = " + uplinkFlow
								 + ", Downlink Flow = " + downlinkFlow
								 + ", MBRDL = " + mbrdl
								 + ", MBRUL = " + mbrul
								 + ", GBRDL = " + gbrdl
								 + ", GBRUL = " + gbrul
								 + ", QCI = " + qci
								 + ", Flow Status = " + flowStatus);
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		toString(out);
		out.close();
		return stringWriter.toString();
	}

	public QCI getQCI() {
		return qci;
	}

	
}

