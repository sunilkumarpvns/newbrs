package com.elitecore.aaa.radius.policies.radiuspolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.radius.policies.radiuspolicy.data.RadiusPolicyTreeData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.IReplyItems;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.ITreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public class RadiusReplyItems implements IReplyItems,Serializable {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "Radius Reply Items";
	ArrayList<ReplyItem> replyItemsList;
	String replyItem;

	public RadiusReplyItems(String replyItem) {
		this.replyItem = replyItem;
	}
	class ReplyItem implements Serializable{
		private static final long serialVersionUID = 1L;
		private PolicyParseTreeNode checkItem;
		private String checkItemString;
		private static final int REQUEST_PACKET = 0;
		private static final int RESPONSE_PACKET = 1;
		private ArrayList<ReplyAttribute> replyAttributes;
		public ReplyItem(String condition,Map<String,ArrayList<String>> replyItems) {
			this.checkItemString = condition;
			if(condition.startsWith("$REQ:")){
				checkItem = getAttributeParseTreeNode(condition.substring(5));
				checkItem.setCheckIn(REQUEST_PACKET);
			}else if(condition.startsWith("$RES:")){
				checkItem = getAttributeParseTreeNode(condition.substring(5));
				checkItem.setCheckIn(RESPONSE_PACKET);
			}
			
			replyAttributes = new ArrayList<ReplyAttribute>();
			Iterator<String> replyItemsIterator = replyItems.keySet().iterator();
			while(replyItemsIterator.hasNext()){
				String attributeId = replyItemsIterator.next();
				ArrayList<String> values = replyItems.get(attributeId);
				ReplyAttribute replyAttribute = new ReplyAttribute(attributeId,values.toArray(new String[values.size()]));
				replyAttributes.add(replyAttribute);
			}
		}
		public ArrayList<IRadiusAttribute> getReplyItem(RadiusPolicyTreeData policyTreeData, Map<String, Map<String, ArrayList<String>>> overideValues){
			int result = PolicyConstants.SUCCESS;
			//TODO checkIn processing is pending
			if(checkItem != null){
					result = checkItem.evaluate(policyTreeData, null);
			}

			if(result != PolicyConstants.SUCCESS){
				return null;
			}
			Map<String, ArrayList<String>> overrideAttribute = null;
			if(overideValues != null){
				overrideAttribute = overideValues.get(checkItemString);
			}
				
			ArrayList<IRadiusAttribute> replyItems = new ArrayList<IRadiusAttribute>();
			for(ReplyAttribute replyAttribte:replyAttributes){
				ArrayList<String> valueList = null;
				if(overrideAttribute != null)
					valueList = overrideAttribute.get(replyAttribte.getReplyAttribute());
				ReplyAttribute overridedReplyAttribute = replyAttribte;
				if(valueList != null){
					overridedReplyAttribute = new ReplyAttribute(replyAttribte.getReplyAttribute(), valueList.toArray(new String[valueList.size()]));
					overrideAttribute.remove(replyAttribte.getReplyAttribute());
				}
				ArrayList<IRadiusAttribute> localReplyItems = overridedReplyAttribute.getReplyItems(policyTreeData.getRadiusServiceRequest(), policyTreeData.getRadiusServiceResponse()); 
				if(localReplyItems != null)
					replyItems.addAll(localReplyItems);
			}
			return replyItems;
		}
		
	}
	
	class ReplyAttribute implements Serializable{
		private static final long serialVersionUID = 1L;
		private IRadiusAttribute replyAttribute;
		private String strReplyAttribute;
		private String strKey;
		private ArrayList<IRadiusAttribute> values;
		private ArrayList<String> valuesFromRequest;
		private ArrayList<String> valuesFromResponse;
		private ArrayList<ArrayList<ReplyAttribute>> concateValues;
		public ReplyAttribute(String attributeId,String[] values) {
			strReplyAttribute = attributeId;
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex != -1){
				this.strKey = attributeId.substring(keyIndex + 1);
				attributeId = attributeId.substring(0, keyIndex);
			}
			
			this.values = new ArrayList<IRadiusAttribute>();
			this.valuesFromRequest = new ArrayList<String>();
			this.valuesFromResponse = new ArrayList<String>();
			this.concateValues = new ArrayList<ArrayList<ReplyAttribute>>();
			replyAttribute = Dictionary.getInstance().getKnownAttribute(attributeId);
			if(replyAttribute != null){
				final int totalValues = values.length;
				for(int cntr = 0; cntr < totalValues; cntr++){
					String value = values[cntr];
					String[] newValues = ParserUtility.splitString(value, '+');
					if(newValues.length > 1){
						ArrayList<ReplyAttribute> concatedAttributes = new ArrayList<ReplyAttribute>();
						for(int i=0;i<newValues.length;i++){
							String[] arr = new String[1];
							arr[0] = newValues[i];
							ReplyAttribute replyAttr = new ReplyAttribute(strReplyAttribute, arr);
							concatedAttributes.add(replyAttr);
						}
						this.concateValues.add(concatedAttributes);
					}else{
						if(value.startsWith("$REQ:")){
							valuesFromRequest.add(value.substring(5));
						}else if(value.startsWith("$RES:")){
							valuesFromResponse.add(value.substring(5));
						}else{
							if(strKey != null)
								value = strKey + "=" + value; 
							try {
								IRadiusAttribute  radAttr = (IRadiusAttribute) replyAttribute.clone();
								radAttr.setStringValue(value);
								this.values.add(radAttr);
							} catch (CloneNotSupportedException e) {
							}
						}
					}
				}
			}
		}
		
		public String getReplyAttribute(){
			return strReplyAttribute;
		}
		public ArrayList<IRadiusAttribute> getReplyItems(RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse) {
			ArrayList<IRadiusAttribute> replyAttributes = new ArrayList<IRadiusAttribute>();
			if(replyAttribute == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unknown Attribute encountered in policy application.  Ignoring Unknown Attribute: " + strReplyAttribute);
				return replyAttributes;
			}
			for(IRadiusAttribute radiusAttribute:values){
				try {
					replyAttributes.add((IRadiusAttribute)radiusAttribute.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			
			for(String attrId:valuesFromRequest){
				try {
					IRadiusAttribute radiusAttribute = (IRadiusAttribute) replyAttribute.clone();
					String valueFromRequest = getValueFromServiceRequest(radServiceRequest, attrId);
					if(valueFromRequest == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Configured Attribute not found in reqeust packet, AttributeId = " + attrId);
						continue;
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
					IRadiusAttribute radiusAttribute = (IRadiusAttribute) replyAttribute.clone();
					String valueFromResponse = getValueFromServiceResponse(radServiceResponse, attrId);
					if(valueFromResponse == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Configured Attribute not found in response packet, AttributeId = " + attrId);
						continue;
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
			
			IRadiusAttribute radiusAttribute=null;
			if(concateValues.size() > 0){
				try{
					for(ArrayList<ReplyAttribute> replyAttrs: concateValues){
						radiusAttribute =(IRadiusAttribute) replyAttribute.clone();
						for(ReplyAttribute replyAttr : replyAttrs){
							ArrayList<IRadiusAttribute> radAttrs = replyAttr.getReplyItems(radServiceRequest, radServiceResponse);
							if(radAttrs != null){
								for(IRadiusAttribute attr : radAttrs){
									radiusAttribute.doPlus(attr.getStringValue());
								}
							}else{
								return null;
							}
						}
						if(radiusAttribute!= null)
							replyAttributes.add(radiusAttribute);
					}
					
				}catch(Exception ex){
					LogManager.getLogger().trace(MODULE,ex);
				}
			}
			return replyAttributes;
		}
		private String getValueFromServiceRequest(RadServiceRequest radiusRequest,String attributeId){
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex == -1){
				IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(attributeId,true);
				if(radiusAttribute != null)
					return radiusAttribute.getStringValue();
			}else{
				String strAttribute = attributeId.substring(0, keyIndex);
				String strKey = attributeId.substring(keyIndex + 1);
				try{
					IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(strAttribute,true);
					if(radiusAttribute != null)
						return radiusAttribute.getKeyValue(strKey);
				}catch(ClassCastException cce){
					LogManager.getLogger().trace(MODULE, cce);
				}
			}
			return null;
		}

		private String getValueFromServiceResponse(RadServiceResponse radiusResponse,String attributeId){
			int keyIndex = attributeId.indexOf('.');
			if(keyIndex == -1){
				IRadiusAttribute radiusAttribute = radiusResponse.getRadiusAttribute(true,attributeId);
				if(radiusAttribute != null)
					return radiusAttribute.getStringValue();
			}else{
				String strAttribute = attributeId.substring(0, keyIndex);
				String strKey = attributeId.substring(keyIndex + 1);
				try{
					IRadiusAttribute radiusAttribute = radiusResponse.getRadiusAttribute(true,strAttribute);
					if(radiusAttribute != null)
						return radiusAttribute.getKeyValue(strKey);
				}catch(ClassCastException cce){
					LogManager.getLogger().trace(MODULE, cce);
				}
			}
			return null;
		}

		@Override
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Reply Attribute: " + strReplyAttribute);
			buffer.append("\nStatic values:" + values);
			buffer.append("\nValues from Request: " + valuesFromRequest);
			buffer.append("\nValues from Response: " + valuesFromResponse);
			buffer.append("\nValues to Concatenate: " + concateValues);
			return buffer.toString();
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
		
		RadiusParseTreeNode radiusParseTreeNode = new RadiusParseTreeNode(strSplitedExpression,true,PolicyType.CHECK_ITEM_POLICY, strExpr);
		return radiusParseTreeNode;
	}


	@Override
	public ArrayList<?> getReplyItems(ITreeData treeData,
			Map<String, Map<String, ArrayList<String>>> overideValues) {
		ArrayList<IRadiusAttribute> replyAttributes = new ArrayList<IRadiusAttribute>();
		if(replyItemsList != null){
			final int totoalReplyItems = replyItemsList.size();
			for(int cntr = 0 ; cntr < totoalReplyItems;cntr++){
				ReplyItem replyItem = replyItemsList.get(cntr);
				ArrayList<IRadiusAttribute> localReplyAttributes = replyItem.getReplyItem((RadiusPolicyTreeData)treeData, overideValues);
				if(localReplyAttributes != null)
					replyAttributes.addAll(localReplyAttributes);
			}
		}
		if(overideValues != null){
			for(Entry<String, Map<String, ArrayList<String>>> entry:overideValues.entrySet()){
				ReplyItem replyItem = new ReplyItem(entry.getKey(), entry.getValue());
				ArrayList<IRadiusAttribute> localReplyAttributes = replyItem.getReplyItem((RadiusPolicyTreeData)treeData, overideValues);
				if(localReplyAttributes != null)
					replyAttributes.addAll(localReplyAttributes);
			}
		}
		return replyAttributes;
	}
}
