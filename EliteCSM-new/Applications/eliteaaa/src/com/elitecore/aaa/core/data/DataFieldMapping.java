package com.elitecore.aaa.core.data;

import java.util.Map;

public interface DataFieldMapping {
	public String getFieldName();
	public String getDefaultValue();
	public String getValue(String inValue);
	public Map<String, String> getValueMapping();
}
