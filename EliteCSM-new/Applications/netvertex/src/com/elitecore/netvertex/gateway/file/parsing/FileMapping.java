package com.elitecore.netvertex.gateway.file.parsing;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;

public class FileMapping {
	
	private String sourceKey;
	private String destinationKey;
	private String defaultValue;
	private String valueMapping;
	
	private static final String MODULE = "FILE-MAPPING";
	
	private Map<String,String> keyValueMapping = new HashMap<String,String>();
	
	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public String getDestinationKey() {
		return destinationKey;
	}

	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	public void setKeyValueMapping(String valueMapping) {
		if (Strings.isNullOrBlank(valueMapping)) {
			return;
		}
		
		String[] keyPairArray = valueMapping.split(",|;");
		if (keyPairArray.length > 0) {
			for (int i = 0; i < keyPairArray.length; i++) {
				String[] keyPair = keyPairArray[i].split("=");
				if (keyPair.length == 2) {
					this.keyValueMapping.put(keyPair[0].trim(), keyPair[1].trim());
				} else {
					LogManager.getLogger().warn(MODULE, "Invalid value for value mapping entry: [" + keyPairArray[i] + "] will be skipped.");
				}
			}
		}
	}
	
	public String getValue(String inValue) {
		if (Strings.isNullOrBlank(inValue)) {
			return inValue;
		}
		
		String mappedValue = keyValueMapping.get(inValue);
		
		String returnValue;
		if (Strings.isNullOrEmpty(mappedValue) == false) {
			returnValue = mappedValue;
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Source key " + sourceKey + " value: " + inValue + ", mapped with value: " + keyValueMapping.get(inValue));
			}
		} else {
			returnValue = inValue;
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Source key " + sourceKey + "; mapping for value: " + inValue + " not found; remains unchanged");
			}
		}

		return returnValue;
	}
}
