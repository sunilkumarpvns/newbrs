package com.elitecore.aaa.diameter.policies.diameterpolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.policies.diameterpolicy.data.DiameterPolicyTreeData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.IReplyItems;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.ITreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

public class DiameterReplyItems implements IReplyItems, Serializable {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "Radius Reply Items";
	ArrayList<ReplyItem> replyItemsList;
	String replyItem;

	public DiameterReplyItems(String replyItem) {
		this.replyItem = replyItem;
	}
	class ReplyItem implements Serializable{
		private static final long serialVersionUID = 1L;
		private PolicyParseTreeNode checkItem;
		private String checkItemString;
		private int checkIn;
		private static final int REQUEST_PACKET = 0;
		private static final int RESPONSE_PACKET = 1;
		private ArrayList<ReplyAttribute> replyAttributes;
		public ReplyItem(String condition,Map<String,ArrayList<String>> replyItems) {
			this.checkItemString = condition;
			if(condition.startsWith("$REQ:")){
				checkItem = getAttributeParseTreeNode(condition.substring(5));
				checkIn = REQUEST_PACKET;
			}else if(condition.startsWith("$RES:")){
				checkItem = getAttributeParseTreeNode(condition.substring(5));
				checkIn = RESPONSE_PACKET;
			}
			replyAttributes = new ArrayList<ReplyAttribute>();
			Iterator<String> replyItemsIterator = replyItems.keySet().iterator();
			while(replyItemsIterator.hasNext()){
				String attributeId = replyItemsIterator.next();
				ArrayList<String> values = replyItems.get(attributeId);
				ReplyAttribute replyAttribute = new ReplyAttribute(attributeId,values);
				replyAttributes.add(replyAttribute);
			}
		}
		public ArrayList<IDiameterAVP> getReplyItem(DiameterPolicyTreeData policyTreeData, Map<String, Map<String, ArrayList<String>>> overideValues){
			int result = PolicyConstants.SUCCESS;
			//TODO checkIn processing is pending
			if(checkItem != null){
				if(checkIn == REQUEST_PACKET)
					result = checkItem.evaluate(policyTreeData, null);
				else
					result = checkItem.evaluate(policyTreeData, null);
			}

			if(result != PolicyConstants.SUCCESS){
				return null;
			}
			Map<String, ArrayList<String>> overrideAttribute = null;
			if(overideValues != null){
				overrideAttribute = overideValues.get(checkItemString);
			}
				
			ArrayList<IDiameterAVP> replyItems = new ArrayList<IDiameterAVP>();
			for(ReplyAttribute replyAttribte:replyAttributes){
				ArrayList<String> valueList = null;
				if(overrideAttribute != null)
					valueList = overrideAttribute.get(replyAttribte.getReplyAttribute());
				ReplyAttribute overridedReplyAttribute = replyAttribte;
				if(valueList != null){
					overridedReplyAttribute = new ReplyAttribute(replyAttribte.getReplyAttribute(), valueList);
					overrideAttribute.remove(replyAttribte.getReplyAttribute());
				}
				ArrayList<IDiameterAVP> localReplyItems = overridedReplyAttribute.getReplyItems(policyTreeData.getDiameterRequest(), policyTreeData.getDiameterAnswer()); 
				replyItems.addAll(localReplyItems);
			}
			return replyItems;
		}
	}
	
	class ReplyAttribute implements Serializable{
		private static final long serialVersionUID = 1L;
		private IDiameterAVP replyAttribute;
		private String strReplyAttribute;
		private String strKey;
		private ArrayList<IDiameterAVP> values;
		private ArrayList<String> valuesFromRequest;
		private ArrayList<String> valuesFromResponse;
		public ReplyAttribute(String attributeId,ArrayList<String> values) {
			strReplyAttribute = attributeId;
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex != -1){
				this.strKey = attributeId.substring(keyIndex + 1);
				attributeId = attributeId.substring(0, keyIndex);
			}
			
			this.values = new ArrayList<IDiameterAVP>();
			this.valuesFromRequest = new ArrayList<String>();
			this.valuesFromResponse = new ArrayList<String>();
			replyAttribute = DiameterDictionary.getInstance().getAttribute(attributeId);
			if(replyAttribute != null){
				final int totalValues = values.size();
				for(int cntr = 0; cntr < totalValues; cntr++){
					String value = values.get(cntr);
					if(value.startsWith("$REQ:")){
						valuesFromRequest.add(value.substring(5));
					}else if(value.startsWith("$RES:")){
						valuesFromResponse.add(value.substring(5));
					}else{
						if(strKey != null)
							value = strKey + "=" + value; 
						try {
							IDiameterAVP  radAttr = (IDiameterAVP) replyAttribute.clone();
							radAttr.setStringValue(value);
							this.values.add(radAttr);
						} catch (CloneNotSupportedException e) {
						}
					}
				}
			}
		}
		
