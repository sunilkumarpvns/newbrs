package com.elitecore.aaa.radius.plugins.userstatistic.conf;

import java.util.List;
import java.util.Map;

public interface UserStatisticPostAuthPluginConfiguration {
	
	public static final String DB_FIELD = "DB_FIELD";
	public static final String DEFAULT_VALUE = "DEFAULT_VALUE";
	public static final String ATTRIBUTE_IDS = "ATTRIBUTE_IDS";
	public static final String USE_DICTIONARY_VALUE = "USE_DICTIONARY_VALUE";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String PACKET_TYPE = "PACKET_TYPE";
	public static final String DATA_TYPE_DATE = "Date";
	public static final long QUERY_TIMEOUT_IN_MS = 1000;
	public static final long MAX_QUERY_TIMEOUT_COUNT = 100;
	public static final long BATCH_UPDATE_INTERVAL_MS = 1000;
	
	public String getName();
	public String getDescription();
	public String getStatus();
	public String getDataSourceName();	
	public String getTableName();
	public long getDbQueryTimeoutInMs();
	public long getMaxQueryTimeoutCount();
	public long getBatchUpdateIntervalInMs();
	public List<Map<String, Object>> getDbFieldMapping();
	
}
