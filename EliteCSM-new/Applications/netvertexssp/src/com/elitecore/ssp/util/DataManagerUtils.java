package com.elitecore.ssp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.webservice.WebServiceManager;

public class DataManagerUtils {
	
	private Connection smConnection = null;

	private static final String MODULE = DataManagerUtils.class.getSimpleName();

	private Connection getSMDatabaseConection(){
		Connection connection = null;
		try {
			Class.forName(WebServiceManager.getInstance().getTelenorDriverClass());
			connection = DriverManager.getConnection(WebServiceManager.getInstance().getTelenorConnectionURL(),
					WebServiceManager.getInstance().getTelenorDatabaseUsername(), WebServiceManager.getInstance().getTelenorDatabasePassword());
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException ex) {

			ex.printStackTrace();
		}
		
		return connection;
	}
	
	public ResultSet executeQuery(String query, Connection connection) throws Exception{
		ResultSet resultSet = null;
		Statement statement = null;
		try{
			if(connection == null || connection.isClosed()){
				connection = getSMDatabaseConection();
			}
				
			statement = smConnection.createStatement();
			
			Logger.logDebug(MODULE, query);
			
			resultSet = statement.executeQuery(query);
						
		}catch (Exception ex){
			Logger.logError(MODULE,"Exception in executeQuotaQuery() : "+ex.getMessage());
			Logger.logError(MODULE, ex);
			ex.printStackTrace();
			throw ex;
		}
		return resultSet;
	}
	
	public String getSubscriberIdFromUserName(String userName) throws Exception{
		String subscriberId = null;
		String query = "select subscriberidentity from tblnetvertexcustomer where username ='" + userName+ "'";
		ResultSet rs = executeQuery(query, getDatabaseConnection());
		if(rs != null){
			while(rs.next()){
				subscriberId = rs.getString("subscriberidentity");
				break;
			}
		}
		return subscriberId;
	}
	
	public Map<String, String> getUsersRequestsForQuotaTransfer(String userName) throws Exception{
		ResultSet resultSet = null;
		Map<String, String> requestMap = new HashMap<String, String>();
		String query = "select subscriberidentity, param8 from tblnetvertexcustomer where param7='" + userName + "'";
		Logger.logDebug(MODULE, query);
		try{
			resultSet = executeQuery(query, getDatabaseConnection());
			if(resultSet != null){
				while(resultSet.next()){
					String user = resultSet.getString("subscriberidentity");
					String quota = resultSet.getString("param8");
					if(user != null && quota != null){
						
						long quotaInMB = ChildAccountUtility.convertBytesToMB(Long.parseLong(quota));
						requestMap.put(user, String.valueOf(quotaInMB));
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		} finally{
			closeQuietly(resultSet);
			closeQuietly(smConnection);
		}
		return requestMap;
	}
	
	public long getUsersBalanceQuota(String subscriberId){
		long balance = 0L;
		ResultSet resultSet = null;
		Statement statement = null;
		
		
		try{
			String usageQuery = "select totaloctets from tblmsessionusagesummary where userid ='" + subscriberId + "'";
			String hsqValueQuery = "select hsqvalue from tblmpolicygroup where name in " +
					"(select subscriberpackage from tblnetvertexcustomer where subscriberidentity = '" + subscriberId + "')";

			getDatabaseConnection();
			statement = smConnection.createStatement();
			Logger.logDebug(MODULE, usageQuery);
			resultSet = statement.executeQuery(usageQuery);
			Logger.logDebug(MODULE, usageQuery);
			String totalOctets = null;
			if(resultSet != null) {
				while(resultSet.next()) {
					totalOctets = resultSet.getString("totaloctets");
					break;
				}
			}
			
			Logger.logDebug(MODULE, hsqValueQuery);
			
			String hsqValue = null;
			resultSet = statement.executeQuery(hsqValueQuery);
			if(resultSet != null) {
				while(resultSet.next()) {
					hsqValue = resultSet.getString("hsqvalue");
					break;
				}
			}
			long totalOctetsLong = totalOctets != null ? Long.parseLong(totalOctets): 0L;
			long hsqLong = hsqValue != null ? Long.parseLong(hsqValue): 0L;
			balance = hsqLong - totalOctetsLong;
			return balance > 0 ? balance: 0L;
						
		}catch (Exception ex){
			Logger.logError(MODULE,"Exception in getUsersBalanceQuota() : "+ex.getMessage());
			Logger.logError(MODULE, ex);
			ex.printStackTrace();
		} finally {
			closeQuietly(statement);
			closeQuietly(smConnection);
		}
		return balance;
	}
	
	
	
	
	public Connection getDatabaseConnection() throws SQLException{
		if(smConnection == null || smConnection.isClosed()){
			smConnection = getSMDatabaseConection();
		}
		
		return smConnection;
	}
	
	
	public void executeUpdateOrInsert(String query) throws Exception{
		Statement statement = null;
		try{
			if(smConnection == null || smConnection.isClosed()){
				smConnection = getSMDatabaseConection();
			}
				
			statement = smConnection.createStatement();
			Logger.logDebug(MODULE, query);
			statement.executeUpdate(query);

		}catch (Exception ex){
			Logger.logError(MODULE,"Exception in executeQuotaQuery() : "+ex.getMessage());
			Logger.logError(MODULE, ex);
			ex.printStackTrace();
			throw ex;
		}finally{
			closeQuietly(statement);
			closeQuietly(smConnection);
		}
	}
	
	public static void closeQuietly(Statement statement) {
        try{
            if(statement != null && !statement.isClosed()) {
                statement.close();
            }
        }catch(SQLException e) { }
	 }
	 
	 public static void closeQuietly(Connection connection) {
		 try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
	      }catch(SQLException e) { }
	 }
	 
	 public static void closeQuietly(ResultSet resultSet) {
	 	 try {
            if(resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
	     }catch(SQLException e) { }
	 }
	
}
