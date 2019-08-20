package com.elitecore.commons.kpi.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.data.Entry;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class InsertTask extends BaseIntervalBasedTask {

	private static final String MODULE = "KPI-SERVICE-EXECUTOR";
	private KpiConfiguration kpiConfig;
	private List<InsertData> insertDatas;
	private int batchSize;
	private ConnectionProvider connectionProvider;
	private int executeCount;
	
	public InsertTask(List<InsertData> insertDatas, KpiConfiguration kpiConfig, ConnectionProvider connectionProvider) {
		this.kpiConfig = kpiConfig;
		this.insertDatas = insertDatas;
		this.batchSize = kpiConfig.getBatchSize();
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	public long getInterval() {
		return kpiConfig.getDumpInterval();
	}

	@Override
	public void execute() {
		Connection connection = null;
		try {
			connection = connectionProvider.getConnection();
			
			if(connection == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Problem in getting connection, Reason: DB Connection is null");
				return;
			}
			
			connection.setAutoCommit(false);
			
			List<Entry> batches = null;

			for(int i=0 ; i<insertDatas.size() ; i++) {
				
				InsertData insertData = insertDatas.get(i);
				
				if(insertData == null) {
					continue;
				}
				
				do {
					batches = insertData.executeInsert(connection, executeCount);

					if(batches.isEmpty()) {
						break;
					}
					
					executeCount += batches.size();
					
					if(executeCount == batchSize || (i == insertDatas.size()-1 && executeCount < batchSize)) {
						connection.commit();
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, executeCount + " records are committed");
		
						executeCount = 0;
					}

					insertData.remove(batches);
					/*
					 * Need to check after commit because only once less than batch size records can be committed 
					 */
				} while(insertData.isEntriesLeft());
			}

		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in committing batch , Reason: " + e.getMessage());
				
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Task to insert SNMP counters executed");
		}
	}
}
