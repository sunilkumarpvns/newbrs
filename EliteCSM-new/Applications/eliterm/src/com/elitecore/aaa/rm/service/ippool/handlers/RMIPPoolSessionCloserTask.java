package com.elitecore.aaa.rm.service.ippool.handlers;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.alert.AlertSeverity;

public class RMIPPoolSessionCloserTask implements Runnable{

	private static final String ERROR_MESSAGE_WHILE_RELEASING_RESERVED_IP_s = "Error while releasing Reserved IP's. Reason: SQLException occured";
	private static final String MODULE = "RM-IPPool-SessionCloserTask";
	RMIPPoolConfiguration ippoolServiceConfiguration;
	AAAServerContext serverContext;
	DBConnectionManager connectionManager = null;
	public RMIPPoolSessionCloserTask(AAAServerContext Context){
		this.serverContext=Context;
		ippoolServiceConfiguration= ((RMServerConfiguration)serverContext.getServerConfiguration()).getIPPoolConfiguration();
	}

	@Override
	public void run() {
		
		Connection cn = null;
		Statement stmt = null;

		boolean isSuccessReserved = false;
		boolean isSuccessAssigned = false;

		try {
			connectionManager = DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName());
			cn = connectionManager.getConnection();
		} catch (DataSourceException e) {
			serverContext.generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
					MODULE, ERROR_MESSAGE_WHILE_RELEASING_RESERVED_IP_s, 0,
					ERROR_MESSAGE_WHILE_RELEASING_RESERVED_IP_s);
			LogManager.getLogger().trace(MODULE, e);	
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + ippoolServiceConfiguration.getDataSourceName() + " is unavailable while releasing Reserved IP's, Reason: " + e.getMessage());
			}
			return;
		}
		//This block is meant to relase All IP's reserved but not received Acct-Start after the maxreservationtimeout interval.
		try {
			stmt=cn.createStatement();

			int iNumRowUpdate=stmt.executeUpdate(getIPUpdateQueryForReservedIP());

			if (iNumRowUpdate > 0){ 
				isSuccessReserved = true;
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Total no of RESERVED IP's released are " + iNumRowUpdate);

		}catch (SQLException e) {

			LogManager.getLogger().trace(MODULE, e);	
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE," Database Error : while releasing Reserved IP's :"+e.getMessage());
			}
			serverContext.generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
					MODULE, ERROR_MESSAGE_WHILE_RELEASING_RESERVED_IP_s, 0,
					ERROR_MESSAGE_WHILE_RELEASING_RESERVED_IP_s);
		} finally {
			DBUtility.closeQuietly(stmt);
		}

		//This block is meant to release All the IP's that are allocated but not released for more than sessiontimeout configured in the ip-pool-service, for the reason of acct-stop dropped/missed.
		try{
			stmt=cn.createStatement();

			int iNumRowUpdate=stmt.executeUpdate(getIPUpdateQueryForReservedAndAssignedIP());

			if (iNumRowUpdate > 0){ 
				isSuccessAssigned = true;
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Total no of ASSIGNED IP's released are " + iNumRowUpdate);
			}
		}catch (SQLException e) { 
		
			LogManager.getLogger().trace(MODULE, e);	
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Database Error : while releasing assigned IP's :"+e.getMessage());
			}
			serverContext.generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
					MODULE, "Error while releasing Reserved IP's. Reason:  SQLException occured", 0,
					"Error while releasing Reserved IP's. Reason:  SQLException occured");
		}  
		finally {
			try {
				if (isSuccessReserved || isSuccessAssigned) {
					cn.commit();
				}
			} catch (Exception ex) { 
				LogManager.getLogger().trace(ex);
			}
			DBUtility.closeQuietly(stmt);
			DBUtility.closeQuietly(cn);
		}
	}


	public int getReservationTimeOut() {
		return ippoolServiceConfiguration.getReservationTimeoutInterval();
	}
	
	public int getSessionTimeOut() {
		return ippoolServiceConfiguration.getSessionTimeoutInterval();
	}
	private int getMaxBatchSize() {
		return 	ippoolServiceConfiguration.getMaxBatchSize();
	}
	
	public boolean isFixedDelay() {
		return true;
	}
	
	private String getIPUpdateQueryForReservedAndAssignedIP(){
		
		String IPupdateQuery = "UPDATE tblmippooldetail SET assigned ='N', reserved='N', last_updated_time = systimestamp  where reserved='Y' and assigned='Y' and last_updated_time + interval '" + getSessionTimeOut() + "' second < SYSTIMESTAMP and rownum <= "+getMaxBatchSize();
		
		if (DBVendors.POSTGRESQL.equals(connectionManager.getVendor())) {
			IPupdateQuery = "UPDATE tblmippooldetail iptab SET assigned ='N', reserved='N',last_updated_time= current_timestamp "
					+ "from (select ippoolid,serialnumber from tblmippooldetail where reserved='Y' and assigned='Y' "
					+ "AND last_updated_time + interval '" + getSessionTimeOut() + "' second < current_timestamp limit "+getMaxBatchSize()+") tab"
					+ " where tab.ippoolid = iptab.ippoolid and tab.serialnumber = iptab.serialnumber"
					+ " and iptab.reserved='Y' and iptab.assigned='Y' AND iptab.last_updated_time + interval '" + getSessionTimeOut() + "' second < current_timestamp;";
}
		return IPupdateQuery;
		
	}
	
	private String getIPUpdateQueryForReservedIP(){
		
		String IPupdateQuery = "UPDATE tblmippooldetail SET reserved ='N', last_updated_time = systimestamp  where reserved='Y' and assigned='N' and last_updated_time + interval '" + getReservationTimeOut() + "' second < SYSTIMESTAMP and rownum <= "+getMaxBatchSize();
		
		if (DBVendors.POSTGRESQL.equals(connectionManager.getVendor())) {
			IPupdateQuery = "UPDATE tblmippooldetail iptab SET reserved='N',last_updated_time= current_timestamp "
					+ "from (select ippoolid,serialnumber from tblmippooldetail where reserved='Y' and assigned='N' "
					+ "AND last_updated_time + interval '" + getReservationTimeOut() + "' second < current_timestamp limit "+getMaxBatchSize()+") tab"
					+ " where tab.ippoolid = iptab.ippoolid and tab.serialnumber = iptab.serialnumber"
					+ " and iptab.reserved='Y' and iptab.assigned='N' AND iptab.last_updated_time + interval '" + getReservationTimeOut() + "' second < current_timestamp;";
		}
		return IPupdateQuery;
	}
}