		public String getReplyAttribute(){
			return strReplyAttribute;
		}
		public ArrayList<IDiameterAVP> getReplyItems(DiameterRequest diaServiceRequest, DiameterAnswer diameterResponse) {
			ArrayList<IDiameterAVP> replyAttributes = new ArrayList<IDiameterAVP>();
			if(replyAttribute == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unknown Attribute encountered in policy application.  Ignoring Unknown Attribute: " + strReplyAttribute);
				return replyAttributes;
			}
			for(IDiameterAVP radiusAttribute:values){
				try {
					replyAttributes.add((IDiameterAVP)radiusAttribute.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			
			for(String attrId:valuesFromRequest){
				try {
					IDiameterAVP radiusAttribute = (IDiameterAVP) replyAttribute.clone();
					String valueFromRequest = getValueFromServiceRequest(diaServiceRequest, attrId);
					if(valueFromRequest == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Configured Attribute not found in reqeust packet, AttributeId = " + strReplyAttribute);
					}
					if(strKey == null){
						radiusAttribute.setStringValue(valueFromRequest);
					}else{
						radiusAttribute.setStringValue(strKey + "=" + valueFromRequest);
					}
					replyAttributes.add(radiusAttribute);	
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().trace(MODULE,e);
				}
			}
			
			for(String attrId:valuesFromResponse){
				try {
					IDiameterAVP radiusAttribute = (IDiameterAVP) replyAttribute.clone();
					String valueFromResponse = getValueFromServiceResponse(diameterResponse, attrId);
					if(valueFromResponse == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Configured Attribute not found in response packet, AttributeId = " + strReplyAttribute);
					}
					if(strKey == null){
						radiusAttribute.setStringValue(valueFromResponse);
					}else{
						radiusAttribute.setStringValue(strKey + "=" + valueFromResponse);
					}
					replyAttributes.add(radiusAttribute);	
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().trace(MODULE,e);
				}
			}
			return replyAttributes;
		}
		private String getValueFromServiceRequest(DiameterRequest request,String attributeId){
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex == -1){
				IDiameterAVP radiusAttribute = request.getAVP(attributeId);
				if(radiusAttribute != null)
					return radiusAttribute.getStringValue();
			}
			return null;
		}

		private String getValueFromServiceResponse(DiameterAnswer answer,String attributeId){
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex == -1){
				IDiameterAVP attribute = answer.getAVP(attributeId);
				if(attribute != null)
					return attribute.getStringValue();
			}
			return null;
		}

	}
	
	
	public void parseReplyItems() throws ParserException{
		ArrayList<ReplyItem> replyItemsList = new ArrayList<ReplyItem>();
		if(replyItem == null || replyItem.trim().length() == 0)
			return;
		Map<String,Map<String,ArrayList<String>>> parsedReplyItems = ParserUtility.parseReplyItems(replyItem);
		Iterator<String> replyItemsIterator = parsedReplyItems.keySet().iterator();
		while(replyItemsIterator.hasNext()){
			String replyItemsKey =replyItemsIterator.next();
			Map<String,ArrayList<String>> replyItemMap = parsedReplyItems.get(replyItemsKey);
			ReplyItem replyItem = new ReplyItem(replyItemsKey, replyItemMap);
			replyItemsList.add(replyItem);
		}
		this.replyItemsList = replyItemsList;
	}
	
	private PolicyParseTreeNode getAttributeParseTreeNode(String strExpr) {
		if(!strExpr.contains("=")){
			strExpr = strExpr + "=*";
		}
		String[] strSplitedExpression = ParserUtility.splitKeyAndValue(strExpr);
		
		DiameterParseTreeNode diameterParseTreeNode = new DiameterParseTreeNode(strSplitedExpression,true,PolicyType.CHECK_ITEM_POLICY, strExpr);
		return diameterParseTreeNode;
	}


	@Override
	public ArrayList<?> getReplyItems(ITreeData treeData,
			Map<String, Map<String, ArrayList<String>>> overideValues) {
		ArrayList<IDiameterAVP> replyAttributes = new ArrayList<IDiameterAVP>();
		if(replyItemsList != null){
			final int totoalReplyItems = replyItemsList.size();
			for(int cntr = 0 ; cntr < totoalReplyItems;cntr++){
				ReplyItem replyItem = replyItemsList.get(cntr);
				ArrayList<IDiameterAVP> localReplyAttributes = replyItem.getReplyItem((DiameterPolicyTreeData)treeData, overideValues);
				if(localReplyAttributes != null)
					replyAttributes.addAll(localReplyAttributes);
			}
		}
		if(overideValues != null){
			for(Entry<String, Map<String, ArrayList<String>>> entry:overideValues.entrySet()){
				ReplyItem replyItem = new ReplyItem(entry.getKey(), entry.getValue());
				ArrayList<IDiameterAVP> localReplyAttributes = replyItem.getReplyItem((DiameterPolicyTreeData)treeData, overideValues);
				if(localReplyAttributes != null)
					replyAttributes.addAll(localReplyAttributes);
			}
		}
		return replyAttributes;
	}
}
