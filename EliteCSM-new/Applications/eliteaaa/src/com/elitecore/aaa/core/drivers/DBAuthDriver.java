package com.elitecore.aaa.core.drivers;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;


public abstract class DBAuthDriver extends BaseAuthDriver {

	private static String MODULE = "DB-AUTH-DRV";
	private final DBDataSource dataSource;
	private int  iTotalQueryTimeoutCount = 0;
	private static final int QUERY_TIMEOUT_ERRORCODE = 1013;
	
	private static int DEFAULT_QUERY_TIMEOUT = 10;
	private String insertAccountDataQuery = null;
	private String updateAccountDataQuery = null;
	
	private @Nonnull DynamicCheckItemUpdater dynamicCheckItemUpdater;
	private DBConnectionManager connectionManager;
	
	public DBAuthDriver(AAAServerContext serverContext, DBDataSource dataSource, 
			DBConnectionManager dbConnectionManager) {
		super(serverContext);
		this.dataSource = dataSource;
		this.connectionManager = dbConnectionManager;
	}
	
	public void setQueryTimeout(PreparedStatement preparedStatement, int timeout) throws SQLException{
		if(timeout > 0 && timeout < DEFAULT_QUERY_TIMEOUT)
			timeout=DEFAULT_QUERY_TIMEOUT;
		if(timeout > 0)
			preparedStatement.setQueryTimeout(timeout);		
	}
	
	private String getQueryForAccountData(){		
		return "SELECT * FROM " + getTablename()+ " WHERE " +getProfileLookUpColumnName()+ "=?";
	}
	
