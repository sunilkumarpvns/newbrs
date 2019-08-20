package com.elitecore.core.serverx.sessionx;

import java.util.Date;
import java.util.Set;

public interface SessionData extends Comparable<SessionData> {
	Date getCreationTime();
	Date getLastUpdateTime();
	String getSessionId();
	String getSchemaName();
	String getValue(String key);
	void addValue(String key, String value);
	Set<String> getKeySet();
	void setSessionLoadTime(long sessionLoadTime);
	long getSessionLoadTime();
}
