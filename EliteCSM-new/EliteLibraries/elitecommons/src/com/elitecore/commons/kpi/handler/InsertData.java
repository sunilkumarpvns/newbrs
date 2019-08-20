package com.elitecore.commons.kpi.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.data.Entry;
import com.elitecore.commons.kpi.data.Value;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class InsertData {
	
	private static final String MODULE = "INSERT_DATA";
	private static final int MAX_BATCH_CAPACITY = 1000;
	private String query;
	private ConcurrentLinkedQueue<Entry> entries;
	private KpiConfiguration kpiConfig;
	private String tableName;
	private int size;

	public InsertData(String query, KpiConfiguration kpiConfig, String tableName) {
		this.query = query;
		this.kpiConfig = kpiConfig;
		this.tableName = tableName;
		this.entries = new ConcurrentLinkedQueue<Entry>();
	}
	
	public List<Entry> executeInsert(Connection connection, int executeCount) {
		
		if(entries.isEmpty()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Not enought entries to commit for table: " + tableName);
			
			return Collections.emptyList();
		}
		
		int batchSize = kpiConfig.getBatchSize();

		Iterator<Entry> iterator = entries.iterator();
		List<Entry> currentEntries = new ArrayList<Entry>(); 
		PreparedStatement prepareStatement = null;

		try {
			prepareStatement = connection.prepareStatement(query);

			for(int recIndex=0 ; recIndex<(batchSize-executeCount) && iterator.hasNext(); recIndex++) {
				Entry entry = (Entry) iterator.next();
				List<Value> values = entry.getValues();
				for(int valueIndex=0 ; valueIndex<values.size() ; valueIndex++) {
					
					Value value = values.get(valueIndex);
					int type = value.getType();
					Object obj = values.get(valueIndex).getValue();
					
					if(java.sql.Types.BIGINT == type) {
						prepareStatement.setLong(valueIndex+1, (Long) obj);
					} else if(java.sql.Types.INTEGER == type) {
						prepareStatement.setInt(valueIndex+1, (Integer) obj);
					} else if(java.sql.Types.VARCHAR == type) {
						prepareStatement.setString(valueIndex+1, String.valueOf(obj));
					} else if(java.sql.Types.TIMESTAMP == type) {
						prepareStatement.setTimestamp(valueIndex+1, (Timestamp) obj);
					} else if(java.sql.Types.DOUBLE == type) {
						prepareStatement.setDouble(valueIndex+1, (Double) obj);
					} else {
						prepareStatement.setObject(valueIndex+1, obj);
					}
				}
				prepareStatement.addBatch();
				currentEntries.add(entry);
			}
			
			int[] executeBatch = prepareStatement.executeBatch();
//			prepareStatement.clearParameters();				// uncomment if number of columns keeps on changing 

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, executeBatch.length + " records inserted in table: " + tableName);
		
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in dumping data in table: " + tableName + ", Reason: " + e.getMessage());
			return Collections.emptyList();

		} catch (Exception e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) 
				LogManager.getLogger().trace(MODULE, "Batch execution for table: " + getTableName() + " failed, Reason: " + e.getMessage());
			return Collections.emptyList();
		
		} finally {
			DBUtility.closeQuietly(prepareStatement);
		}
		return currentEntries;
	}

	public synchronized void remove(List<Entry> entriesToRemove) {
		entries.removeAll(entriesToRemove);
		size = entries.size();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Number of batches left to dump: " + size);
	}
	
	public synchronized void addRecords(Entry entry) {
		if(entry == null) {
			return;
		}
		entries.add(entry);
	}

	public String getTableName() {
		return tableName;
	}
	
	public boolean isEntriesLeft() {
		return size != 0;
	}

	public boolean isLimitReached() {
		return entries.size() >= MAX_BATCH_CAPACITY;
	}
}
