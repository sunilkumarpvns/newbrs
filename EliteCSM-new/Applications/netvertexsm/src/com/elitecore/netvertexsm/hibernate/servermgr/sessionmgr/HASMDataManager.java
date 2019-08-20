/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HASMDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.hibernate.servermgr.sessionmgr;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.ASMDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionManagerDBConfiguration;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.exception.DataNotInSyncException;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;



public class HASMDataManager extends HBaseDataManager implements ASMDataManager{

	private static String MODULE = "ASM DATA MANAGER";

	Connection con = null;
	Connection ipPoolConnection = null;

	public HASMDataManager(){
		/*
		Logger.logInfo(MODULE, "Trying to get a ASM(Active Session Management) database connection.");*/
		if(con==null){
			Logger.logInfo(MODULE, "Trying to get a Server Manager database connection.");
			try{
				con = getSMConnection();
			}catch(Exception e){
				Logger.logError(MODULE, "Failed to get Server Manager database connection :"+e.getMessage());
			}
		}

	}

	public Connection getSMConnection() throws SQLException, ClassNotFoundException, IOException{
		Connection connection;


		FileInputStream fileInputStream = null;

		Properties properties = new Properties();
		File dbPropsFile = new File(EliteUtility.getSMHome()+File.separator+ConfigConstant.DATABASE_CONFIG_FILE_LOCATION);
		fileInputStream = new FileInputStream(dbPropsFile);
		properties.load(fileInputStream);

		Set bundleKeys = properties.keySet();
		Iterator<String> iterator = bundleKeys.iterator();
		String driverClass="";
		String dbUrl="";
		String dbUser="";
		String dbPassword="";

		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = properties.getProperty(key);
			if(key.equalsIgnoreCase("hibernate.connection.driver_class")){
				driverClass=value;
			}
			if(key.equalsIgnoreCase("hibernate.connection.url")){
				dbUrl=value;
			}
			if(key.equalsIgnoreCase("hibernate.connection.username")){
				dbUser=value;
			}
			if(key.equalsIgnoreCase("hibernate.connection.password")){
				dbPassword=value;
			}
		}
		Logger.logInfo(MODULE, "Database Url      :"+dbUrl);
		Logger.logInfo(MODULE, "Database Class    :"+driverClass);
		Logger.logInfo(MODULE, "Database Username :"+dbUser);
		Logger.logInfo(MODULE, "Database Password :"+dbPassword);

