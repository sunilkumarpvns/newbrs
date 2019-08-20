package com.elitecore.elitesm.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;

public class ConcurrentLoginPolicyUtility {
	
	public static String getAttributeIdFromAttributeName(String attribute, String attributeName) throws DataManagerException {
		
		Map<String, String> AttributeIdToNameMap = getAttributeIdToNameMap(attribute);
		
		if (AttributeIdToNameMap == null) {
			return "attribute_list_not_found";
		} else {
			return getIdOfNameFromAttributeIdToNameMap(AttributeIdToNameMap, attributeName);
		}

	}
	
	public static String getAttributeNameFromAttributeId(String attribute, String attributeId) throws DataManagerException {
		
		Map<String, String> AttributeIdToNameMap = getAttributeIdToNameMap(attribute);

		if (AttributeIdToNameMap == null) {
			return "attribute_list_not_found";
		} else {
			return getNameOfIdFromAttributeIdToNameMap(AttributeIdToNameMap, attributeId);
		}
		
	}

	private static String getIdOfNameFromAttributeIdToNameMap(Map<String, String> attributeIdToNameMap, String attributeName) {
		if (attributeIdToNameMap != null) {
			for (Entry<String, String> entry : attributeIdToNameMap.entrySet()) {
				if (entry.getValue().equals(attributeName)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
	
	private static String getNameOfIdFromAttributeIdToNameMap(Map<String, String> attributeIdToNameMap, String attributeId) {
		if (attributeIdToNameMap != null) {
			for (Entry<String, String> entry : attributeIdToNameMap.entrySet()) {
				if (entry.getKey().equals(attributeId)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}
	
	private static Map<String, String> getAttributeIdToNameMap(String attribute) throws DataManagerException {
		
		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
		DictionaryParameterDetailData dictionaryParameterDetailData = new DictionaryParameterDetailData();
		dictionaryParameterDetailData.setName(attribute);
		List lstPreDefineValues = dictionaryBLManager.getDictionaryParameterDetailList(dictionaryParameterDetailData);

		if (Collectionz.isNullOrEmpty(lstPreDefineValues) == false) {
			dictionaryParameterDetailData = (DictionaryParameterDetailData) lstPreDefineValues.get(0);
			String strPredefineValues = dictionaryParameterDetailData.getPredefinedValues();
			
			if (Strings.isNullOrBlank(strPredefineValues) == false) {
				StringTokenizer tokenizer = new StringTokenizer(strPredefineValues, ",:");
				
				Map<String, String> supportedValueMap = new LinkedHashMap<String, String>();

				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String key = tokenizer.nextToken();
					supportedValueMap.put(key, value);
				}
				
				return supportedValueMap;
			}
		} 
		
		return null;
	}

}
