package com.elitecore.aaa.radius.drivers.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.CacheParameters;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "db-auth-drivers")
@ConfigurationProperties(moduleName ="RAD_DBADC_DRV_CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "db-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadDBAuthDriverConfigurable extends Configurable implements DriverConfigurable {

	private final String MODULE = "RAD_DBADC_DRV_CONFIGURABLE";
	private static int DEFAULT_QUERY_TIMEOUT = 100;
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";
	private static String FIELDMAP_TABLE = "tblmopendbauthfieldmap";
	/* Fields of tblmopendbauthfieldmap*/
	private static String DBFIELD_LOGICALNAME = "logicalname";
	private static String DBFIELD_FIELD = "dbfield";

	private List<RadDBAuthDriverConfigurationImpl> radDBAuthDriverList =  new ArrayList<RadDBAuthDriverConfigurationImpl>();
	@XmlTransient
	private Map<String, DBAuthDriverConfiguration> driverConfigurationMap = new LinkedHashMap<String, DBAuthDriverConfiguration>();


	@Override
	@XmlElement(name = "db-auth-driver")
	public List<RadDBAuthDriverConfigurationImpl> getDriverConfigurationList() {
		return radDBAuthDriverList;
	}

	public void setRadDBAuthdriverList(List<RadDBAuthDriverConfigurationImpl> authdriverDetails) {
		this.radDBAuthDriverList = authdriverDetails;
	}


	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		ResultSet resultSetForFieldMapping = null;
		PreparedStatement psForFieldMapping = null;

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = new DriverSelectQueryBuilder(DriverTypes.RAD_OPENDB_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();	

			List<RadDBAuthDriverConfigurationImpl> driverConfigurationList = new ArrayList<RadDBAuthDriverConfigurationImpl>();
			Map<String, DBAuthDriverConfiguration> dbAuthDriverMap = new LinkedHashMap<String, DBAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);		
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading rad db auth driver configuration, reason: prepared Statement is null");
			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				RadDBAuthDriverConfigurationImpl dbAuthDriverDetails = new RadDBAuthDriverConfigurationImpl();
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				dbAuthDriverDetails.setDriverInstanceId(driverInstanceId);


				
				/************************************************************************
				 * getting Primary details for configuration of tablename and db properties.
				 *************************************************************************/

				// TODO Need to varify after having more number of records in tblmopendbauthdriver  
				query = getQueryForOpendbAuthDriver();			
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem reading rad db auth driver configuration, reason: prepared statement received is NULL");
				}
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);
				preparedStatement.setString(3,driverInstanceId);


				setQueryTimeout(preparedStatement,100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					String openDbAuthId = resultSet.getString("opendbauthid");
					dbAuthDriverDetails.setDSName(resultSet.getString("DATASOURCE_NAME"));

					String driverName = resultSet.getString("DRIVER_NAME");
					if(driverName!=null)
						dbAuthDriverDetails.setDriverName(driverName);

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						dbAuthDriverDetails.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					dbAuthDriverDetails.setTablename(resultSet.getString("tablename"));

					if(resultSet.getString("PROFILELOOKUPCOLUMN")!=null && resultSet.getString("PROFILELOOKUPCOLUMN").trim().length()>0)
						dbAuthDriverDetails.setProfileLookUpColumnName(resultSet.getString("PROFILELOOKUPCOLUMN"));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Profile Lookup Column Parameter  for Driver :"+driverName+" is not defined ,using default value");
					}
					if(resultSet.getString("dbquerytimeout")!=null && resultSet.getString("dbquerytimeout").length()>0){
						dbAuthDriverDetails.setDbQueryTimeout(Numbers.parseInt(resultSet.getString("dbquerytimeout").trim(), dbAuthDriverDetails.getDbQueryTimeout()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "DB Query Timeout Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("maxquerytimeoutcount")!=null && resultSet.getString("maxquerytimeoutcount").length()>0){
						dbAuthDriverDetails.setMaxQueryTimeoutCount(Numbers.parseLong(resultSet.getString("maxquerytimeoutcount").trim(), dbAuthDriverDetails.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "DB Query Timeout Parameter for Driver :"+driverName+" is not defined ,using default value");
					}
					
					CacheParameters cacheParameters=new CacheParameters();
					
					cacheParameters.setPrimaryKeyColumn(resultSet.getString("PRIMARYKEYCOLUMN"));
					cacheParameters.setSequenceName(resultSet.getString("SEQUENCENAME"));
					
					dbAuthDriverDetails.setCacheParams(cacheParameters);
					
					/**************************************************************************
					 * setting all the configured fields for accountData
					 *************************************************************************/

					query = getQueryForFieldMapping();
					psForFieldMapping = connection.prepareStatement(query);

					if(psForFieldMapping == null){
							throw new SQLException("Problem reading rad db auth driver field mapping configuration, reason: prepared statement received is NULL");
					}
					
				
					psForFieldMapping.setString(1, openDbAuthId);			
					setQueryTimeout(psForFieldMapping, dbAuthDriverDetails.getDbQueryTimeout());

					resultSetForFieldMapping =  psForFieldMapping.executeQuery();

					StringWriter stringBuffer = new StringWriter();
					PrintWriter out = new PrintWriter(stringBuffer);
					out.println();
					AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
					List<DataFieldMappingImpl> fieldMappingList = new ArrayList<DataFieldMappingImpl>();
					while(resultSetForFieldMapping.next()){
						String defaultValue = resultSetForFieldMapping.getString(DEFAULT_VALUE);
						String valueMapping = resultSetForFieldMapping.getString(VALUE_MAPPING);
						String dbFieldLogicalName = resultSetForFieldMapping.getString(DBFIELD_LOGICALNAME);
						String dbField = resultSetForFieldMapping.getString(DBFIELD_FIELD);
						
						DataFieldMappingImpl dataFieldMappingImpl = new DataFieldMappingImpl(dbField, defaultValue, valueMapping, dbFieldLogicalName);
						fieldMappingList.add(dataFieldMappingImpl);
						out.println("        "+dbFieldLogicalName +" = " + dbField);
						out.println("            Default Value: "  + defaultValue);
						out.println("            Value Mapping: " + valueMapping);														
					}
					tempAccountDataFieldMapping.setFieldMappingList(fieldMappingList);
					dbAuthDriverDetails.setAccountDataFieldMapping(tempAccountDataFieldMapping);
					driverConfigurationList.add(dbAuthDriverDetails);
					dbAuthDriverMap.put(driverInstanceId, dbAuthDriverDetails);
					out.close();
				}
			}
			this.radDBAuthDriverList = driverConfigurationList;
			this.driverConfigurationMap = dbAuthDriverMap;
		} finally{
			DBUtility.closeQuietly(resultSetForFieldMapping);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(psForFieldMapping);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadDBAuthDriverConfiguration() throws Exception{


		int size = this.radDBAuthDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadDBAuthDriverConfigurationImpl radDBAuthDriverConfigurationImpl = radDBAuthDriverList.get(i);
			queryBuilder.append("'" + radDBAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + radDBAuthDriverList.get(size - 1).getDriverInstanceId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId = null;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			psForDriverInstanceId = conn.prepareStatement(queryForReload);
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while(rsForDriverInstanceId.next()){
				RadDBAuthDriverConfigurationImpl dbAuthDriverDetails = (RadDBAuthDriverConfigurationImpl) driverConfigurationMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));
				String driverInstanceId = dbAuthDriverDetails.getDriverInstanceId();

				String query = getQueryForReloadingpendbAuthDriver();			
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Problem reloading rad db auth driver configuration, reason: prepared statement received is NULL");
				}
				preparedStatement.setString(1,driverInstanceId);
				setQueryTimeout(preparedStatement,100);			
				resultSet =  preparedStatement.executeQuery();
				String driverName = dbAuthDriverDetails.getDriverName();

				if(resultSet.next()){

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						dbAuthDriverDetails.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					if(resultSet.getString("dbquerytimeout")!=null && resultSet.getString("dbquerytimeout").length()>0){
						dbAuthDriverDetails.setDbQueryTimeout(Numbers.parseInt(resultSet.getString("dbquerytimeout").trim(), dbAuthDriverDetails.getDbQueryTimeout()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "DB Query Timeout Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("maxquerytimeoutcount")!=null && resultSet.getString("maxquerytimeoutcount").length()>0){
						dbAuthDriverDetails.setMaxQueryTimeoutCount(Numbers.parseLong(resultSet.getString("maxquerytimeoutcount").trim(), dbAuthDriverDetails.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "DB Query Timeout Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

				}
			}
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(conn);
		}

	}
	
	@PostRead
	public void postReadProcessing() {
		if(radDBAuthDriverList != null && radDBAuthDriverList.size() >0){
			int size = radDBAuthDriverList.size();
			RadDBAuthDriverConfigurationImpl dbAuthDriverDetails;
			for(int i = 0;i<size;i++){
				dbAuthDriverDetails = radDBAuthDriverList.get(i);
				
				postProcessingForUserIdentityAttributes(dbAuthDriverDetails);
				
				List<DataFieldMappingImpl> fieldMappingdList = dbAuthDriverDetails.getAccountDataFieldMapping().getFieldMappingList();
				Map<String, List<DataFieldMapping>> dataFieldMappingMap = new HashMap<String, List<DataFieldMapping>>();
				if(fieldMappingdList!=null){
					int numOfFieldMapping = fieldMappingdList.size();
					for(int j=0;j<numOfFieldMapping;j++){
						DataFieldMappingImpl dataFieldMapping = fieldMappingdList.get(j);
						String logicalName  = dataFieldMapping.getLogicalName();
						if(logicalName!=null){
							List<DataFieldMapping> list  = dataFieldMappingMap.get(logicalName);
							if(list==null){
								list = new ArrayList<DataFieldMapping>();
								dataFieldMappingMap.put(logicalName, list);
							}
							list.add(dataFieldMapping);
						}
					}
				}
				dbAuthDriverDetails.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
			}
		}

	}
	
	private void postProcessingForUserIdentityAttributes(
			RadDBAuthDriverConfigurationImpl dbAuthDriverDetails) {
		
		String strUserIdentity = dbAuthDriverDetails.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttribute = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttribute.add(userId);
			}
			dbAuthDriverDetails.setUserIdentityAttribute(userIdentityAttribute);
		}
	}

	private String getQueryForOpendbAuthDriver(){
		return "SELECT A.*,B.NAME AS DATASOURCE_NAME,B.STATUSCHECKDURATION,B.TIMEOUT,C.NAME AS DRIVER_NAME FROM tblmopendbauthdriver A, tblmdatabaseds B,tblmdriverinstance C WHERE A.driverinstanceid=? " +
		"AND B.databasedsid = (Select databasedsid from tblmopendbauthdriver where driverinstanceid=?) AND C.driverinstanceid=?";

	}
	private String getQueryForReloadingpendbAuthDriver(){
		return "SELECT DBQUERYTIMEOUT,MAXQUERYTIMEOUTCOUNT,USERIDENTITYATTRIBUTES FROM TBLMOPENDBAUTHDRIVER WHERE DRIVERINSTANCEID=?";

	}

	private String getQueryForFieldMapping(){
		return "SELECT * FROM " + FIELDMAP_TABLE +" WHERE opendbauthid=?";
	}
	private void setQueryTimeout(PreparedStatement preparedStatement, int timeout) throws SQLException {
		if(timeout < DEFAULT_QUERY_TIMEOUT)
			timeout=DEFAULT_QUERY_TIMEOUT;
		preparedStatement.setQueryTimeout(timeout);		
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(radDBAuthDriverList != null && radDBAuthDriverList.size() >0){
			int size = radDBAuthDriverList.size();
			RadDBAuthDriverConfigurationImpl dbAuthDriverDetails;
			for(int i = 0;i<size;i++){
				dbAuthDriverDetails = radDBAuthDriverList.get(i);
				postProcessingForUserIdentityAttributes(dbAuthDriverDetails);
			}
		}

	}
}