		Class.forName(driverClass);
		connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
		Logger.logInfo(MODULE, "connection successfull");
		return connection;

	}
	private void close(ResultSet resultSet){
		try{
			resultSet.close();
		}catch(Exception e){

		}
	}
	private void close(PreparedStatement preparedStatement){
		try{
			preparedStatement.close();
		}catch(Exception e){

		}
	}
	private void close(Connection connection){
		try{
			connection.close();
		}catch(Exception e){

		}
	}
	private void close(Statement statement){
		try{
			statement.close();
		}catch(Exception e){

		}
	}
	public Connection getIPPoolConnection() throws SQLException, ClassNotFoundException, IOException{
		if(ipPoolConnection==null)
			ipPoolConnection = getSMConnection();
		return ipPoolConnection;
	} 	


	private Connection getConnection(SessionManagerDBConfiguration dbConfiguration) throws SQLException, ClassNotFoundException,IllegalAccessException, InstantiationException{
		Connection connection;
		String dbUrl = dbConfiguration.getConnectionUrl();
		String dbUser = dbConfiguration.getDbUserName();
		String dbPassword = dbConfiguration.getDbPassword();
		String driverClass = null;
		if(dbUrl.toLowerCase().contains("oracle"))
			driverClass = "oracle.jdbc.driver.OracleDriver";
		else if(dbUrl.toLowerCase().contains("mysql"))
			driverClass = "com.mysql.jdbc.Driver";
		else if(dbUrl.toLowerCase().contains("postgresql"))
			driverClass = "org.postgresql.Driver";
		Logger.logDebug(MODULE, "db url  :"+dbUrl);
		Logger.logDebug(MODULE, "db user :"+dbUser);

		Class.forName(driverClass).newInstance();
		connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

		Logger.logInfo(MODULE, "DB Connection successfull");
		return connection;
	}

	private void rollback(Connection connection){
		try{
			if(connection!=null && !connection.isClosed()){
				connection.rollback();
			}
		}catch(Exception ex){

		}
	}
	public void closeSession(Long userId,SessionManagerDBConfiguration dbConfiguration) throws DataNotInSyncException,DataManagerException{
		Connection connection = null;
		Statement stmt=null;
		try{
			connection = getConnection(dbConfiguration);
			stmt = connection.createStatement();

			String queryString = "DELETE FROM "+dbConfiguration.getTableName()+"  WHERE "+dbConfiguration.getIdentityField()+"='"+userId+"'";

			stmt.executeUpdate(queryString);
		}
		catch(SQLException sqlexp){
			throw new DataManagerException("Error while closing session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			throw new DataManagerException("Close Session Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			DBUtility.closeQuietly(stmt);
			DBUtility.closeQuietly(connection);
		
				}
		}

	public void closeSession(String[] userIds,SessionManagerDBConfiguration dbConfiguration) throws DataNotInSyncException,DataManagerException{
		Connection connection = null;
		PreparedStatement preparedUpdate = null;
		try{
			connection = getConnection(dbConfiguration);
			connection.setAutoCommit(false);

			preparedUpdate = connection.prepareStatement("DELETE  FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getIdentityField()+"=?");
			for (int i = 0; i < userIds.length; i++) {

				preparedUpdate.setLong(1, Long.parseLong(userIds[i]));
				preparedUpdate.executeUpdate();
				preparedUpdate.clearParameters();
			}

			preparedUpdate.close();
			connection.commit();

		}
		catch(SQLException sqlexp){

			rollback(connection);
			throw new DataManagerException("Error while closing session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			rollback(connection);
			throw new DataManagerException("Close Session Operation Failed.Reason"+e.getMessage(),e);

		}finally{
		  DBUtility.closeQuietly(preparedUpdate);
		  DBUtility.closeQuietly(connection);
				}
		}

	public List<Map<String,Object>> purgeClosedSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		List<Map<String,Object>> purgedSessionsList = new ArrayList<Map<String,Object>>();
		Connection connection = null;
		Connection  ipPoolConn = null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			connection = getConnection(dbConfiguration);
			ipPoolConn = getIPPoolConnection();
			connection.setAutoCommit(false);
			ipPoolConn.setAutoCommit(false);

			stmt = connection.createStatement();

			String strSelectClosedSession = "SELECT * FROM "+dbConfiguration.getTableName();

			rs = stmt.executeQuery(strSelectClosedSession);
			ResultSetMetaData  rsMetadata = rs.getMetaData();

			while(rs.next()){
				Map<String,Object> columnValueMap = new HashMap<String,Object>();
				int columnCount = rsMetadata.getColumnCount();

				for(int i=1;i<=columnCount ;i++){
					Object object  = rs.getObject(i);
					String columnName = rsMetadata.getColumnName(i);
					columnValueMap.put(columnName.toLowerCase(),object);
				}

				purgedSessionsList.add(columnValueMap);
				Object policyKeyObject =columnValueMap.get("uid_policy_key");
				Object ipAddressObject =columnValueMap.get(dbConfiguration.getFramedIpAddressField().toLowerCase());
				String policyKey = null;
				if(policyKeyObject!=null){
					policyKey = policyKeyObject.toString();
				}
				String ipAddress =null;
				if(ipAddressObject!=null){
					ipAddress = ipAddressObject.toString();
				}



			}	


			String queryString = "DELETE  FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getIdentityField()+" IN (SELECT "+dbConfiguration.getIdentityField()+" FROM (SELECT a.*,ROW_NUMBER() OVER (ORDER BY "+dbConfiguration.getIdentityField()+" ) rownumber from "+dbConfiguration.getTableName()+" a ) b WHERE b.rownumber <= 5000)"; 

			stmt.executeUpdate(queryString);
			stmt.close();
			connection.commit();
			ipPoolConn.commit();
			return purgedSessionsList;
		}
		catch(SQLException sqlexp){
			rollback(connection);
			rollback(ipPoolConn);
			throw new DataManagerException("Error while purging closed session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			rollback(connection);
			rollback(ipPoolConn);
			throw new DataManagerException("Purge Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(stmt);
			DBUtility.closeQuietly(connection);
			setAutoCommit(ipPoolConn,true);
		}

	}

	public void setAutoCommit(Connection con, boolean autoCommit){
		if(con!=null){
			try{
				con.setAutoCommit(autoCommit);
			}catch(Exception e){
				Logger.logError(MODULE, "Error in setting auto commit parameter :"+e.getMessage());
			}
		}
	}

	public void purgeAllSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		Connection connection = null;
		Statement stmt=null;
		try{

			connection = getConnection(dbConfiguration);

			stmt = connection.createStatement();


			String queryString = "DELETE from "+dbConfiguration.getTableName();


			stmt.executeUpdate(queryString);
			stmt.close();


		}
		catch(SQLException sqlexp){
			throw new DataManagerException("Purge All Operation Failed.Reason :"+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			throw new DataManagerException("Purge All Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			DBUtility.closeQuietly(stmt);
			DBUtility.closeQuietly(connection);
				}
		}



}
