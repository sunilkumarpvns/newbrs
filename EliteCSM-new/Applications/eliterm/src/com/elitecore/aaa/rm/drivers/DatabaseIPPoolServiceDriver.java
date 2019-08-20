package com.elitecore.aaa.rm.drivers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolService;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolServiceContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.util.queue.ConcurrentLinkedQueue;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class DatabaseIPPoolServiceDriver extends BaseDriver{

	private static final String EXECPTION_MESSAGE_FOR_DATABASE_DOWN_SYSTEM_MARKED_AS_DEAD = "Database is Down, System marked as dead. Reason: ";
	private static final String EXCEPTION_MESSAGE_FOR_DATASOURCE_TYPE_IS_NOT_SUPPORTED = "Datasource Type is not Supported.";
	private static final String POSTGRESQL_COMMIT_STAMEMENT = "COMMIT";
	private static final String ORACLE_COMMIT_STATEMENT = "COMMIT WORK WRITE NOWAIT";
	private static final String MODULE = "Database-IPPool-Driver";
	RMIPPoolServiceContext serviceContext;
	RMIPPoolConfiguration ippoolServiceConfiguration;
	private static final String IP_ADDRESS_RELEASE_MESSAGE = "ipAddress release";
	private static final String IP_UPDATE_MESSAGE = "ipAddress update";

	private static final int DATABASE_UNIQUE_CONSTRAINTS = 1;
	private static final int QUERY_TIMEOUT_ERRORCODE = 1013;
	private static final int DEFAULT_QUERY_TIMEOUT = 10;
	private int dbQueryTimeOut;
	
	ConcurrentLinkedQueue<PoolDetail> queueForIpAddress;
	ConcurrentLinkedQueue<PoolDetail> queueForSerialID;
	private HashMap<String, TBLMIPPOOL_class> hashMapIPPool;
	private List<TBLMIPPOOL_class> ipPoolList;

	private Map<String, String> ippoolIDToName;
	private DBVendors dbVendor = null;
	
	public DatabaseIPPoolServiceDriver(RMIPPoolServiceContext serviceContext) {
		super(serviceContext.getServerContext());
		this.serviceContext=serviceContext;
		ippoolServiceConfiguration=serviceContext.getIPPoolConfiguration();
		this.dbQueryTimeOut  =ippoolServiceConfiguration.getDbQueryTimeOut();
		queueForIpAddress = new ConcurrentLinkedQueue<PoolDetail>(50000);
		queueForSerialID = new ConcurrentLinkedQueue<PoolDetail>(50000);
		ipPoolList = new ArrayList<DatabaseIPPoolServiceDriver.TBLMIPPOOL_class>();
		this.ippoolIDToName = new HashMap<String, String>();
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		try {
			initDataSource();
			AcctIntrimBatchExecutor batchExecutor = new AcctIntrimBatchExecutor(2);
			getServerContext().getTaskScheduler().scheduleIntervalBasedTask(batchExecutor);
			cacheIPPools();
		}catch(DriverInitializationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE, " Fail to Initialize Driver , Reason : "+e.getMessage());
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Fail to Initialize Driver ", 0,
					"Fail to Initialize Driver ");
			throw e;
		}

	}

	private void initDataSource() throws DriverInitializationFailedException{

		DBDataSource dataSource = ((AAAServerContext)getServerContext()).getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(ippoolServiceConfiguration.getDataSourceName());
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dataSource.getDataSourceName());
		try{
			dbConnectionManager.init(dataSource, getServerContext().getTaskScheduler());
			dbVendor = dbConnectionManager.getVendor();
		}catch (DatabaseInitializationException e) {
			markDead();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, e.getMessage());
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Datasource initialization Failed.", 0, 
					"Datasource initialization Failed.");

		}catch (DatabaseTypeNotSupportedException e) {
			markDead();
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, EXCEPTION_MESSAGE_FOR_DATASOURCE_TYPE_IS_NOT_SUPPORTED,0,
					EXCEPTION_MESSAGE_FOR_DATASOURCE_TYPE_IS_NOT_SUPPORTED);
			throw new DriverInitializationFailedException(EXCEPTION_MESSAGE_FOR_DATASOURCE_TYPE_IS_NOT_SUPPORTED,e);
		}
		catch(Exception e) {
			markDead();
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Driver Datasource Initialization failed.", 0,
					"Driver Datasource Initialization failed.");
			throw new DriverInitializationFailedException("Driver Datasource Initialization faild.",e);
		}finally{
			TransactionFactory transactionFactory = dbConnectionManager.getTransactionFactory();
			transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {

				@Override
				public void alive(ESCommunicator esCommunicator) {
					DatabaseIPPoolServiceDriver.this.markAlive();
				}

				@Override
				public void dead(ESCommunicator esCommunicator) {
					DatabaseIPPoolServiceDriver.this.markDead();
					
				}
			});
		}
	}
	
	private void cacheIPPools() {

		Connection  cn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String additionalAttr = null;
		TBLMIPPOOL_class tblmippool_class=null;
		HashMap<String, TBLMIPPOOL_class>hashMapIPPool=new HashMap<String, TBLMIPPOOL_class>();
		List<TBLMIPPOOL_class>hashMapIPPoolList = new ArrayList<TBLMIPPOOL_class>();
		Map<String, String> tempIppoolIDToName = new HashMap<String, String>();

		try{
			cn = getDSConnection();
			final String PoolCacheQuery="select * from TBLMIPPOOL where COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'";
			preparedStatement = cn.prepareStatement(PoolCacheQuery);

			if(preparedStatement == null){
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
						MODULE, "PreparedStatement is not available", 0,
						"PreparedStatement is not available");
				
				throw new SQLException("PreparedStatement is not available");
				}

			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){

				String iPPoolName=resultSet.getString("NAME");
				String poolId =  resultSet.getString("IPPOOLID");
				String nasIp = resultSet.getString("NASIPADDRESS");

				tblmippool_class=new TBLMIPPOOL_class(poolId , iPPoolName, nasIp);

				additionalAttr = resultSet.getString("ADDITIONALATTRIBUTES");
				tblmippool_class.additionalResponseAttributes = new AdditionalResponseAttributes(additionalAttr);

				hashMapIPPool.put(iPPoolName, tblmippool_class);
				hashMapIPPoolList.add(tblmippool_class);
				tempIppoolIDToName.put(poolId, iPPoolName);
			}
			this.hashMapIPPool = hashMapIPPool;
			this.ipPoolList = hashMapIPPoolList; 
			this.ippoolIDToName = tempIppoolIDToName;
		}catch (DataSourceException e) {
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Connection to datasource: " + ippoolServiceConfiguration.getDataSourceName() 
						+ " is unavailable, Reason: " + e.getMessage(), 0, 
						"Connection to datasource: " + ippoolServiceConfiguration.getDataSourceName() 
						+ " is unavailable, Reason: " + e.getMessage());
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Failed to initialize pool cache. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}catch (SQLException ex) {

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Failed to initialize pool cache. Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(MODULE, ex);

			int sqlErrorCode = ex.getErrorCode();
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(ex) || sqlErrorCode == QUERY_TIMEOUT_ERRORCODE) {
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
				
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DATABASEDOWN, MODULE, 
						EXECPTION_MESSAGE_FOR_DATABASE_DOWN_SYSTEM_MARKED_AS_DEAD + ex.getMessage(), 
						0, ippoolServiceConfiguration.getDataSourceName());
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Database is Down, System marked as dead.");
				}
			}else if(sqlErrorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUNIQUECONSTRAINTS, 
						MODULE, "Unique Constraint Violated in database.", 0, 
						ippoolServiceConfiguration.getDataSourceName() + "(" + MODULE + ")");
			}

		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Failed to initialize pool cache. Reason: " + e.getMessage());
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASE_GENERIC, 
					MODULE, "Failed to initialize pool cache ", 0,
					"Failed to initialize pool cache");
		}
		finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(cn);
		}

	}

	public Connection getDSConnection() throws DataSourceException {
		return DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getConnection();
	}

	public PoolDetail getPoolDetail(List<String> poolNameList,RadServiceRequest request,RadServiceResponse response) throws DriverProcessFailedException{

		Connection cn = null;
		CallableStatement cstmt = null;
		PoolDetail poolDetail=null;
		boolean isSuccess = false;

		try{
			cn = this.getDSConnection();
			
			if(cn == null){
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, MODULE, "Connection to Database is not available, Database connection MAX limit reached");
				throw new SQLException("Connection to Database is not available, Database connection MAX limit reached");
			}
			
			/**
			 * Procedure signature : 
			 * sp_free_aaa_ip (I_IPPOOLIDs,O_IPPOOLID,SERIALNUMBER,IPADDRESS,CALLING_STATION_ID,USER_IDENTITY,NAS_IP_ADDRESS,NAS_ID)
			 */

			String ipPoolDetailQuery = "{call sp_free_aaa_ip(?,?,?,?,?,?,?,?)}" ;
			
			cstmt = cn.prepareCall(ipPoolDetailQuery);
			
			if (cstmt == null) {
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
						MODULE, "Problem in Allocating IP Address. Reason: PreparedStatement is not available", 0,
						"Problem in Allocating IP Address. Reason: PreparedStatement is not available");
				throw new SQLException("Problem in Allocating IP Address. Reason : PreparedStatement is not available");
			}

			if (this.dbQueryTimeOut > 0) {
				cstmt.setQueryTimeout(dbQueryTimeOut);
			}
			
			String strPoolNames = "";
			
			for (int i=0 ; i < poolNameList.size() ; i++) {

				StringBuilder poolIDsToLookUp = new StringBuilder();
				strPoolNames = poolNameList.get(i);

				StringTokenizer st = new StringTokenizer(strPoolNames, ",;");

				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "IP-Pool(s) to be looked up for IPAddress from configuration is: " + strPoolNames);
				}

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (hashMapIPPool.containsKey(token)){
						String poolId = this.hashMapIPPool.get(token).getIPPOOLID();
						poolIDsToLookUp.append(poolId).append(",");
					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE,"No configuration detail found for IP-Pool: "+token);
						}
					}
				}

				int length = poolIDsToLookUp.length();
				if (poolIDsToLookUp.charAt(length - 1) == ',') {
					poolIDsToLookUp.deleteCharAt(length - 1);
				}

				cstmt.setString(1, poolIDsToLookUp.toString());

				cstmt.registerOutParameter(2, Types.VARCHAR); // selected ippoolid by procedure
				cstmt.registerOutParameter(3, Types.NUMERIC); // selected serialnumber of the ip address from the selected pool
				cstmt.registerOutParameter(4, Types.VARCHAR); // selected ipaddress from the selected pool

				String nasipaddress=null;
				String nasid=null;
				String username=null;
				String callingstationid=null;

				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					callingstationid = callingStationID.getStringValue();
					cstmt.setString(5, callingstationid);
				} else {
					cstmt.setString(5, null);
				}

				IRadiusAttribute cui = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
				if(cui != null) {
					username = cui.getStringValue();
					cstmt.setString(6, username);
				} else {
					cstmt.setString(6, null);
				}

				IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(nasIpAddress != null) {
					nasipaddress = nasIpAddress.getStringValue();
					cstmt.setString(7, nasipaddress);
				} else {
					cstmt.setString(7, null);
				}

				IRadiusAttribute nasId = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
				if(nasId!=null) {
					nasid = nasId.getStringValue();
					cstmt.setString(8, nasid);
				} else {
					cstmt.setString(8, null);
				}

				long queryExecutionTime = 0;
				queryExecutionTime = System.currentTimeMillis();

				cstmt.executeUpdate();

				queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

				if(queryExecutionTime > 100){
					LogManager.getLogger().warn(MODULE, "high execution time for procedure: "+queryExecutionTime+ "ms.");
				}

				if(DEFAULT_QUERY_TIMEOUT < queryExecutionTime){
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, "OPEN-DB Query execution time getting high, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
				}

				String poolId = cstmt.getString(2);
				Number serialnumber = cstmt.getBigDecimal(3);
				String ipaddress = cstmt.getString(4);

				if(serialnumber != null && ipaddress != null){
					String strPoolName = this.ippoolIDToName.get(poolId);
					poolDetail = new PoolDetail(poolId, serialnumber.toString(), ipaddress, strPoolName, nasipaddress, nasid, username, callingstationid);
					isSuccess = true;
					break;
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "No free ip available in IP-Pool(s): " + strPoolNames);
					}
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RM_IPPOOL_GENERIC, 
							MODULE , "No free IP available from IP-Pool: " + strPoolNames, 0,
							"No free IP available from IP-Pool: " + strPoolNames);
				}
			} 

			if (!isSuccess) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN )){
					LogManager.getLogger().warn(MODULE, "No free IP could be allocated for Pool(s): " + poolNameList);
				}
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				response.setFurtherProcessingRequired(false);
			}
		}catch (DataSourceException e) {
			response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)){
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "IP Pool Database: " + ippoolServiceConfiguration.getDataSourceName() + ", Reason: " + e.getMessage(), 0,
					"IP Pool Database: " + ippoolServiceConfiguration.getDataSourceName() + ", Reason: " + e.getMessage());
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Problem in Allocating IP Address, Reason: " + e.getMessage());
			}
			throw new DriverProcessFailedException(e.getMessage(), e.getCause());
		}catch (SQLException e) {
			int sqlErrorCode = e.getErrorCode();
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)) {
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASEDOWN, MODULE, 
						"IP Pool Database: " + ippoolServiceConfiguration.getDataSourceName() + " is Down, System marked as dead",
						0, ippoolServiceConfiguration.getDataSourceName());
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,EXECPTION_MESSAGE_FOR_DATABASE_DOWN_SYSTEM_MARKED_AS_DEAD + e.getMessage());
				}
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}else if(sqlErrorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUNIQUECONSTRAINTS, 
						MODULE, "Unique Constraint Violated in IP Pool Database: " + ippoolServiceConfiguration.getDataSourceName(),
						0, ippoolServiceConfiguration.getDataSourceName() + "(" + MODULE + ")");
				
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}
			throw new DriverProcessFailedException(e.getMessage(), e);
		}finally {
			try {
				if(cstmt != null){
						try{
							cstmt.close();
						}catch (SQLException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE,e.toString());
							}
						}
							}

				if(cn != null){
					try {
						cn.commit();
						cn.close();
					} catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}	

			}catch (Exception ex) {  
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE,ex.toString());
				}
			}
		}
		return poolDetail;

	}


	public boolean updateOrReleaseIP( String poolid ,int serialnumber,String iRequestType,RadServiceRequest request,RadServiceResponse response) throws DriverProcessFailedException{

		Connection cn = null;
		PreparedStatement pstmt = null;
		boolean isSuccess = false;

		PreparedStatement commitNoWaitStmt = null;
		
		try {
			cn = this.getDSConnection();
			
			commitNoWaitStmt = cn.prepareStatement(getDBVendorSpecificCommitNoWaitStatement());
			
			if (dbQueryTimeOut > 0) {
				commitNoWaitStmt.setQueryTimeout(dbQueryTimeOut);
			}
			
			if(IP_ADDRESS_RELEASE_MESSAGE.equals(iRequestType)){

				pstmt=cn.prepareStatement(getIPReleaseQueryBasedOnIPPoolIdAndSerialNumber());
				if (pstmt == null) {
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
							MODULE, "PreparedStatement is not available while releasing ipaddress", 0,
							"PreparedStatement is not available while releasing ipaddress");
					throw new SQLException("PreparedStatement is not available while releasing ipaddress");
					}
		
				pstmt.setString(1,poolid);
				pstmt.setInt(2,serialnumber);
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					pstmt.setString(3, callingStationID.getStringValue());
				} else {
					pstmt.setString(3, null);
				}
				
				
			}else if(IP_UPDATE_MESSAGE.equals(iRequestType)){

				pstmt=cn.prepareStatement(getIPUpdateQueryBasedOnIPPoolIdAndSerialNumber());
				if (pstmt == null) {
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
							MODULE, "PreparedStatement is not available while updating ipaddress", 0,
							"PreparedStatement is not available while updating ipaddress");
					throw new SQLException("PreparedStatement is not available while updating ipaddress");
				}
				IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(nasIpAddress != null) {
					pstmt.setString(1, nasIpAddress.getStringValue());
				} else {
					pstmt.setString(1, null);
				}
				
				IRadiusAttribute nasId = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
				if(nasId!=null) {
					pstmt.setString(2, nasId.getStringValue());
				} else {
					pstmt.setString(2, null);
				}

				IRadiusAttribute cui = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
				if(cui != null) {
					pstmt.setString(3, cui.getStringValue());
				} else {
					pstmt.setString(3, null);
				}
					
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					pstmt.setString(4, callingStationID.getStringValue());
				} else {
					pstmt.setString(4, null);
				}

				pstmt.setString(5,poolid);
				pstmt.setInt(6,serialnumber);
				
			}  else if (RMIPPoolService.ACCT_UPDATE_MESSAGE.equals(iRequestType)) {
				String nasipaddress=null;
				String nasid=null;
				String username=null;
				String callingstationid=null;
				IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(nasIpAddress != null) {
					nasipaddress = nasIpAddress.getStringValue();
				}				
				IRadiusAttribute nasId = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
				if(nasId!=null) {
					nasid = nasId.getStringValue();
				}

				IRadiusAttribute cui = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
				if(cui != null) {
					username = cui.getStringValue();
				}
					
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					callingstationid = callingStationID.getStringValue();
				}
				
				addTobatch(Integer.toString(serialnumber),poolid,nasipaddress,nasid,username,callingstationid);
				return true;
			}

			if(dbQueryTimeOut>0)
				pstmt.setQueryTimeout(dbQueryTimeOut);
			
			long queryExecutionTime = 0;
			
			queryExecutionTime = System.currentTimeMillis();
			int iNumRowUpdate = pstmt.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			
			if(queryExecutionTime > 100){
				LogManager.getLogger().warn(MODULE, "high execution time for update query: "+queryExecutionTime+ "ms, in "+iRequestType);
			}
			
			if(DEFAULT_QUERY_TIMEOUT < queryExecutionTime)
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
						"OPEN-DB Query execution time getting high, - Last Query execution time = " 
								+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");

			if(iNumRowUpdate>0){
				isSuccess = true;	
			}
		}catch (DataSourceException e) {
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)){
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Connection to Database is not available, Reason: " + e.getMessage(), 0,
					"Connection to Database is not available, Reason: " + e.getMessage());
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Problem in updating/releasing ip address, Reason: " + e.getMessage());
			}
			throw new DriverProcessFailedException(e.getMessage(), e);
		}catch (SQLException e) {
			int sqlErrorCode = e.getErrorCode();
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)) {
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASEDOWN, MODULE, 
						"Problem in Updating IP Address. Reason: Database: " 
								+ ippoolServiceConfiguration.getDataSourceName() + " is Down",
								0, ippoolServiceConfiguration.getDataSourceName() + "(Problem in updating IP)");
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,EXECPTION_MESSAGE_FOR_DATABASE_DOWN_SYSTEM_MARKED_AS_DEAD + e.getMessage());
				}
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}else if(sqlErrorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUNIQUECONSTRAINTS, 
						MODULE, "Problem in Updating IP Address. Reason: Unique Constraint Violated in Database: " 
								+ ippoolServiceConfiguration.getDataSourceName(), 0, 
								ippoolServiceConfiguration.getDataSourceName() + "(" + MODULE + "-Problem in updating IP)");
				
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}
			throw new DriverProcessFailedException(e.getMessage(), e);
		}finally {
			try {
				if(cn!=null){
					if (isSuccess){
						try{
							commitNoWaitStmt.execute();
						}catch (SQLException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE,e.toString());
							}
						}
					}else {
						try{
							cn.rollback();
						}catch (SQLException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE,e.toString());
							}
						}

					}

				}
				
				if(pstmt != null){
					try {
						pstmt.close();
					}catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}	
				
				if (commitNoWaitStmt != null) {
					try {
						commitNoWaitStmt.close();
					}catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}
				
				if(cn != null){
					try {
						cn.close();
					} catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}	


			}catch (Exception ex) {  
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE,ex.toString());
				}
			}
		}		

		return isSuccess;
	}

	private String getDBVendorSpecificCommitNoWaitStatement() {
		String commitNoWaitStmt = ORACLE_COMMIT_STATEMENT;
		
		if (DBVendors.POSTGRESQL.equals(dbVendor)) {
			commitNoWaitStmt = POSTGRESQL_COMMIT_STAMEMENT;
		}
		
		return commitNoWaitStmt;
	}
	
	private String getIPReleaseQueryBasedOnIPPoolIdAndSerialNumber() {
		
		String ipRealeaseQuery = "UPDATE tblmippooldetail a SET a.assigned = 'N', a.reserved = 'N' , a.last_updated_time=systimestamp "
			+ " WHERE a.ippoolid = ? AND a.serialnumber = ? and CALLING_STATION_ID = ?";
		
		if (DBVendors.POSTGRESQL.equals(dbVendor)) {
			ipRealeaseQuery = "UPDATE tblmippooldetail SET assigned = 'N', reserved = 'N' , last_updated_time=current_timestamp "
				+ " WHERE ippoolid = ? AND serialnumber = ? and CALLING_STATION_ID = ?";
		}
		
		return ipRealeaseQuery;
	}
	
	private String getIPUpdateQueryBasedOnIPPoolIdAndSerialNumber() {
		
		String IPupdateQuery = "UPDATE tblmippooldetail a SET a.assigned = 'Y',a.reserved = 'Y', a.last_updated_time=systimestamp, a.NAS_IP_ADDRESS = ?, a.NAS_ID=?, a.USER_IDENTITY=?,a.CALLING_STATION_ID=?"
				+ " WHERE a.ippoolid = ? AND a.serialnumber = ? ";
		
		if (DBVendors.POSTGRESQL.equals(dbVendor)) {
			IPupdateQuery = "UPDATE tblmippooldetail SET assigned = 'Y',reserved = 'Y', last_updated_time=current_timestamp, NAS_IP_ADDRESS = ?, NAS_ID=?, USER_IDENTITY=?,CALLING_STATION_ID=?"
				+ " WHERE ippoolid = ? AND serialnumber = ? ";
		}
		
		return IPupdateQuery;
	}
	
	public boolean updateOrReleaseIP(  String  ipaddress,String iRequestType,RadServiceRequest request,RadServiceResponse response) throws DriverProcessFailedException{

		boolean isSuccess = false;
		Connection cn = null;
		PreparedStatement pstmt = null;

		PreparedStatement commitNoWaitStmt = null;
		
		try {
			cn = this.getDSConnection();
			
			commitNoWaitStmt = cn.prepareStatement(getDBVendorSpecificCommitNoWaitStatement());
			
			if (dbQueryTimeOut > 0) {
				commitNoWaitStmt.setQueryTimeout(dbQueryTimeOut);
			}
			
			if(IP_ADDRESS_RELEASE_MESSAGE.equals(iRequestType)){

				pstmt=cn.prepareStatement(getIPReleaseQueryBasedOnIPAddress());				
				
				if (pstmt == null){
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
							MODULE, "PreparedStatement is not available while releasing ipaddress", 0,
							"PreparedStatement is not available while releasing ipaddress");
					throw new SQLException("PreparedStatement is not available while releasing ipaddress");
					}
				
				pstmt.setString(1, ipaddress);
					
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					pstmt.setString(2, callingStationID.getStringValue());
				} else {
					pstmt.setString(2, null);
				}
				
			}else if(IP_UPDATE_MESSAGE.equals(iRequestType)){

				pstmt=cn.prepareStatement(getIPUpdateQueryBasedOnIPAddress());
				if (pstmt == null){
					serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
							MODULE, "PreparedStatement is not available while updating ipaddress.", 0,
							"PreparedStatement is not available while updating ipaddress.");
					throw new SQLException("PreparedStatement is not available while updating ipaddress");

				}

				IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(nasIpAddress != null) {
					pstmt.setString(1, nasIpAddress.getStringValue());
				} else {
					pstmt.setString(1, null);
				}
				
				IRadiusAttribute nasId = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
				if(nasId!=null) {
					pstmt.setString(2, nasId.getStringValue());
				} else {
					pstmt.setString(2, null);
				}

				IRadiusAttribute cui = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
				if(cui != null) {
					pstmt.setString(3, cui.getStringValue());
				} else {
					pstmt.setString(3, null);
				}
					
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					pstmt.setString(4, callingStationID.getStringValue());
				} else {
					pstmt.setString(4, null);
				}

				pstmt.setString(5, ipaddress);
				
			} else if (RMIPPoolService.ACCT_UPDATE_MESSAGE.equals(iRequestType)) {
				String nasipaddress=null;
				String nasid=null;
				String username=null;
				String callingstationid=null;
				IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(nasIpAddress != null) {
					nasipaddress = nasIpAddress.getStringValue();
				}				
				IRadiusAttribute nasId = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
				if(nasId!=null) {
					nasid = nasId.getStringValue();
				}

				IRadiusAttribute cui = request.getRadiusAttribute(RadiusAttributeConstants.CUI);
				if(cui != null) {
					username = cui.getStringValue();
				}
				
				IRadiusAttribute callingStationID = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationID!=null) {
					callingstationid = callingStationID.getStringValue();
				}
				addTobatch(ipaddress, nasipaddress,nasid,username,callingstationid);
				
				return true;
			}

			if (dbQueryTimeOut > 0) {
				pstmt.setQueryTimeout(dbQueryTimeOut);
			}
			
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();

			int iNumRowUpdate = pstmt.executeUpdate();

			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			
			if(queryExecutionTime > 100){
				LogManager.getLogger().warn(MODULE, "high execution time for update query: "+queryExecutionTime+ "ms, in "+iRequestType);
			}
			
			if(DEFAULT_QUERY_TIMEOUT < queryExecutionTime){
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
								"OPEN-DB Query execution time getting high, - Last Query execution time = " + queryExecutionTime + " milliseconds.");
			}
			
			if (iNumRowUpdate > 0) {
				isSuccess = true;
			}
		}catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn (MODULE,"Problem in updating/releasing ip address, Reason: " + e.getMessage());
			}
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)){
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
			}
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASE_GENERIC, 
					MODULE, "Problem in Updating IP Address. Reason: Database " + ippoolServiceConfiguration.getDataSourceName() 
						+ " is unavailable, Reason: " + e.getMessage(), 0,
					"Problem in Updating IP Address. Reason: Database " + ippoolServiceConfiguration.getDataSourceName() 
						+ " is unavailable, Reason: " + e.getMessage());
			
			throw new DriverProcessFailedException(e.getMessage(), e);
		}catch (SQLException e) {
			int sqlErrorCode = e.getErrorCode();
			if(DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).isDBDownSQLException(e)) {
				DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().markDead();
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DATABASEDOWN, MODULE, 
						"Problem in Updating IP Address. Reason: Database: " 
								+ ippoolServiceConfiguration.getDataSourceName() + " is Down",
								0, ippoolServiceConfiguration.getDataSourceName() + "(Problem in updating IP)");
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,EXECPTION_MESSAGE_FOR_DATABASE_DOWN_SYSTEM_MARKED_AS_DEAD + e.getMessage());
				}
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}else if(sqlErrorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUNIQUECONSTRAINTS, 
						MODULE, "Problem in Updating IP Address. Reason: Unique Constraint Violated in Database: " 
								+ ippoolServiceConfiguration.getDataSourceName(), 0, 
								ippoolServiceConfiguration.getDataSourceName() + "(" + MODULE + "-Problem in updating IP)");
				
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			}
			throw new DriverProcessFailedException(e.getMessage(), e);
		}finally {
			try {
				if(cn!=null){
					if (isSuccess){
						try{
							commitNoWaitStmt.execute();
						}catch (SQLException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE,e.toString());
							}
						}
					}else {
						try{
							cn.rollback();
						}catch (SQLException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE,e.toString());
							}
						}

					}

				}
				if(pstmt != null){
					try {
						pstmt.close();
					}catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}	
				
				if (commitNoWaitStmt != null) {
					try {
						commitNoWaitStmt.close();
					}catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}
				
				if(cn != null){
					try {
						cn.close();
					} catch (SQLException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,e.toString());
						}
					}
				}	
			}catch (Exception ex) {  
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE,ex.toString());
				}
			}
		}

		return isSuccess;
	}

	
	private String getIPReleaseQueryBasedOnIPAddress(){
		
		String ipRealeaseQuery =  "UPDATE tblmippooldetail a SET a.assigned = 'N', a.reserved ='N', a.last_updated_time=systimestamp "
				+ " WHERE a.ipaddress = ? AND CALLING_STATION_ID = ?";
		
		if(DBVendors.POSTGRESQL.equals(dbVendor)) {
			ipRealeaseQuery = "UPDATE tblmippooldetail SET assigned = 'N', reserved ='N', last_updated_time=current_timestamp "
					+ " WHERE ipaddress = ? AND CALLING_STATION_ID = ?";
		}
		
		return ipRealeaseQuery;
	}
	
	private String getIPUpdateQueryBasedOnIPAddress(){
		
		String ipUpdateQuery = "UPDATE tblmippooldetail a SET a.assigned = 'Y',a.reserved = 'Y', a.last_updated_time=systimestamp, a.NAS_IP_ADDRESS = ?, a.NAS_ID=?, a.USER_IDENTITY=?,a.CALLING_STATION_ID=?"
				+ " WHERE a.ipaddress = ? ";
		
		if(DBVendors.POSTGRESQL.equals(dbVendor)) {
			ipUpdateQuery = "UPDATE tblmippooldetail SET assigned = 'Y',reserved = 'Y', last_updated_time=current_timestamp, NAS_IP_ADDRESS = ?, NAS_ID=?, USER_IDENTITY=?,CALLING_STATION_ID=?"
					+ " WHERE ipaddress = ? ";
		}
		
		return ipUpdateQuery;
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public void scan() {
		DBConnectionManager.getInstance(ippoolServiceConfiguration.getDataSourceName()).getTransactionFactory().scan();
	}

	public int stringToInteger(String originalString,int defaultValue) {
		int resultValue = defaultValue;
		try{
			resultValue = Integer.parseInt(originalString);
		}catch (Exception e) {
			
		}
		return resultValue;

	}

	@Override
	public int getType() {
		return DriverTypes.RM_OPENDB_IPPOOL_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_OPENDB_IPPOOL_DRIVER.name();
	}


	public class  TBLMIPPOOL_class{

		private String IPPOOLID;
		private String NAME;	
		private String  NASIPADDRESS;
		private AdditionalResponseAttributes additionalResponseAttributes;

		public TBLMIPPOOL_class(String ippoolid, String name, 	String nasipaddress) {
			IPPOOLID = ippoolid;
			NAME = name;
			NASIPADDRESS = nasipaddress;
		}

		public void setIPPOOLID(String iPPOOLID) {
			IPPOOLID = iPPOOLID;
		}

		public String getIPPOOLID() {
			return IPPOOLID;
		}

		public void setNAME(String nAME) {
			NAME = nAME;
		}

		public String getNAME() {
			return NAME;
		}

		public String getNASIPADDRESS() {
			return NASIPADDRESS;
		}

		public void setNASIPADDRESS(String nasipaddress) {
			NASIPADDRESS = nasipaddress;
		}

		public String toString(){
			return "  "+IPPOOLID+"  "+NAME+"  "+NASIPADDRESS+"  ";
		}

		public AdditionalResponseAttributes getAdditionalResponseAttributes() {
			return additionalResponseAttributes;
		}

	}

	public class PoolDetail{
		private String poolId;
		private String poolSerialNumber;
		private String ipAddress;
		private String poolName;
		private String nasipaddress;
		private String nasid;
		private String username;
		private String callingStationid;


		public PoolDetail(String poolid,String poolserialnumber,String ipaddress, String poolName,String nasipaddress,String nasid,String username,String callingStationid  ){
			poolId=poolid;
			poolSerialNumber=poolserialnumber;
			ipAddress=ipaddress;
			this.poolName = poolName;
			this.nasipaddress = nasipaddress;
			this.nasid = nasid;
			this.username  = username;
			this.callingStationid = callingStationid;

		}
		public String getPoolId(){
			return poolId;
		}
		public String getPoolSerialNumber(){
			return poolSerialNumber;
		}
		public String getIPAddress(){
			return ipAddress;
		}
		
		public String getPoolName () {
			return this.poolName;
		}
		public String getNasipaddress() {
			return nasipaddress;
		}
		public String getNasid() {
			return nasid;
		}
		public String getUsername() {
			return username;
		}
		public String getCallingStationid() {
			return callingStationid;
		}
	}
	
	public void addTobatch(String ipAddress,String nasipaddress,String nasid,String username,String callingStationid) {
		PoolDetail poolDetail = new PoolDetail(null,null, ipAddress, null,nasipaddress,nasid,username,callingStationid);
		queueForIpAddress.add(poolDetail);
	}	

	public void addTobatch(String serialNumber, String poolId,String nasipaddress,String nasid,String username,String callingStationid) {
		PoolDetail poolDetail = new PoolDetail(poolId, serialNumber, null, null,nasipaddress,nasid,username,callingStationid);
		queueForSerialID.add(poolDetail);
	}
	
	private void executeBatchUpdate() {
		if (queueForIpAddress.size() <=0 && queueForSerialID.size() <=0) {
			return;
		}
		
		Connection connection = null;
		PreparedStatement psForIpAddr = null;
		PreparedStatement psForSerialId = null;
		int batchSize = 500;
		int batchCountForFramedIP = 0;
		int batchCountForSrNo = 0;
		try {
			connection = getDSConnection();
			psForIpAddr = connection.prepareStatement(getIPPoolUpdateQueryForAcctIntrim());

			psForSerialId = connection.prepareStatement(getIPPoolUpdateQueryForAcctIntrimBasedOnIPPoolIdAndSerialNo());
			
			while (true) {

					PoolDetail poolDetail = queueForSerialID.poll();
					if (poolDetail != null) {
						psForSerialId.setString(1, poolDetail.getNasipaddress());
						psForSerialId.setString(2, poolDetail.getNasid());
						psForSerialId.setString(3,poolDetail.getUsername());
						psForSerialId.setString(4,poolDetail.getCallingStationid());
						psForSerialId.setString(5, poolDetail.getPoolId());
						psForSerialId.setString(6, poolDetail.getPoolSerialNumber());
						
						
						psForSerialId.addBatch();
						if (++batchCountForSrNo >= batchSize) {
							break;
						}

					} else {
						break;
					}
			}

			while (true) {

					PoolDetail poolDetail  = queueForIpAddress.poll();
					if (poolDetail != null) {
						psForIpAddr.setString(1, poolDetail.getNasipaddress());
						psForIpAddr.setString(2, poolDetail.getNasid());
						psForIpAddr.setString(3,poolDetail.getUsername());
						psForIpAddr.setString(4,poolDetail.getCallingStationid());
						psForIpAddr.setString(5, poolDetail.getIPAddress());
						psForIpAddr.addBatch();
						if (++batchCountForFramedIP >= batchSize) {
							break;
						}

					} else {
						break;
					}
			} 
			if (batchCountForFramedIP > 0) {
				psForIpAddr.executeBatch();
			}

			if (batchCountForSrNo > 0) {
				psForSerialId.executeBatch();
			}
			connection.commit();
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Acct-intrim batch update complete. No of records updated: " + batchCountForFramedIP + batchCountForSrNo);
		}catch (DataSourceException e) {
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
					MODULE, "Could not execute Acct-intrim batch update, Reason: " + e.getMessage(), 0,
					"Could not execute Acct-intrim batch update, Reason: " + e.getMessage());
			queueForIpAddress.clear();
			queueForSerialID.clear();
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Connection to datasource: " + ippoolServiceConfiguration.getDataSourceName() 
						+ " is unavailable, Reason: " + e.getMessage() + ". Flushing Acct-intrim batch update queues.");
			}
			LogManager.getLogger().trace(MODULE,e);
		}catch (SQLException e) {
			serviceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
					MODULE, "Could not execute Acct-intrim batch update. Reason: SQLException occured", 0,
					"Could not execute Acct-intrim batch update. Reason: SQLException occured");
			queueForIpAddress.clear();
			queueForSerialID.clear();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Could not execute Acct-intrim batch update. Reason: " + e.getMessage() + ". Flushing Acct-intrim batch update queues");
			}
			LogManager.getLogger().trace(MODULE,e);
			
		} finally {
			DBUtility.closeQuietly(psForSerialId);
			DBUtility.closeQuietly(psForIpAddr);
			DBUtility.closeQuietly(connection);
		}
	}
	

	private String getIPPoolUpdateQueryForAcctIntrim() {
		
		String updateQuery = "UPDATE tblmippooldetail a SET a.assigned = 'Y',a.reserved = 'Y', a.last_updated_time= systimestamp, a.NAS_IP_ADDRESS = ?, a.NAS_ID=?, a.USER_IDENTITY=?,a.CALLING_STATION_ID=? WHERE a.ipaddress = ? ";
		
		if(DBVendors.POSTGRESQL.equals(dbVendor)) {
			updateQuery = "UPDATE tblmippooldetail SET assigned = 'Y',reserved = 'Y', last_updated_time= current_timestamp, NAS_IP_ADDRESS = ?, NAS_ID=?, USER_IDENTITY=?,CALLING_STATION_ID=? WHERE ipaddress = ? ";
		}
		
		return updateQuery;
	}
	
	private String getIPPoolUpdateQueryForAcctIntrimBasedOnIPPoolIdAndSerialNo() {
		
		String updateQuery = "UPDATE tblmippooldetail a SET a.assigned = 'Y',a.reserved = 'Y', a.last_updated_time= systimestamp, a.NAS_IP_ADDRESS = ?, a.NAS_ID=?, a.USER_IDENTITY=?,a.CALLING_STATION_ID=? WHERE a.ippoolid = ? AND a.serialnumber = ? ";
		
		if(DBVendors.POSTGRESQL.equals(dbVendor)) {
			updateQuery = "UPDATE tblmippooldetail SET assigned = 'Y',reserved = 'Y', last_updated_time= current_timestamp, NAS_IP_ADDRESS = ?, NAS_ID=?, USER_IDENTITY=?, CALLING_STATION_ID=? WHERE ippoolid = ? AND serialnumber = ? ";
		}
		
		return updateQuery;
	}
	private class AcctIntrimBatchExecutor extends BaseIntervalBasedTask {

		int interval;
		public AcctIntrimBatchExecutor(int timeInterval) {
			this.interval = timeInterval;
		}
		
		@Override
		public long getInterval() {
			return interval;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			try {
				executeBatchUpdate();
			} catch (Throwable t) {
				LogManager.getLogger().error(MODULE, "Unknown Error Occured while Acct-Interim batch execution. Reason: " + t.getMessage());
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_IPPOOL_GENERIC, 
						MODULE, "Unknown Error Occured while Acct-Interim batch execution", 0,
						"Unknown Error Occured while Acct-Interim batch execution");
			}
		}
		
		@Override
		public long getInitialDelay() {
			return interval;
		}
	}

	public TBLMIPPOOL_class getIPPoolDetail(String poolName) {
		return this.hashMapIPPool.get(poolName);
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		int size = this.ipPoolList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from TBLMIPPOOL where IPPOOLID IN (");

		for(int i = 0; i < size-1; i++){
			TBLMIPPOOL_class ipPoolClass = this.ipPoolList.get(i);
			queryBuilder.append("'" + ipPoolClass.getIPPOOLID() + "',");
		}
		queryBuilder.append("'" + ipPoolList.get(size - 1).getIPPOOLID() + "')");
		String queryForReload = queryBuilder.toString();
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = conn.prepareStatement(queryForReload);
			rs = preparedStatement.executeQuery();

			while(rs.next()){
				TBLMIPPOOL_class tempIPPoolClass =  this.hashMapIPPool.get(rs.getString("NAME"));
				if(tempIPPoolClass!=null){
					String additionalAttr = rs.getString("ADDITIONALATTRIBUTES");
					tempIPPoolClass.additionalResponseAttributes = new AdditionalResponseAttributes(additionalAttr);

				}
			}
		}catch (DataSourceException e) {
			throw new InitializationFailedException("Connection to datasource: " + ippoolServiceConfiguration.getDataSourceName() + " is unavailable, Reason: " + e.getMessage(), e);
		}catch (SQLException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			throw new InitializationFailedException(e.getMessage(), e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}

	}
	
	@Override
	public void generateUpAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.INFO,
				Alerts.DATABASEUP,
				MODULE, 
				"Connection is UP for Driver: " + getName() + " (" +ippoolServiceConfiguration.getDataSourceName() + ")",
				0, getName() + "(" + ippoolServiceConfiguration.getDataSourceName() + ")");
		
		LogManager.getLogger().debug(MODULE, "RM ippool Driver: " + getName()+ " is alive now.");
	}
	
	@Override
	public void generateDownAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEDOWN, MODULE, 
				"Connection is Down for Driver: " + getName() + " (" + ippoolServiceConfiguration.getDataSourceName() + ")",
				0, getName() + "(" +ippoolServiceConfiguration.getDataSourceName() + ")");
		
		LogManager.getLogger().warn(MODULE, "RM ippool Driver: " + getName()+ " is dead marked.");
	}
} 