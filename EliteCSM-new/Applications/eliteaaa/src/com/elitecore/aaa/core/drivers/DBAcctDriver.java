package com.elitecore.aaa.core.drivers;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;

public abstract class DBAcctDriver extends BaseAcctDriver {
	private static String MODULE = "DB-ACCT-DRV";
	
	public final static int INSERT_QUERY_TYPE=1; 
	public final static int UPDATE_QUERY_TYPE=2;
	public final static int DELETE_QUERY_TYPE=3;
	public static final int ACCT_STATUS_TYPE_NOT_FOUND =0;
	
	private static final int QUERY_TIMEOUT_ERRORCODE = 1013;
	private static final int DATABASE_UNIQUE_CONSTRAINTS = 1;
	private int  iTotalQueryTimeoutCount = 0;
	private static int DEFAULT_QUERY_TIMEOUT = 10;
	
	public DBAcctDriver(ServerContext serverContext) {
		super(serverContext);
	}
	
	@Override
	protected void initInternal() throws TransientFailureException, 
	DriverInitializationFailedException {
		initDataSource();
	}
	
	/*
	 * Treats datasource initialization failure as temporary while type not supported is
	 * treated as permanent
	 */
	private void initDataSource() throws TransientFailureException, 
	DriverInitializationFailedException {
		
		DBDataSource dataSource = ((AAAServerContext)getServerContext())
		.getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(getDsName());
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(dataSource.getDataSourceName());
		try {
			dbConnectionManager.init(dataSource, getServerContext().getTaskScheduler());
		} catch (DatabaseInitializationException e) {
			markDead();
			throw new TransientFailureException("Datasource initialization Failed", e);
		} catch (DatabaseTypeNotSupportedException e) {
			markDead();
			throw new DriverInitializationFailedException("Datasource Type is not Supported", e);
		}finally{
			TransactionFactory transactionFactory = dbConnectionManager.getTransactionFactory();
			transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {

				@Override
				public void alive(ESCommunicator esCommunicator) {
					DBAcctDriver.this.markAlive();
				}

				@Override
				public void dead(ESCommunicator esCommunicator) {
					DBAcctDriver.this.markDead();
					
				}
			});
		}
	}
	
	public void setQueryTimeout(PreparedStatement preparedStatement, int timeout) throws SQLException{
		if(timeout > 0 && timeout < DEFAULT_QUERY_TIMEOUT)
			timeout=DEFAULT_QUERY_TIMEOUT;
		if(timeout > 0)
			preparedStatement.setQueryTimeout(timeout);		
	}
	
	public int executeQuery(int queryType,String query, List<DBTypeAndValueTuple> acctFiledList,ServiceRequest request) throws DriverProcessFailedException{		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int insertIndex = 1;
		int iResult = -1;
		DBConnectionManager connectionManager = DBConnectionManager.getInstance(getDsName());
		try{ 
			connection = connectionManager.getConnection();
			preparedStatement = connection.prepareStatement(query);
			if(preparedStatement == null) {
				throw new DriverProcessFailedException("PreparedStatement is not available");
			}
			if(queryType != DELETE_QUERY_TYPE) {
				long lEventTimeStamp = getEventTimeValue(request);
				
				if(getCallStartFieldName() != null && getCallStartFieldName().trim().length() > 0) {
					preparedStatement.setTimestamp(insertIndex, new Timestamp(lEventTimeStamp - getAcctSessionTimeValue(request)));
					insertIndex++;
				}

				if(getCallEndFieldName() != null && getCallEndFieldName().trim().length() > 0) {
					preparedStatement.setTimestamp(insertIndex,new Timestamp(lEventTimeStamp));
					insertIndex++;
				}

				if(getCreateDateFieldName() != null && getCreateDateFieldName().trim().length() > 0) {
					preparedStatement.setTimestamp(insertIndex, new Timestamp(new Date().getTime()));
					insertIndex++;
				}

				if(getLastModifiedDateFieldName() != null && getLastModifiedDateFieldName().trim().length() > 0) {
					preparedStatement.setTimestamp(insertIndex, new Timestamp(new Date().getTime()));
					insertIndex++;
				}

				if(getEnebled() && getDbDateField()!= null && getDbDateField().trim().length() > 0) {
					preparedStatement.setTimestamp(insertIndex, new Timestamp(new Date().getTime()));
					insertIndex++;
				}

				if(acctFiledList != null){
					DBTypeAndValueTuple acctField = null;
					if(acctFiledList.size() <= 0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "OpenDB AcctFieldMap Configuration Not Found for driver: " + getName());
					}
					for(int index = 0; index < acctFiledList.size(); index++){
						acctField = acctFiledList.get(index);
						String acctFieldvalue = acctField.getValue();
						if("Date".equalsIgnoreCase(acctField.getType())){
							preparedStatement.setTimestamp(insertIndex, new Timestamp(Long.parseLong(acctFieldvalue) * 1000 ));
							insertIndex++;
						}else{
							if(Strings.isNullOrBlank(acctFieldvalue) == false){
								preparedStatement.setString(insertIndex, acctFieldvalue);
							}else{
								preparedStatement.setObject(insertIndex, null);
							}
							insertIndex++;
						}
					}
					if(queryType == UPDATE_QUERY_TYPE) {
						preparedStatement.setString(insertIndex, getAcctSessionId(request));
					}
				}
			} else {
				preparedStatement.setString(1,getAcctSessionId(request));
			}
			setQueryTimeout(preparedStatement,getDbQueryTimeout());
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			iResult = preparedStatement.executeUpdate();
			queryExecutionTime = (System.currentTimeMillis() - queryExecutionTime);
			if(10 < queryExecutionTime) {
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
						"OPEN-DB Query execution time getting high, - Last Query execution time = " + queryExecutionTime 
						+ " milliseconds.", (int)queryExecutionTime, "");
			}
			if(iResult > 0) 
				connection.commit();
			iTotalQueryTimeoutCount = 0;
			return iResult;
		} catch (DataSourceException e) {
			if(connectionManager.isDBDownSQLException(e)) {
				connectionManager.getTransactionFactory().markDead();
			}
			throw new DriverProcessFailedException("Connection to datasource: " + getDsName() + " is unavailable, Reason: " + e.getMessage());
		}catch(SQLException e){
			int errorCode = e.getErrorCode();
			if(errorCode == QUERY_TIMEOUT_ERRORCODE) {				
				iTotalQueryTimeoutCount++;
				if(iTotalQueryTimeoutCount > getMaxQueryTimeoutCount()) {
					LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System marked as DEAD");
					connectionManager.getTransactionFactory().markDead();
				}
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, "DB query TimeOut.",0,"");
				throw new DriverProcessFailedException(e);
			}else if(errorCode == DATABASE_UNIQUE_CONSTRAINTS) {
				getServerContext().generateSystemAlert(AlertSeverity.INFO,Alerts.DATABASEUNIQUECONSTRAINTS, 
						MODULE, "Unique Constraint Violated in db.", 0, 
						getDsName() + "(" + MODULE + ")");
				
			}else if(connectionManager.isDBDownSQLException(e)){
				connectionManager.getTransactionFactory().markDead();
				
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
						"Connection to datasource: " + getDsName() + 
						" is unavailable, system marked as dead. Reason: " + e.getMessage(),
						0, getDsName());
				
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + getDsName() + " is unavailable, system marked as dead. Reason: " + e.getMessage());
			}else {
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
						"Unknown DB error, Reason: " + e.getMessage(),
						0, getDsName() + "(Unknown DB Error)");
			}
			throw new DriverProcessFailedException(e);
		}finally{
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}	
	}
	
	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}
	
	@Override
	public void scan() {
		DBConnectionManager.getInstance(getDsName()).getTransactionFactory().scan();
	}

	@Override
	public void generateUpAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CLEAR,
				Alerts.DATABASEUP,
				MODULE,
				"Connection is UP for Driver: " + getName() + " (" +getDsName() + ")", 0,
				getName() + "(" + getDsName() + ")");
		
		LogManager.getLogger().debug(MODULE, "DB Acct Driver: " + getName()+ " is alive now");
	}
	
	@Override
	public void generateDownAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN,MODULE,
				"Connection is Down for Driver: " + getName() + " (" +getDsName() + ")",
				0, getName() + "(" +getDsName() + ")");
		
		LogManager.getLogger().warn(MODULE, "DB Acct Driver: " + getName()+ " is dead marked");
	}
	
	public abstract int getMaxQueryTimeoutCount();
	public abstract String getMultivalDelimeter() ;
	public abstract String getDsName();
	public abstract String getCallStartFieldName();
	public abstract String getCallEndFieldName();
	public abstract String getCreateDateFieldName();
	public abstract boolean getEnebled();
	public abstract String getDbDateField();
	public abstract String getLastModifiedDateFieldName();
	public abstract int getDbQueryTimeout();
	protected abstract long getEventTimeValue(ServiceRequest request);
	protected abstract long getAcctSessionTimeValue(ServiceRequest request);
	protected abstract String getAcctSessionId(ServiceRequest request);
	
}
