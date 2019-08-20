package com.elitecore.netvertex.service.offlinernc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;

public class DailyUsageDbListener implements DailyUsageListener {
	
	private static final String MODULE = "DAILY-USAGE-DB-LISTENER";
	private final String insertQuery;
	private final String updateQuery;
	
	private String tableName;
	private DBConnectionManager connMgr;

	public DailyUsageDbListener(String table, DBConnectionManager connMgr) {
		this.tableName = table;
		this.connMgr = connMgr;
		this.insertQuery = buildInsertQuery();
		this.updateQuery = buildUpdateQuery();
	}

	@Override
	public void recordStats(DailyUsageStats dailyUsageStats) {
			try (Connection conn = connMgr.getConnection();
					PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
					PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

				updateStmt.setBigDecimal(1, dailyUsageStats.getUsage());
				updateStmt.setString(2, dailyUsageStats.getAccountName());
				updateStmt.setDate(3, dailyUsageStats.getUsageDate());
				if (updateStmt.executeUpdate() == 0) {
					insertStmt.setString(1, dailyUsageStats.getAccountName());
					insertStmt.setDate(2, dailyUsageStats.getUsageDate());
					insertStmt.setBigDecimal(3, dailyUsageStats.getUsage());
					if (insertStmt.executeUpdate() == 0)  {
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().warn(MODULE,"Failed to insert record for account: " + dailyUsageStats.getAccountName());
						}
					} else {
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE,"Successfully inserted record for account: " + dailyUsageStats.getAccountName());
						}
					}
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE,"Successfully update record of account: " + dailyUsageStats.getAccountName());
					}
				}
			
				conn.commit();
			} catch (SQLException e) {
				LogManager.getLogger().trace(e);
				LogManager.getLogger().warn(MODULE, "Failed to insert records of account: " + dailyUsageStats.getAccountName());
			}
	}
	
	private String buildInsertQuery() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ")
			.append(tableName)
			.append(" (ACCOUNTNAME, USAGEDATE, UNBILLEDUSAGE) VALUES(?,?,?)");
		return builder.toString();
	}

	private String buildUpdateQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE ")
			.append(tableName)
			.append(" set UNBILLEDUSAGE = UNBILLEDUSAGE + ? where ACCOUNTNAME = ? AND USAGEDATE = ?");
		return builder.toString();
	}
}
