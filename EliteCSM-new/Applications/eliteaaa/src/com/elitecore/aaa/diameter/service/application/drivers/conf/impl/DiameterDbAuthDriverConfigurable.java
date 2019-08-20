package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

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
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.CacheParameters;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
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
@ConfigurationProperties(moduleName = "DIAMETER-DBADC-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "db-auth-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterDbAuthDriverConfigurable extends Configurable{

	private final String MODULE = "DIAMETER-DBADC-CONFIGURABLE";
	private static int DEFAULT_QUERY_TIMEOUT = 100;
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";
	private static String FIELDMAP_TABLE = "tblmopendbauthfieldmap";
	/* Fields of tblmopendbauthfieldmap*/
	private static String DBFIELD_LOGICALNAME = "logicalname";
	private static String DBFIELD_FIELD = "dbfield";

	private List<DiameterDbAuthDriverConfigurationImpl> authdriverDetailsList =  new ArrayList<DiameterDbAuthDriverConfigurationImpl>();
	@XmlTransient
	private Map<String, DBAuthDriverConfiguration> driverConfigurationMap = new LinkedHashMap<String, DBAuthDriverConfiguration>();
	
	public DiameterDbAuthDriverConfigurable(){
		authdriverDetailsList =  new ArrayList<DiameterDbAuthDriverConfigurationImpl>();
	}

	@XmlElement(name = "db-auth-driver")
	public List<DiameterDbAuthDriverConfigurationImpl> getAuthdriverDetails() {
		return authdriverDetailsList;
	}

	public void setAuthdriverDetails(List<DiameterDbAuthDriverConfigurationImpl> authdriverDetails) {
		this.authdriverDetailsList = authdriverDetails;
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

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery ="select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIAMETER_DB_DRIVER.value + "'";
					
			List<DiameterDbAuthDriverConfigurationImpl> driverConfigurationList = new ArrayList<DiameterDbAuthDriverConfigurationImpl>();
			Map<String, DBAuthDriverConfiguration> driverConfigurationMap = new LinkedHashMap<String, DBAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);	//NOSONAR - Reason: SQL binding mechanisms should be used	
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading diameter db auth driver configuration, reason: prepared Statement is null");
			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				DiameterDbAuthDriverConfigurationImpl dbAuthDriverDetails = new DiameterDbAuthDriverConfigurationImpl();
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				dbAuthDriverDetails.setDriverInstanceId(driverInstanceId);


				
				/************************************************************************
				 * getting Primary details for configuration of tablename and db properties.
				 *************************************************************************/

				// TODO Need to varify after having more number of records in tblmopendbauthdriver  
				query = getQueryForOpendbAuthDriver();			
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem reading diameter db auth driver configuration, reason: prepared statement received is NULL");
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
							throw new SQLException("Problem reading diameter db auth driver field mapping configuration, reason: prepared statement received is NULL");
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
					driverConfigurationMap.put(dbAuthDriverDetails.getDriverInstanceId(), dbAuthDriverDetails);
					out.close();
				}
			}
			this.authdriverDetailsList = driverConfigurationList;
			this.driverConfigurationMap = driverConfigurationMap;
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
	public void reloadDiameterDBAuthDriverConfiguration() throws Exception{


		int size = this.authdriverDetailsList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			DiameterDbAuthDriverConfigurationImpl nasDBAuthDriverConfigurationImpl = authdriverDetailsList.get(i);
			queryBuilder.append("'" + nasDBAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + authdriverDetailsList.get(size - 1).getDriverInstanceId() + "')");
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
				DiameterDbAuthDriverConfigurationImpl dbAuthDriverDetails = (DiameterDbAuthDriverConfigurationImpl) driverConfigurationMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));
				String driverInstanceId = dbAuthDriverDetails.getDriverInstanceId();

				String query = getQueryForReloadingpendbAuthDriver();			
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem reading diameter db auth driver configuration, reason: prepared statement received is NULL");
				}
				preparedStatement.setString(1,driverInstanceId);

				setQueryTimeout(preparedStatement,100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){

					String driverName = dbAuthDriverDetails.getDriverName();

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
		if(authdriverDetailsList != null && authdriverDetailsList.size() >0){
			int size = authdriverDetailsList.size();
			DiameterDbAuthDriverConfigurationImpl dbAuthDriverDetails;
			for(int i = 0;i<size;i++){
				dbAuthDriverDetails = authdriverDetailsList.get(i);
				
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
			DiameterDbAuthDriverConfigurationImpl dbAuthDriverDetails) {
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
		if(authdriverDetailsList != null && authdriverDetailsList.size() >0){
			int size = authdriverDetailsList.size();
			DiameterDbAuthDriverConfigurationImpl dbAuthDriverDetails;
			for(int i = 0;i<size;i++){
				dbAuthDriverDetails = authdriverDetailsList.get(i);
				postProcessingForUserIdentityAttributes(dbAuthDriverDetails);
				
			}
		}
	
	}
	
	private String getReadQueryForNasServicePolicyConfiguration() {
		DiameterNasServiceConfigurable diameterNasServiceConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		if(diameterNasServiceConfigurable == null) {
			return "''";
	}
		List<String> servicePolicies = diameterNasServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+")";

	}
	
	private String getReadQueryForEapServicePolicyConfiguration() {

		DiameterEAPServiceConfigurable diameterEAPServiceConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);
		if(diameterEAPServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies =  diameterEAPServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
	
	}

