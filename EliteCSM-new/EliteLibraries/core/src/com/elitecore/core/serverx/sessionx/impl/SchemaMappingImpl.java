package com.elitecore.core.serverx.sessionx.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.serverx.sessionx.AutoSessionCloserListner;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;

public class SchemaMappingImpl implements SchemaMapping {
	private String tableName;
	private String schemaName;
	private int idGenerator;
	private String sequenceName;
	private int intialSequenceValue;
	private int seqIncreamentSize;
	private FieldMapping id;
	private FieldMapping creationTime;
	private FieldMapping lastUpdateTime;	
	private int sessionIdleTime;
	private int batchCount;
	private long threadSleepTime;
	private List<FieldMapping> fieldMappingList;
	private Map<String,FieldMapping> fieldMappings;
	private AutoSessionCloserListner sessionCloserListner;
	public static final String SESSION_TIMEOUT_MANDATORY_FIELD = "session-timeout";
	
	// Mandatory field Mapping field-name
	
	public final static String SESSION_ID="Session ID";
	public final static String GROUP_NAME="Group Name";
	public final static String SERVICE_TYPE="Service Type";
	public final static String SESSION_TIMEOUT="Session Timeout";
	public final static String USER_IDENTITY="User Identity";
	
	
	public SchemaMappingImpl(String idField, String creationTimeField, String lastUpdateTimeField) {
		this.id = new FieldMappingImpl(FieldMapping.STRING_TYPE, "id", idField);
		this.creationTime = new FieldMappingImpl(FieldMapping.TIMESTAMP_TYPE, CREATION_TIME, creationTimeField);
		this.lastUpdateTime = new FieldMappingImpl(FieldMapping.TIMESTAMP_TYPE, LAST_UPDATE_TIME, lastUpdateTimeField);
		fieldMappingList = new  ArrayList<FieldMapping>();
		this.fieldMappings = new HashMap<String, FieldMapping>();
//		sessionIdleTime = 2;
//		batchCount = 4;
//		threadSleepTime = 30000;
	}
	
	public SchemaMappingImpl(String idField, String creationTimeField, String lastUpdateTimeField, AutoSessionCloserListner sessionCloserListner) {
		this(idField, creationTimeField, lastUpdateTimeField);
		this.sessionCloserListner = sessionCloserListner;
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public int getIdGenerator() {
		return idGenerator;
	}
	public void setIdGenerator(int idGenerator) {
		this.idGenerator = idGenerator;
	}
	
	@Override
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	@Override
	public int getIntialSequenceValue() {
		return intialSequenceValue;
	}
	public void setIntialSequenceValue(int intialSequenceValue) {
		this.intialSequenceValue = intialSequenceValue;
	}
	
	@Override
	public int getSeqIncreamentSize() {
		return seqIncreamentSize;
	}
	public void setSeqIncreamentSize(int seqIncreamentSize) {
		this.seqIncreamentSize = seqIncreamentSize;
	}
	
	@Override
	public FieldMapping getIdFieldMapping() {
		return id;
	}

	@Override
	public FieldMapping getCreationTime() {
		return creationTime;
	}
	
	@Override
	public FieldMapping getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	@Override
	public List<FieldMapping> getFieldMappings() {
		return fieldMappingList;
	}
	public void setFieldMappings(List<FieldMapping> fieldMappingList) {
		this.fieldMappingList = fieldMappingList;
		fieldMappings.clear();
		for(FieldMapping fieldMapping:fieldMappingList){
			fieldMappings.put(fieldMapping.getColumnName(), fieldMapping);
			fieldMappings.put(fieldMapping.getPropertyName(), fieldMapping);
		}
	}
	
	public void addFieldMapping(FieldMapping fieldMapping){
		fieldMappingList.add(fieldMapping);
		fieldMappings.put(fieldMapping.getColumnName(), fieldMapping);
		fieldMappings.put(fieldMapping.getPropertyName(), fieldMapping);
	}
	
	public FieldMapping getFieldMapping(String key){
		return fieldMappings.get(key);
	}
	
	public void setSessionIdleTime(int sessionIdleTime) {
		this.sessionIdleTime = sessionIdleTime;
	}
	
	@Override
	public int getSessionIdleTime() {
		return sessionIdleTime;
	}
	
	@Override
	public AutoSessionCloserListner getSessionCloserListner() {
		return sessionCloserListner;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public long getThreadSleepTime() {
		return threadSleepTime;
	}

	public void setThreadSleepTime(long threadSleepTime) {
		this.threadSleepTime = threadSleepTime;
	}

	@Override
	public String getSchemaName() {
		return this.schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
}