	private String getInsertQueryForAccountData(){	
	
		if(insertAccountDataQuery==null){
			StringBuffer insertQueryBuffer = new StringBuffer();
			StringBuffer valueBuffer = new StringBuffer();
			insertQueryBuffer.append("INSERT INTO "+getTablename()+" ( ");
			
			AccountDataFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();
			
			if(getSequenceName()!=null && getSequenceName().trim().length()>0 
					&& getPrimaryKeyColumn()!=null && getPrimaryKeyColumn().trim().length()>0){
				
				valueBuffer.append(connectionManager.getVendor().getVendorSpecificSequenceSyntax(getSequenceName().trim())+",");
				insertQueryBuffer.append(getPrimaryKeyColumn().trim()+",");
				
			}
			
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME) != null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY) != null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}				
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}				
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION)!=null){
				insertQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION).get(0).getFieldName()+",");
				valueBuffer.append("?,");
			}
			
			String values = valueBuffer.toString().substring(0, valueBuffer.length()-1);
			String insertQueryFields = insertQueryBuffer.toString().substring(0, insertQueryBuffer.length()-1);
			insertAccountDataQuery = insertQueryFields + ") VALUES ("+values + ")"; ;
		}
		return insertAccountDataQuery;
		
	}
	
	private String getUpdateQueryForAccountData() {	
		
		if(updateAccountDataQuery==null){
			StringBuffer updateQueryBuffer = new StringBuffer();
			updateQueryBuffer.append("UPDATE " + getTablename()+ " set   ");
			AccountDataFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();
			
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME) != null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY) != null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE)!=null)					
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION).get(0).getFieldName()+"=? ,");				
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK).get(0).getFieldName()+"=? ,");				
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY)!=null)					
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY)!=null)					
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION).get(0).getFieldName()+"=? ,");
			
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN).get(0).getFieldName()+"=? ,");
			if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION)!=null)
				updateQueryBuffer.append(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION).get(0).getFieldName()+"=? ,");
			
			
			updateQueryBuffer.delete(updateQueryBuffer.length() - 2, updateQueryBuffer.length());
			updateQueryBuffer.append(" WHERE " +getProfileLookUpColumnName()+ "=?");
			updateAccountDataQuery= updateQueryBuffer.toString();

		}
		return updateAccountDataQuery;
		
	}
	
	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException {
		
		
		List<String> driverLevelUIAttr =  getUserIdentityAttributes();
		
		if (driverLevelUIAttr != null){
			for (String userIdAttr : driverLevelUIAttr){
				String userIdValue = getValueForIdentityAttribute(serviceRequest, userIdAttr); 
				if ( userIdValue != null){
					userIdentity = userIdValue;
					break;
				}
			}
		}

		if (btrimUserIdentity) {
			userIdentity = userIdentity.trim();
		}
		
		userIdentity = caseStrategy.apply(userIdentity);
		
		String strUnstrippedUserIdentityValue = userIdentity;
		userIdentity = stripStrategy.apply(userIdentity, realmSeparator);
		
		
		LogManager.getLogger().info(MODULE,"Trying to get account data for user identity: [" + userIdentity + "]");
		Connection  connection = null; //serverContext.getServerConfiguration().getConnection();
		AccountData accountData = null;		
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try{			
			connection = connectionManager.getConnection();
			query = getQueryForAccountData();
			preparedStatement = connection.prepareStatement(query);
			if(preparedStatement == null){
				throw new DriverProcessFailedException("PreparedStatement is not available");
			}
			preparedStatement.setString(1,userIdentity);
			if(getDbQueryTimeout()>0){
				preparedStatement.setQueryTimeout(getDbQueryTimeout());
			}
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			resultSet = preparedStatement.executeQuery();
			queryExecutionTime = (System.currentTimeMillis() - queryExecutionTime);
			if(DEFAULT_QUERY_TIMEOUT < queryExecutionTime)
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
						"OPEN-DB Query execution time getting high, - Last Query execution time = " 
								+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");
			
			if(resultSet.next()) {
				
				DBAccountDataValueProvider valueProvider = new DBAccountDataValueProvider(resultSet);
				accountData = buildAccountData(serviceRequest,valueProvider, getAccountDataFieldMapping());
				if (accountData != null){
					accountData.setUserIdentity(userIdentity);					
				}
				serviceRequest.setParameter(AAAServerConstants.CUI_KEY, userIdentity);
				serviceRequest.setParameter(AAAServerConstants.UNSTRIPPED_CUI, strUnstrippedUserIdentityValue);
				
				iTotalQueryTimeoutCount = 0;
			}
		} catch (DataSourceException e) {
			if(connectionManager.isDBDownSQLException(e)) {
				connectionManager.getTransactionFactory().markDead();
			}
			throw new DriverProcessFailedException("Connection to datasource: " + getDSName() + " is unavailable, Reason: " + e.getMessage());
		}catch (SQLRecoverableException e) {
			connectionManager.getTransactionFactory().markDead();
			getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
					"Database with Data Source name "+getDSName()
					+" is Down, System marked as dead. Reason: " + e.getMessage(),
					0, getDSName());
			
			LogManager.getLogger().warn(MODULE,"Database with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage());
			throw new DriverProcessFailedException(e);
		}catch(SQLException e){
			int errorCode = e.getErrorCode();
			if(errorCode == QUERY_TIMEOUT_ERRORCODE) {
				iTotalQueryTimeoutCount++;
				if(iTotalQueryTimeoutCount > getMaxQueryTimeoutCount()) {
					LogManager.getLogger().warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so System marked as DEAD");
					connectionManager.getTransactionFactory().markDead();
				}
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, "DB query TimeOut.", 0, "");
			}else if(connectionManager.isDBDownSQLException(e)){
				connectionManager.getTransactionFactory().markDead();
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
						"Database with Data Source name " + getDSName()
						+" is Down, System marked as dead. Reason: " + e.getMessage(),
						0, getDSName());
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Database with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage());
				}
			} else {
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
						"Unknown DB Error in Data Source " + getDSName() + " , Reason: " + e.getMessage(),
						0, getDSName() + "(Unknown DB Error)");
			}
			throw new DriverProcessFailedException(e);
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}		
		return accountData;
	}
	
	@Override
	public void setDynamicCheck(String strDynamicCheck, String strUserIdentity)
			throws UpdateAccountDataFailedException {
		this.dynamicCheckItemUpdater.update(strDynamicCheck, strUserIdentity);
	}
	
	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}
	
	@Override
	public void scan() {
		connectionManager.getTransactionFactory().scan();
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		initDataSource(true);
		createDynamicCheckItemUpdater();
	}
	
	/*
	 * If dynamic check items field mapping is not configured then the call does not reach driver.
	 * But as the code is not encapsulated, the condition may change. So have been defensive.
	 */
	private void createDynamicCheckItemUpdater() {
		AccountDataFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();
		if (accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS) == null) {
			this.dynamicCheckItemUpdater = new NullDynamicCheckItemUpdater();
		} else {
			this.dynamicCheckItemUpdater = new DBDynamicCheckItemUpdater();
		}
	}
	
	/*
	 * Treats datasource initialization failure as temporary while type not supported is
	 * treated as permanent
	 */
	private void initDataSource(boolean bCheckForAllIp) throws TransientFailureException,
	DriverInitializationFailedException {

		try {
			connectionManager.init(dataSource, getServerContext().getTaskScheduler());
		} catch (DatabaseInitializationException e) {
			markDead();
			throw new TransientFailureException("Datasource initialization Failed", e);
		} catch (DatabaseTypeNotSupportedException e) {
			markDead();
			throw new DriverInitializationFailedException("Datasource Type is not Supported",e);
		}finally{
			TransactionFactory transactionFactory = connectionManager.getTransactionFactory();
			transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {

				@Override
				public void alive(ESCommunicator esCommunicator) {
					DBAuthDriver.this.markAlive();
				}

				@Override
				public void dead(ESCommunicator esCommunicator) {
					DBAuthDriver.this.markDead();
					
				}
			});
		}
	}

	@Override
	public void saveAccountData(AccountData accountData) {
		Connection  connection = null;
		
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{			
			connection = connectionManager.getConnection();
			AccountDataFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();			
			query = getQueryForAccountData();
			preparedStatement = connection.prepareStatement(query);
			if(preparedStatement == null){
				LogManager.getLogger().debug(MODULE,"Preparedstatement is null while getting accountdata");
				return;
			}
			preparedStatement.setString(1,accountData.getUserIdentity());
			setQueryTimeout(preparedStatement,getDbQueryTimeout());
			long queryExecutionTime = 0;
			queryExecutionTime = System.currentTimeMillis();
			resultSet = preparedStatement.executeQuery();
			queryExecutionTime = (System.currentTimeMillis() - queryExecutionTime);
			
			
			if(10 < queryExecutionTime)
				getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
						"OPEN-DB Profile Lookup Query execution time getting high in caching, - Last Query execution time = " 
								+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");
			
			if(resultSet.next()){
				resultSet.close();
				preparedStatement.close();
				
				String updateQuery = getUpdateQueryForAccountData();
				LogManager.getLogger().debug(MODULE,"Update Query :"+updateQuery);
				preparedStatement =  connection.prepareStatement(updateQuery);
				
				long updateQueryExecutionTime = 0;
				updateQueryExecutionTime = System.currentTimeMillis();
	
				int fieldIndex=1;
				
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME) != null){
					preparedStatement.setString(fieldIndex, accountData.getUserIdentity());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY) != null){
					preparedStatement.setString(fieldIndex, accountData.getAccessPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAccountStatus());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCallbackId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCalledStationId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCallingStationId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCheckItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getConcurrentLoginPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT)!=null){
					preparedStatement.setLong(fieldIndex, accountData.getCreditLimit());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCUI());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCustomerServices());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCustomerType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL)!=null){
					preparedStatement.setString(fieldIndex, accountData.getEmailId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getEncryptionType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE)!=null){
					preparedStatement.setObject(fieldIndex, accountData.getExpiryDate());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGracePolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGroupName());
					fieldIndex++;
				}
					
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getHotlinePolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION)!=null){
					preparedStatement.setString(fieldIndex, accountData.getIPPoolName());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDynamicCheckItems());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME)!=null){
					preparedStatement.setLong(fieldIndex, accountData.getMaxSessionTime());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam1());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam2());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam3());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam4());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam5());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD)!=null){
					preparedStatement.setString(fieldIndex, accountData.getPassword());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK)!=null){
					preparedStatement.setString(fieldIndex, accountData.getPasswordCheck());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAuthorizationPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAdditionalPolicy());
					fieldIndex++;
				}
				
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getRejectItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getReplyItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getServiceType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION)!=null){
					preparedStatement.setBoolean(fieldIndex, accountData.isMacValidation());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getImsi());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMeid());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMsisdn());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMdn());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getImei());
					fieldIndex++;
				}
				
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceVendor());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceName());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDevicePort());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceVLAN());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGeoLocation());
					fieldIndex++;
				}
				preparedStatement.setString(fieldIndex,accountData.getUserIdentity());
				
				int result = preparedStatement.executeUpdate();
				if (result == 0) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Couldn't update account data in DB for user identity: " + accountData.getUserIdentity());
					}
				}
				updateQueryExecutionTime = System.currentTimeMillis() - updateQueryExecutionTime;
				if(DEFAULT_QUERY_TIMEOUT < updateQueryExecutionTime)
					getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
							"OPEN-DB Update query execution time getting high in caching profile, - Last Query execution time = "
									+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");

				connection.commit();
			}else{
				
				preparedStatement.close();
				String insertQuery = getInsertQueryForAccountData();
				LogManager.getLogger().debug(MODULE,"Insert Query :"+insertQuery);
				
				long insertQueryExecutionTime = 0;
				insertQueryExecutionTime = System.currentTimeMillis();
				
				
				
				preparedStatement =  connection.prepareStatement(insertQuery);
				
				int fieldIndex=1;
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME) != null){
					preparedStatement.setString(fieldIndex, accountData.getUserIdentity());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY) != null){
					preparedStatement.setString(fieldIndex, accountData.getAccessPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAccountStatus());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCallbackId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCalledStationId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCallingStationId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCheckItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getConcurrentLoginPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT)!=null){
					preparedStatement.setLong(fieldIndex, accountData.getCreditLimit());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCUI());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCustomerServices());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getCustomerType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL)!=null){
					preparedStatement.setString(fieldIndex, accountData.getEmailId());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getEncryptionType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE)!=null){
					if(accountData.getExpiryDate()!=null){
						Timestamp t = new Timestamp(accountData.getExpiryDate().getTime());
						preparedStatement.setTimestamp(fieldIndex, t);
						fieldIndex++;	
					}else{
						preparedStatement.setTimestamp(fieldIndex, null);
						fieldIndex++;	
						
					}
					
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGracePolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGroupName());
					fieldIndex++;
				}
					
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getHotlinePolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION)!=null){
					preparedStatement.setString(fieldIndex, accountData.getIPPoolName());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDynamicCheckItems());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME)!=null){
					preparedStatement.setLong(fieldIndex, accountData.getMaxSessionTime());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam1());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam2());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam3());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam4());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5)!=null){
					preparedStatement.setString(fieldIndex, accountData.getParam5());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD)!=null){
					preparedStatement.setString(fieldIndex, accountData.getPassword());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK)!=null){
					preparedStatement.setString(fieldIndex, accountData.getPasswordCheck());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAuthorizationPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY)!=null){
					preparedStatement.setString(fieldIndex, accountData.getAdditionalPolicy());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getRejectItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS)!=null){
					preparedStatement.setString(fieldIndex, accountData.getReplyItem());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE)!=null){
					preparedStatement.setString(fieldIndex, accountData.getServiceType());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION)!=null){
					preparedStatement.setBoolean(fieldIndex, accountData.isMacValidation());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getImsi());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMeid());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMsisdn());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getMdn());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI)!=null){
					preparedStatement.setString(fieldIndex, accountData.getImei());
					fieldIndex++;
				}
				
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceVendor());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceName());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDevicePort());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN)!=null){
					preparedStatement.setString(fieldIndex, accountData.getDeviceVLAN());
					fieldIndex++;
				}
				if(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION)!=null){
					preparedStatement.setString(fieldIndex, accountData.getGeoLocation());
					fieldIndex++;
				}
				
				int result = preparedStatement.executeUpdate();

				if (result == 0) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Couldn't insert account data in DB for user identity: " + accountData.getUserIdentity());
					}
				}

				insertQueryExecutionTime = System.currentTimeMillis() - insertQueryExecutionTime;
				if(DEFAULT_QUERY_TIMEOUT < insertQueryExecutionTime)
					getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
							"OPEN-DB  Insert query execution time getting high in caching profile, - Last Query execution time = "
									+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");
				
				connection.commit();
			}
		}catch (DataSourceException e) {
			if(connectionManager.isDBDownSQLException(e)) {
				connectionManager.getTransactionFactory().markDead();
			} 
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Connection to datasource: " + getDSName() + " is unavailable while getting accountdata, Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}catch(SQLException e){
			LogManager.getLogger().warn(MODULE, "SQL Error in caching profile: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().warn(MODULE, "Error in caching profile: " + e.getMessage());
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}		
	}
	
	public class DBAccountDataValueProvider implements AccountDataValueProvider{

		ResultSet resultSet;
		public DBAccountDataValueProvider(ResultSet resultSet){
			this.resultSet = resultSet;
		}
		
		@Override
		public String getStringValue(String fieldName) {
			String strValue = "";
			try {
				strValue = resultSet.getString(fieldName);
			} catch (SQLException e) {
				LogManager.getLogger().warn(MODULE, "Could not fetch String value for fieldname: " + "for Driver: " +getName());
			}
			return strValue;
		}

		@Override
		public Date getDateValue(String fieldName) {
			Date date = null;
			try{
				date = resultSet.getTimestamp(fieldName);
			} catch (SQLException e){
				LogManager.getLogger().warn(MODULE, "Could not fetch Date value for fieldname: " + fieldName + "for Driver: " +getName());
			} catch (IllegalArgumentException e){
				LogManager.getLogger().warn(MODULE, "Could not fetch Date value for fieldname: " + fieldName + "for Driver: " +getName());
			}
			
			
			return date;
		}
		
	}

	@Override
	public void generateUpAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CLEAR,
				Alerts.DATABASEUP,
				MODULE,
				"Connection is UP for Driver: " + getName() + " (" +getDSName() + ")",
				0, getName() + "(" + getDSName() + ")");
		
		LogManager.getLogger().debug(MODULE, "DB Auth Driver: " + getName()+ " is alive now");
	}
	
	@Override
	public void generateDownAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE,
				"Connection is Down for Driver: " + getName() + " (" +getDSName() + ")",
				0, getName() + "(" +getDSName() + ")");
		
		LogManager.getLogger().warn(MODULE, "DB Auth Driver: " + getName() + " is dead marked");
	}
	
	public abstract String getDSName();
	
	public abstract String getTablename();

	public abstract String getPrimaryKeyColumn();
	
	public abstract String getSequenceName();

	public abstract int getDbQueryTimeout();

	public abstract long getMaxQueryTimeoutCount();
	
	public abstract AccountDataFieldMapping getAccountDataFieldMapping();
	
	public abstract String getProfileLookUpColumnName();
	
	public abstract List<String> getUserIdentityAttributes();
	
	interface DynamicCheckItemUpdater {
		void update(String dynamicCheckItem, String userIdentity) throws UpdateAccountDataFailedException;
	}
	
	class NullDynamicCheckItemUpdater implements DynamicCheckItemUpdater {

		@Override
		public void update(String dynamicCheckItem, String userIdentity) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, format("Dynamic check items will not be updated for identity: %s" 
						+ " Reason, Dynamic check item field mapping not configured for driver: %s.", 
						userIdentity, getName()));
			}
		}
		
	}
	
	class DBDynamicCheckItemUpdater implements DynamicCheckItemUpdater {
		private String dynamicCheckQuery;
		private static final int UPDATE_DEFAULT_DB_QUERY_TIMEOUT = 1000;
		
		public DBDynamicCheckItemUpdater() {
			prepareQuery();
		}
		
		private void prepareQuery() {
			AccountDataFieldMapping accountDataFieldMapping = getAccountDataFieldMapping();
			this.dynamicCheckQuery = format("UPDATE %s SET %s = ? WHERE %s = ?", 
					getTablename(),
					accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS).get(0).getFieldName(),
					getProfileLookUpColumnName());
		}
		
		@Override
		public void update(String dynamicCheckItem, String userIdentity) throws UpdateAccountDataFailedException {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			
			try {
				connection = connectionManager.getConnection();
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Updating dynamic check item in DB for identity: "
							+ userIdentity + ", query: " + dynamicCheckQuery);
				}
				
				preparedStatement = connection.prepareStatement(dynamicCheckQuery);
				if (preparedStatement == null) {
					LogManager.getLogger().debug(MODULE,"Preparedstatement is null.while getting accountdata");
					throw new UpdateAccountDataFailedException("Preparedstatement is null.");
				}
				
				preparedStatement.setString(1, dynamicCheckItem);
				preparedStatement.setString(2, userIdentity);
				
				setQueryTimeout(preparedStatement,getDbQueryTimeout());
				
				long queryExecutionTime = 0;
				queryExecutionTime = System.currentTimeMillis();
				int result = preparedStatement.executeUpdate();
				if (result > 0) {
					connection.commit();
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Dynamic check item updated successfully for identity: " 
								+ userIdentity);
					}
				}
				queryExecutionTime = (System.currentTimeMillis() - queryExecutionTime);
				
				if (UPDATE_DEFAULT_DB_QUERY_TIMEOUT < queryExecutionTime) {
					getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, 
							MODULE, "OPEN-DB Query execution time getting high, - Last Query execution time = "
							+ queryExecutionTime + " milliseconds.", (int)queryExecutionTime, "");
				}
			} catch (DataSourceException e) {
				if (connectionManager.isDBDownSQLException(e)) {
					connectionManager.getTransactionFactory().markDead();
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.DATABASEDOWN, MODULE, 
							"Database with Data Source name " + getDSName() 
							+ " is Down, System marked as dead. Reason: " + e.getMessage(),
							0, getDSName());
				}
				throw new UpdateAccountDataFailedException("Connection to datasource: " + getDSName() + " is unavailable, Reason: " + e.getMessage(), e);
			} catch (SQLRecoverableException e) {
				connectionManager.getTransactionFactory().markDead();
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
						"Database with Data Source name " + getDSName() 
						+ " is Down, System marked as dead. Reason: " + e.getMessage(),
						0, getDSName());
				
				LogManager.getLogger().warn(MODULE,"Database with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage());
				throw new UpdateAccountDataFailedException(e);
			} catch (SQLException e) {
				LogManager.getLogger().trace(MODULE, e);
				if (LogManager.getLogger().isErrorLogLevel()) {
					LogManager.getLogger().error(MODULE, "Error while updating DynamicCheckItems reason: " + e.getMessage());
				}
				int errorCode = e.getErrorCode();
				LogManager.getLogger().warn(MODULE, "SQL Error Code: " + errorCode);
				if (errorCode == QUERY_TIMEOUT_ERRORCODE) {
					iTotalQueryTimeoutCount++;
					if (iTotalQueryTimeoutCount > getMaxQueryTimeoutCount()) {
						connectionManager.getTransactionFactory().markDead();
						getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.DATABASEDOWN, MODULE, 
								"Database with Data Source name " + getDSName() 
								+ " is Down, System marked as dead. Reason: " + e.getMessage(),
								0, getDSName());
						
						LogManager.getLogger().warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so System marked as DEAD");
					} else {
						getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DBQUERYTIMEOUT, MODULE, 
								"DB query TimeOut.", 0, "");
					}
				} else if (connectionManager.isDBDownSQLException(e)) {
					connectionManager.getTransactionFactory().markDead();
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.DATABASEDOWN, MODULE, 
							"Database with Data Source name " + getDSName() 
							+ " is Down, System marked as dead. Reason: " + e.getMessage(),
							0, getDSName());
					
					LogManager.getLogger().warn(MODULE,"Database with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage());
				} else {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.DATABASEDOWN, MODULE, 
							"UnKnown DB Error.", 0, getDSName() + "(Unknown DB Error)");
					
					LogManager.getLogger().warn(MODULE, "UnKnown DB error,Reason: " +e.getMessage());
				}
				throw new UpdateAccountDataFailedException(e);
			} finally {
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
	}
}
