package com.elitecore.core.commons.data;

public interface ValueProvider {

	static final ValueProvider NO_VALUE_PROVIDER = identifier-> null;
	String getStringValue(String identifier);

}


