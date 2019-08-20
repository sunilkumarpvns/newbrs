package com.elitecore.nvsmx.ws.sessionmanagement.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.session.SessionRuleData;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SessionData implements Serializable{

	private static final String MODULE = "SESSION-DATA";
	private static final String ACTIVE_PCC_RULE_NAMES = "ACTIVE_PCC_RULE_NAMES";
	private static final String ACTIVE_PCC_RULE_IDS = "ACTIVE_PCC_RULE_IDS";
	private static final String ACTIVE_CHARGING_RULE_BASE_NAMES = "ACTIVE_CHARGING_RULE_BASE_NAMES";
	private static final String ACTIVE_CHARGING_RULE_BASE_NAMES_IDS = "ACTIVE_CHARGING_RULE_BASE_NAME_IDS";
	private static final String ACTIVE_IMS_PCC_RULE_IDS = "ACTIVE_IMS_PCC_RULE_IDS";
	private static final String ACTIVE_IMS_PCC_RULE_NAMES = "ACTIVE_IMS_PCC_RULE_NAMES";

	private List<Entry> entry;
	public static final String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
	public static final String CREATE_TIME = "CREATE_TIME";
	
	public SessionData() { }
	
	public SessionData(List<Entry> entry){
		this.setEntry(entry);		
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}
	
	public String toString() {
		
		StringWriter stringWriter = new StringWriter();
		IndentingWriter tabbedPrintWriter = new IndentingPrintWriter(new PrintWriter(stringWriter));
		toString(tabbedPrintWriter);
		
		return stringWriter.toString();
	}

	public void toString(IndentingWriter tabbedPrintWriter) {
		tabbedPrintWriter.incrementIndentation();
		tabbedPrintWriter.println();
		for(Entry en : entry){
			tabbedPrintWriter.println(en);
		}		
		tabbedPrintWriter.decrementIndentation();
	}

	public static SessionData from(SessionInformation sessionInformation, String sessionIPOrSubscriberId) {

		com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData wsSessionData = new com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData();
		Set<String> keySet = sessionInformation.getKeySet();

		List<com.elitecore.nvsmx.ws.sessionmanagement.data.Entry> wsEntries = new ArrayList<Entry>();
		for (String key : keySet) {

			if (key.equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)) {
				Map<String,String> pccRuleBasedOnSubscriptionId = ActivePCCRuleParser.deserialize(sessionInformation.getValue(key));

				com.elitecore.corenetvertex.util.commons.Entry nameEntry = new com.elitecore.corenetvertex.util.commons.Entry();
				com.elitecore.corenetvertex.util.commons.Entry idEntry = new com.elitecore.corenetvertex.util.commons.Entry();
				nameEntry.setKey(ACTIVE_PCC_RULE_NAMES);
				idEntry.setKey(ACTIVE_PCC_RULE_IDS);

				if(pccRuleBasedOnSubscriptionId.isEmpty()) {
					nameEntry.setValue(CommonConstants.DOUBLE_QUOTES);
					idEntry.setValue(CommonConstants.DOUBLE_QUOTES);
				} else {

					StringBuilder pccRuleNames = new StringBuilder();
					StringBuilder pccRuleIds = new StringBuilder();

					for (String id : pccRuleBasedOnSubscriptionId.keySet()) {

						PCCRule pccRule = DefaultNVSMXContext.getContext().getPolicyRepository().getPCCRuleById(id);
						if (pccRule == null) {
							getLogger().warn(MODULE, "PCC rule not found with id:" + id +", for subscriberId/sessionIP: " + sessionIPOrSubscriberId);
							continue;
						}

						pccRuleIds.append(id).append(", ");
						pccRuleNames.append(pccRule.getName()).append(", ");
					}
					idEntry.setValue(pccRuleIds.toString());
					nameEntry.setValue(pccRuleNames.toString());
				}

				wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(idEntry.getKey(), idEntry.getValue()));
				wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(nameEntry.getKey(), nameEntry.getValue()));

				continue;
			}

			if (key.equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)) {
				Map<String,String> chargingRuleBasedOnSubscriptionId = ActivePCCRuleParser.deserialize(sessionInformation.getValue(key));

				com.elitecore.corenetvertex.util.commons.Entry nameEntry = new com.elitecore.corenetvertex.util.commons.Entry();
				com.elitecore.corenetvertex.util.commons.Entry idEntry = new com.elitecore.corenetvertex.util.commons.Entry();

				nameEntry.setKey(ACTIVE_CHARGING_RULE_BASE_NAMES);
				idEntry.setKey(ACTIVE_CHARGING_RULE_BASE_NAMES_IDS);

				if(chargingRuleBasedOnSubscriptionId.isEmpty()) {
					nameEntry.setValue(CommonConstants.DOUBLE_QUOTES);
					idEntry.setValue(CommonConstants.DOUBLE_QUOTES);
				} else {

					StringBuilder chargingRuleBaseNames = new StringBuilder();
					StringBuilder chargingRuleBaseNameIds = new StringBuilder();

					for (String id : chargingRuleBasedOnSubscriptionId.keySet()) {

						ChargingRuleBaseName chargingRuleBaseName = DefaultNVSMXContext.getContext().getPolicyRepository().getChargingRuleBaseNameById(id);
						if (chargingRuleBaseName == null) {
							getLogger().warn(MODULE, "ChargingRuleBaseName not found with id:" + id +", for subscriberId/sessionIP: " + sessionIPOrSubscriberId);
							continue;
						}

						chargingRuleBaseNameIds.append(id).append(", ");
						chargingRuleBaseNames.append(chargingRuleBaseName.getName()).append(", ");
					}
					idEntry.setValue(chargingRuleBaseNameIds.toString());
					nameEntry.setValue(chargingRuleBaseNames.toString());
				}

				wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(idEntry.getKey(), idEntry.getValue()));
				wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(nameEntry.getKey(), nameEntry.getValue()));

				continue;
			}


			wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(key, sessionInformation.getValue(key)));
		}

		wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(
				com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData.LAST_UPDATE_TIME, sessionInformation.getLastUpdateTime().toString()));
		wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(
				com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData.CREATE_TIME, sessionInformation.getCreationTime().toString()));


		/**
		 * For Active IMS PCCRules
		 */
		List<SessionRuleData> sessionRuleDatas = sessionInformation.getSessionRuleDatas();

		if(Collectionz.isNullOrEmpty(sessionRuleDatas) == false) {


			StringBuilder dedicatedPccRuleIds = new StringBuilder();

			for (SessionRuleData sessionRuleData: sessionRuleDatas) {

				if (sessionRuleData.getPccRule() == null) {
					getLogger().warn(MODULE, "Active IMS PCCRule not found with id:" + sessionRuleData.getPccRule() +", for subscriberId/sessionIP: " + sessionIPOrSubscriberId);
					continue;
				}

				dedicatedPccRuleIds.append(sessionRuleData.getPccRule()).append(", ");
			}

			com.elitecore.corenetvertex.util.commons.Entry nameEntry = new com.elitecore.corenetvertex.util.commons.Entry();
			com.elitecore.corenetvertex.util.commons.Entry idEntry = new com.elitecore.corenetvertex.util.commons.Entry();

			nameEntry.setKey(ACTIVE_IMS_PCC_RULE_NAMES);
			idEntry.setKey(ACTIVE_IMS_PCC_RULE_IDS);

			if(dedicatedPccRuleIds == null || dedicatedPccRuleIds.toString().trim() == "") {
				idEntry.setValue(CommonConstants.DOUBLE_QUOTES);
				nameEntry.setValue(CommonConstants.DOUBLE_QUOTES);
			} else {
				//Currently PCCRule's Id and Name are same.that's why assigning same value in Id and Names
				nameEntry.setValue(dedicatedPccRuleIds.toString());
				idEntry.setValue(dedicatedPccRuleIds.toString());
			}

			wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(idEntry.getKey(), idEntry.getValue()));
			wsEntries.add(new com.elitecore.nvsmx.ws.sessionmanagement.data.Entry(nameEntry.getKey(), nameEntry.getValue()));
		}

		wsSessionData.setEntry(wsEntries);

		return wsSessionData;
	}


}

