package com.elitecore.corenetvertex.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;

public class ActivePCCRuleParser {
	
	private static final Splitter SEMICOLON_BASE_SPLITTER = Splitter.on(CommonConstants.SEMICOLON).trimTokens();
	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private static final Splitter COLON_BASE_SPLITTER = Splitter.on(CommonConstants.COLON).trimTokens();
	
	
	/**
	 * Input Example:
	 * 	basePkgId:pcc1,pcc2;subscriptionId=pcc3,pcc4
	 * 
	 * Output Example:
	 * 	pcc1=basePkgId
	 * 	pcc2=basePkgId
	 * 	pcc3=subscriptionId
	 * 	pcc4=subscriptionId
	 * 
	 * @param activePCCRuleStr
	 * @return
	 */
	public static Map<String, String> deserialize(String activePCCRuleStr) {
		Map<String, String> pccIdToSubscription = new HashMap<String, String>(0);
		
		if (Strings.isNullOrBlank(activePCCRuleStr)) {
			return pccIdToSubscription;
		}
		
		/*
		 * This check is provided for Previous version compatibility
		 * 
		 * Previously activePCCRuleStr is stored only with comma seperated pcc ids.
		 * Example:
		 * 	<pcc1-id>,<anotherPcc-id>, ...
		 * 
		 * Now activePCCRuleStr is stored in below format:
		 * <basepackage-id>:<pcc-id>,<anotherpcc-id>;<subscription-id>:<pcc-id>,<anotherpcc-id> 
		 * 
		 * 
		 * if activePCCRuleStr not contains SEMICOLON that means its for previous version activepccrule.
		 * 
		 * So generating map by: <pcc-id> -> null
		 * 
		 */
		if (activePCCRuleStr.indexOf(CommonConstants.COLON) == -1) {
			List<String> activePCCIds = COMMA_BASE_SPLITTER.split(activePCCRuleStr);
			
			for (String pccId : activePCCIds) {
				pccIdToSubscription.put(pccId, null);
			}
			
			return pccIdToSubscription;
		}
		
		
		
		/*
		 * generating map by <pcc-id> -> subscription or basePackage id 
		 * 
		 */
		List<String> alreadyActivePCCEntries = SEMICOLON_BASE_SPLITTER.split(activePCCRuleStr);
		for (String activePCCEntry : alreadyActivePCCEntries) {	
			List<String> tokens = COLON_BASE_SPLITTER.split(activePCCEntry);
			List<String> pccIds = COMMA_BASE_SPLITTER.split(tokens.get(1));
			for (String pccId : pccIds) {
				pccIdToSubscription.put(pccId, tokens.get(0));
			}
		}
		
		return pccIdToSubscription;
	}


	public static String serialize(Map<String, String> activePccIdToSubscriptions) {
		
		Map<String, StringBuilder> subscriptionTopccStrbuilder = new HashMap<String, StringBuilder>(0);
		
		for(Entry<String, String> pccSubscriptionEntry : activePccIdToSubscriptions.entrySet()) {
			StringBuilder builder = subscriptionTopccStrbuilder.get(pccSubscriptionEntry.getValue());
			if (builder == null) {
				builder = new StringBuilder(pccSubscriptionEntry.getKey());
				subscriptionTopccStrbuilder.put(pccSubscriptionEntry.getValue(), builder);
			} else {				
				builder.append(CommonConstants.COMMA).append(pccSubscriptionEntry.getKey());
			}
		}
		
		
		
		StringBuilder activePccStrBuilder = new StringBuilder();
		
		Iterator<Entry<String, StringBuilder>> iterator = subscriptionTopccStrbuilder.entrySet().iterator();
		
		Entry<String, StringBuilder> entry = iterator.next();
		activePccStrBuilder.append(entry.getKey()).append(CommonConstants.COLON).append(entry.getValue().toString());
		
		while(iterator.hasNext()) {
			entry = iterator.next();
			activePccStrBuilder.append(CommonConstants.SEMICOLON).append(entry.getKey()).append(CommonConstants.COLON).append(entry.getValue().toString());
		}

		return activePccStrBuilder.toString();
	}
 }
