package com.elitecore.core.driverx;

import java.util.List;

public interface ValueProviderExt {
	public String getStringValue(String identifier) throws Exception;
	public List<String> getStringValues(String identifier) throws Exception;

}
