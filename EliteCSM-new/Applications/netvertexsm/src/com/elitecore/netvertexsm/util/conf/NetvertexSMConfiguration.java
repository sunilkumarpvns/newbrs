package com.elitecore.netvertexsm.util.conf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.util.driver.DriverConfiguration;
import com.elitecore.netvertexsm.util.driver.cdr.conf.impl.CSVDriverConfigurationImpl;
import com.elitecore.netvertexsm.util.driver.cdr.conf.impl.DBCDRDriverConfigurationImpl;
import com.elitecore.netvertexsm.web.datasource.database.DBDataSourceImpl;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;
import com.elitecore.netvertexsm.ws.logger.Logger;


public class NetvertexSMConfiguration {
	
	private static final String MODULE = "NVSM-CNF";
	
	private List<DriverConfiguration> driverConfigurations;
	private Map<String, DBDataSource> datasourceMap;
	private Map<String,DBDataSource> datasourceNameMap;
	
	private static final int CONNECTION_READ_TIMEOUT = 60 * 1000;


	public NetvertexSMConfiguration() {
		driverConfigurations = new ArrayList<DriverConfiguration>();
		datasourceMap = new HashMap<String, DBDataSource>();
		datasourceNameMap = new HashMap<String, DBDataSource>();
	}

	public void readConfiguration() {
		//FIXME temporary disable reading configuration of CDR driver associated with WS --ishani.bhatt
		//readCDRDriverConfiguration();
		readDBDSConfiguration();
	}

	private void readCDRDriverConfiguration() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try{
			List<DriverConfiguration> driverConfigurations = new ArrayList<DriverConfiguration>();
			String query = "SELECT WS.BODCDRDRIVERID, DI.DRIVERTYPEID as BODDRVTYPEID FROM TBLWSCONFIG WS, TBLMDRIVERINSTANCE DI WHERE WS.BODCDRDRIVERID=DI.DRIVERINSTANCEID";
			connection = DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection == null) {
				throw new LoadConfigurationException("No connection availabe while reading CDR driver configuration");
			}
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if(resultSet.next()) {
				int driverInstanceId = resultSet.getInt("BODCDRDRIVERID");
				int driverType = resultSet.getInt("BODDRVTYPEID");

				try {
					DriverConfiguration configuration = getDriverConfiguration(driverInstanceId, driverType);
					if(configuration != null) {
						driverConfigurations.add(configuration);
					}
				} catch (Exception e) {
					Logger.logError(MODULE, "Error in reading CDR driver configuration. Reason: " + e.getMessage());
		        	Logger.logTrace(MODULE, e);
				}
			}
			this.driverConfigurations = driverConfigurations;
        } catch (SQLException ex) {
        	Logger.logError(MODULE, "Error in reading CDR driver configuration. Reason: " + ex.getMessage());
        	Logger.logTrace(MODULE, ex);
		} catch (LoadConfigurationException ex) {
			Logger.logError(MODULE, "Error in reading CDR driver configuration. Reason: " + ex.getMessage());
        	Logger.logTrace(MODULE, ex);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}		
	}
	
	/**
	 * Read the configuration information from the DB
	 */
	private void readDBDSConfiguration() {
	

		Logger.logInfo(MODULE,"Read configuration operation started for Database DS");
		
		Map<String, DBDataSource> tmpDatasourceMap = new HashMap<String, DBDataSource>();
		Map<String, DBDataSource> tempDatasourceNameMap = new HashMap<String, DBDataSource>();
		Connection connection = null;
		PreparedStatement stmntDBDs = null;
		ResultSet rsDBDS = null;
		try{
			DBDataSource dbDataSource;
			connection =  DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection==null){
				throw new LoadConfigurationException("Problem reading database ds configurations. Reason: configuration database connection not found");
			}
			
			String query="select DATABASEDSID,NAME,CONNECTIONURL,USERNAME,PASSWORD,MINIMUMPOOL,MAXIMUMPOOL from TBLMDATABASEDS";
			stmntDBDs = connection.prepareStatement(query);
			rsDBDS = stmntDBDs.executeQuery();
			while(rsDBDS.next()){
				String dsID = String.valueOf(rsDBDS.getInt("DATABASEDSID"));
				String dsName = rsDBDS.getString("NAME");
				String connURL = rsDBDS.getString("CONNECTIONURL");
				String userName = rsDBDS.getString("USERNAME");
				
				if(userName == null){
					Logger.logDebug(MODULE,"Username is not specified in "+dsName+".");
					userName = "";
				}
				
				String password = rsDBDS.getString("PASSWORD");
				if(password == null){
					Logger.logDebug(MODULE,"password is not specified in "+dsName+".");
					password = "";
				}
				int minPoolSize = rsDBDS.getInt("MINIMUMPOOL");
				int maxPoolSize = rsDBDS.getInt("MAXIMUMPOOL");
				dbDataSource = new DBDataSourceImpl(dsID,dsName,connURL,userName,password,minPoolSize,maxPoolSize,DBDataSourceImpl.DEFAULT_STATUS_CHECK_DURATION,CONNECTION_READ_TIMEOUT);
				tmpDatasourceMap.put(dsID, dbDataSource);
				tempDatasourceNameMap.put(dsName, dbDataSource);
			}
			datasourceMap = tmpDatasourceMap;
			datasourceNameMap = tempDatasourceNameMap;
			Logger.logInfo(MODULE, toString());
			Logger.logInfo(MODULE,"Read database datasource configuration completed ");
			
		} catch(SQLException sqlEx) {
			Logger.logTrace(MODULE,sqlEx);
			Logger.logError(MODULE, "Error while reading database datasource. reason : " + sqlEx.getMessage());
		} catch (LoadConfigurationException ex) {
			Logger.logTrace(MODULE,ex);
			Logger.logError(MODULE, "Error while reading database datasource. reason : " + ex.getMessage());
		} catch (Exception ex) {
			Logger.logTrace(MODULE,ex);
			Logger.logError(MODULE, "Error while reading database datasource. reason : " + ex.getMessage());
		} finally {
			DBUtility.closeQuietly(rsDBDS);
			DBUtility.closeQuietly(stmntDBDs);
			DBUtility.closeQuietly(connection);
				}
			}

	private DriverConfiguration getDriverConfiguration(int driverInstanceId, int driverType) throws LoadConfigurationException {
		switch (driverType) {
			case DriverTypes.CSV_CDR_DRIVER :
				DriverConfiguration csvDriverConf = new CSVDriverConfigurationImpl(driverInstanceId);
				csvDriverConf.readConfiguration();
				return csvDriverConf;
				
			case DriverTypes.DB_CDR_DRIVER :
				DriverConfiguration dbCDRDriverConf = new DBCDRDriverConfigurationImpl(driverInstanceId);
				dbCDRDriverConf.readConfiguration();
				return dbCDRDriverConf;
			     
			default :
				return null;
		}
	}

	public List<DriverConfiguration> getDriverConfigurations() {
		return driverConfigurations;
	}
	
	public DBDataSource getDatasource(String dsID) {
		return datasourceMap.get(dsID);
	}

	public DBDataSource getDatasourceByName(String datasourceName) {
		return datasourceNameMap.get(datasourceName);
	}
}
