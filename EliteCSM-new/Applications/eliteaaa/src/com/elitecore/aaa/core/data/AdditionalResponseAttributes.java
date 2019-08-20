package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;


public class AdditionalResponseAttributes {
	
	private static final String MODULE = "ADD-RESP-ATTR";
	private Map<String, List<String>> hardcodedAttributeMap;
	private Map<String, List<String>> attributeFromRequest;
	private Map<String, List<String>> attributeFromResponse;
	private Map<String, AdditionalResponseAttributes> groupAttributes;
	
	public AdditionalResponseAttributes(String strResponseAttributes) {
		this();
		parse(strResponseAttributes);
	}
	
	AdditionalResponseAttributes(
			Map<String, List<String>> hardcodedAttributeMap,
			Map<String, List<String>> attributeFromRequest,
			Map<String, List<String>> attributeFromResponse,
			Map<String, AdditionalResponseAttributes> groupAttributes) {
		this.hardcodedAttributeMap = hardcodedAttributeMap;
		this.attributeFromRequest = attributeFromRequest;
		this.attributeFromResponse = attributeFromResponse;
		this.groupAttributes = groupAttributes;
	}

	private AdditionalResponseAttributes() {
		this.hardcodedAttributeMap = new HashMap<String, List<String>>();
		this.attributeFromRequest = new HashMap<String, List<String>>();
		this.attributeFromResponse = new HashMap<String, List<String>>();
		this.groupAttributes = new HashMap<String, AdditionalResponseAttributes>();
	}

	private void parse(String strResponseAttributes){
		parse(strResponseAttributes, ',');
	}
	
	private void parse(String strResponseAttributes, char separator) {
		Map<String, List<String>> responseAttributeMap = null;
		if(strResponseAttributes !=null && strResponseAttributes.trim().length()>0){
			responseAttributeMap = new HashMap<String, List<String>>();
			String[] responseAttributes = Splitter.on(separator).trimTokens().preserveTokens()
					.splitToArray(strResponseAttributes);
			for(String responsAttr : responseAttributes){
				
				String [] tokens = ParserUtility.splitKeyAndValue(responsAttr);
				
				if (Strings.isNullOrBlank(tokens[1]) || tokens[1].equals("=") == false) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Invalid Attribute: " + responsAttr +  
								" arrived in Response Attributes: " + strResponseAttributes + 
								", Reason: Invalid Operator: " + tokens[1] + 
								" It should be in <key> = <value> form, Skipping Attribute");
					}
					continue;
				}
				
				String attributeId = tokens[0];
				if(Strings.isNullOrBlank(attributeId)){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Blank Attribute-ID arrived in Response Attributes: " + strResponseAttributes + 
								", Skipping Attribute");
					}
					continue;
				}
				String attributeValue = tokens[2];
				if(Strings.isNullOrBlank(attributeValue)){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "No Attribute Value arrived for Attribute-ID: " + attributeId +
								" in Response Attributes: " + strResponseAttributes + 
								", Skipping Attribute");
					}
					continue;
				}
				addTo(responseAttributeMap,attributeId.trim(), attributeValue.trim());
			}
		}
		parse(responseAttributeMap);
	}
	
	private void parse(Map<String, List<String>> responseAttributeMap) {

		if(responseAttributeMap == null || responseAttributeMap.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "No Parseable Response Attribute Arrived");
			}
			return;
		}
		for(Map.Entry<String, List<String>> attribute : responseAttributeMap.entrySet()){
			
			List<String> attributeValues = attribute.getValue();
			
			for(String attributeValue : attributeValues){
				
				if(attributeValue.startsWith("{")){
					addToGroupAttribute(attribute.getKey(), 
							parseJSONGroupAttribute(JSONObject.fromObject(attributeValue)));
				} else {
					addToSpecificMap(attribute.getKey(), attributeValue);
				}
			}
			
		}
	}

	private void addToGroupAttribute(String attributeId,
			AdditionalResponseAttributes subAttributes) {
		groupAttributes.put(attributeId, subAttributes);
	}

	private void addToSpecificMap(String attributID, String attributeValue) {
		
			if(attributeValue.startsWith(AAAServerConstants.ATTRIBUTE_FROM_REQUEST)) {
				addTo(attributeFromRequest, attributID, attributeValue.substring(AAAServerConstants.ATTRIBUTE_FROM_REQUEST.length()));
			}else if (attributeValue.startsWith(AAAServerConstants.ATTRIBUTE_FROM_RESPONSE)) {
				addTo(attributeFromResponse, attributID, attributeValue.substring(AAAServerConstants.ATTRIBUTE_FROM_RESPONSE.length()));
			}else {
				addTo(hardcodedAttributeMap, attributID, attributeValue);
			}		
			
	}
	public static void addTo(Map<String, List<String>> map, String key, String value) {
		List<String> list = map.get(key);

		if (list == null) {
			list = new ArrayList<String>();
			list.add(value);
			map.put(key, list);
		} else {
			map.get(key).add(value);
		}
	}

	private AdditionalResponseAttributes parseJSONGroupAttribute(JSONObject json) {
		AdditionalResponseAttributes subAttributes = new AdditionalResponseAttributes();
		
		@SuppressWarnings("unchecked")
		Set<String> keySet = json.keySet();

		for(String attributekey : keySet){
			Object object = json.get(attributekey);
			if(object instanceof JSONObject){
				subAttributes.addToGroupAttribute(attributekey, parseJSONGroupAttribute((JSONObject) object));
			} else {
				subAttributes.addToSpecificMap(attributekey, (String) object);
			}
		}
		return subAttributes;
	}
	
	public Map<String, List<String>> getHardcodedAttributeMap() {
		return hardcodedAttributeMap;
	}

	public Map<String, List<String>> getAttributeFromRequest() {
		return attributeFromRequest;
	}

	public Map<String, List<String>> getAttributeFromResponse() {
		return attributeFromResponse;
	}

	public Map<String, AdditionalResponseAttributes> getGroupAttributes() {
		return groupAttributes;
	}

}
