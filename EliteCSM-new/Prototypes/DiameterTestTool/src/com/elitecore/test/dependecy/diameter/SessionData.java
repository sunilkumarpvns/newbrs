package com.elitecore.test.dependecy.diameter;

import java.util.Date;
import java.util.Set;

public interface SessionData {
	Date getCreationTime();
	Date getLastUpdateTime();
	String getSessionId();
	String getSchemaName();
	String getValue(String key);
	void addValue(String key, String value);
	Set<String> getKeySet(); 
}
