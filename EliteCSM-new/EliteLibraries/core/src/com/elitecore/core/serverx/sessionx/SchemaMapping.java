package com.elitecore.core.serverx.sessionx;

import java.util.List;

public interface SchemaMapping {
	int LOCAL_SEQUENCE_GENERATOR = 0;
	int DB_SEQUENCE_GENERATOR = 1;
	int UUID_GENERATOR = 2;
	
	String CREATION_TIME = "CREATIONTIME";
	String LAST_UPDATE_TIME = "LASTUPDATETIME";
	
	String getTableName();
	String getSchemaName();

	FieldMapping getIdFieldMapping();
	int getIdGenerator();
	
	int getIntialSequenceValue();
	int getSeqIncreamentSize();

	String getSequenceName();

	List<FieldMapping> getFieldMappings();
	
	FieldMapping getFieldMapping(String key);
	
	FieldMapping getCreationTime();
	
	FieldMapping getLastUpdateTime();
	
	int getSessionIdleTime();
	
	int getBatchCount();
	
	long getThreadSleepTime();
	
	AutoSessionCloserListner getSessionCloserListner();
}
