package com.elitecore.aaa.radius.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import com.elitecore.aaa.core.conf.LDAPAuthDriverConfiguration;
import com.elitecore.aaa.core.conf.LDAPAuthDriverConfiguration.LDAP_SEARCH_SCOPE;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.Collectionz;
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
@XmlRootElement(name = "ldap-auth-drivers")
@ConfigurationProperties(moduleName ="RAD-LDAPDRVR-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "ldap-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadLDAPAuthDriverConfigurable extends Configurable implements DriverConfigurable {

	private static String DBFIELD_LOGICALNAME = "LOGICALNAME";
	private static String DBFIELD_ATTRIBUTES = "LDAPATTRIBUTE";
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";
	private static int DEFAULT_QUERY_TIMEOUT = 100;
	private final String MODULE = "RAD-LDAPDRVR-CONFIGURABLE";

	private List<RadLDAPAuthDriverConfigurationImpl> radLdapDriverList;
	@XmlTransient
	private Map<String, LDAPAuthDriverConfiguration> radLdapAuthDriverMap;

	public RadLDAPAuthDriverConfigurable(){
		radLdapDriverList = new ArrayList<RadLDAPAuthDriverConfigurationImpl>();
		this.radLdapAuthDriverMap = new LinkedHashMap<String, LDAPAuthDriverConfiguration>();
	}

	@Override
	@XmlElement(name ="ldap-auth-driver")
	public List<RadLDAPAuthDriverConfigurationImpl> getDriverConfigurationList() {
		return radLdapDriverList;
	}

	public void setRadLdapDriverList(
			List<RadLDAPAuthDriverConfigurationImpl> radLdapDriverList) {
		this.radLdapDriverList = radLdapDriverList;
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
			String baseQuery = new DriverSelectQueryBuilder(DriverTypes.RAD_LDAP_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();	

			List<RadLDAPAuthDriverConfigurationImpl> ldapDriverConfigurationList = new ArrayList<RadLDAPAuthDriverConfigurationImpl>();
			Map<String, LDAPAuthDriverConfiguration> ldapDriverConfigurationMap = new LinkedHashMap<String, LDAPAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);	
			if(psForDriverInstanceId == null){
				throw new SQLException("Cannot establish connection to database ,Reason : Prepared Statment is null");

			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();


			while (rsForDriverInstanceId.next()) {			

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl = new RadLDAPAuthDriverConfigurationImpl();

				/************************************************************************
				 * getting Primary details for configuration of tablename and db properties.
				 *************************************************************************/
				query = getQueryForLDAPAuthDriver();
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading LDAPDriver Configuration");

				}
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);
				preparedStatement.setString(3,driverInstanceId);
				ldapAuthDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);
				setQueryTimeout(preparedStatement,100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					ldapAuthDriverConfigurationImpl.setLDAPdriverid(resultSet.getString("ldapdriverid"));

					ldapAuthDriverConfigurationImpl.setDSName(resultSet.getString("DATASOURCE_NAME"));
					String driverName = resultSet.getString("DRIVER_NAME");
					if( driverName!=null)
						ldapAuthDriverConfigurationImpl.setDriverName(driverName);

					if(resultSet.getString("STATUSCHECKDURATION")!=null && resultSet.getString("STATUSCHECKDURATION").trim().length()>0)
						ldapAuthDriverConfigurationImpl.setStatusCheckDuration(Numbers.parseInt(resultSet.getString("STATUSCHECKDURATION").trim(), ldapAuthDriverConfigurationImpl.getStatusCheckDuration()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Status Check Duration Parameter for Driver :"+driverName+" is not defined ,using default value");
					}	

					if(resultSet.getString("SEARCHSCOPE") != null && resultSet.getString("SEARCHSCOPE").trim().length() > 0){
						int searchScope = Numbers.parseInt(resultSet.getString("SEARCHSCOPE"), LDAP_SEARCH_SCOPE.SCOPE_SUB.value);
						ldapAuthDriverConfigurationImpl.setSearchScope(LDAP_SEARCH_SCOPE.get(searchScope));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Search Scope option not configured for Driver: "+driverName + ". Using default value: " + LDAP_SEARCH_SCOPE.SCOPE_SUB);
					}
					
					if(resultSet.getString("SEARCHFILTER") != null && resultSet.getString("SEARCHFILTER").trim().length() > 0){
						ldapAuthDriverConfigurationImpl.setSearchFilter(resultSet.getString("SEARCHFILTER"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Search Filter not configured for Driver: "+driverName);
					}
					
					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						ldapAuthDriverConfigurationImpl.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					}

					String strExpiryDatePatterns = resultSet.getString("expirydate_pattern"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						ldapAuthDriverConfigurationImpl.setExpriryPatterns(strExpiryDatePatterns);
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("password_decrypt_type")!=null && resultSet.getString("password_decrypt_type").length()>0){
						ldapAuthDriverConfigurationImpl.setPasswordDecryptType(Numbers.parseInt(resultSet.getString("password_decrypt_type").trim(),ldapAuthDriverConfigurationImpl.getPasswordDecryptType()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Password Decrypt Type Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("MAXQUERYTIMEOUTCOUNT")!=null && resultSet.getString("MAXQUERYTIMEOUTCOUNT").length()>0){
						ldapAuthDriverConfigurationImpl.setMaxQueryTimeoutCount(Numbers.parseLong(resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim(), ldapAuthDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Max Query Timeout Count Parameter for Driver: "+driverName+" is not defined");
					}

					if(resultSet.getString("query_max_exec_time")!=null && resultSet.getString("query_max_exec_time").length()>0){
						ldapAuthDriverConfigurationImpl.setQueryMaxExecTime(Numbers.parseLong(resultSet.getString("query_max_exec_time").trim(), ldapAuthDriverConfigurationImpl.getQueryMaxExecTime()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Query Maximum Exec Time Parameter for Driver :"+driverName+" is not defined ,using default value");
					}
				}

				/**************************************************************************
				 * setting all the configured fields for accountData
				 *************************************************************************/
				query = getQueryForFieldMapping();
				psForFieldMapping = connection.prepareStatement(query);
				if(psForFieldMapping == null){
					throw new SQLException("Prepared Statement is null for reading LDAPDriver Configuration");
				}
				psForFieldMapping.setString(1,ldapAuthDriverConfigurationImpl.getLDAPdriverid());			
				setQueryTimeout(psForFieldMapping,100);			
				resultSetForFieldMapping =  psForFieldMapping.executeQuery();

				AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
				List<DataFieldMappingImpl> fieldMappingList = new ArrayList<DataFieldMappingImpl>();
				while(resultSetForFieldMapping.next()){
					String defaultValue = resultSetForFieldMapping.getString(DEFAULT_VALUE);
					String valueMapping = resultSetForFieldMapping.getString(VALUE_MAPPING);
					DataFieldMappingImpl dataFieldMappingImpl = new DataFieldMappingImpl(resultSetForFieldMapping.getString(DBFIELD_ATTRIBUTES), defaultValue, valueMapping, resultSetForFieldMapping.getString(DBFIELD_LOGICALNAME));
					fieldMappingList.add(dataFieldMappingImpl);

				}	
				tempAccountDataFieldMapping.setFieldMappingList(fieldMappingList);
				ldapAuthDriverConfigurationImpl.setAccountDataFieldMapping(tempAccountDataFieldMapping);
				ldapDriverConfigurationList.add(ldapAuthDriverConfigurationImpl);
				ldapDriverConfigurationMap.put(driverInstanceId, ldapAuthDriverConfigurationImpl);
			}

			this.radLdapDriverList = ldapDriverConfigurationList;
			this.radLdapAuthDriverMap = ldapDriverConfigurationMap;
		}finally{
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
	public void reloadLDAPAuthDriverConfiguration() throws Exception{

		int size = this.radLdapDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadLDAPAuthDriverConfigurationImpl radLDAPAuthDriverConfigurationImpl = radLdapDriverList.get(i);
			queryBuilder.append("'" + radLDAPAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + radLdapDriverList.get(size - 1).getDriverInstanceId() + "')");
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
				RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl = (RadLDAPAuthDriverConfigurationImpl) radLdapAuthDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = ldapAuthDriverConfigurationImpl.getDriverInstanceId();

				String query = getQueryForReloadingLDAPAuthDriver();
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading LDAPDriver Configuration");

				}
				preparedStatement.setString(1,driverInstanceId);
				
				ldapAuthDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);
				setQueryTimeout(preparedStatement,100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					String driverName = ldapAuthDriverConfigurationImpl.getDriverName();
						
					if(resultSet.getString("SEARCHSCOPE") != null && resultSet.getString("SEARCHSCOPE").trim().length() > 0){
						int searchScope = Numbers.parseInt(resultSet.getString("SEARCHSCOPE"), LDAP_SEARCH_SCOPE.SCOPE_SUB.value);
						ldapAuthDriverConfigurationImpl.setSearchScope(LDAP_SEARCH_SCOPE.get(searchScope));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Search Scope option not configured for Driver: "+driverName + ". Using default value: " + LDAP_SEARCH_SCOPE.SCOPE_SUB);
					}
					
					if(resultSet.getString("SEARCHFILTER") != null && resultSet.getString("SEARCHFILTER").trim().length() > 0){
						ldapAuthDriverConfigurationImpl.setSearchFilter(resultSet.getString("SEARCHFILTER"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Search Filter not configured for Driver: "+driverName);
					}
					
					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						ldapAuthDriverConfigurationImpl.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					}

					String strExpiryDatePatterns = resultSet.getString("expirydate_pattern"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						ldapAuthDriverConfigurationImpl.setExpriryPatterns(strExpiryDatePatterns);
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("password_decrypt_type")!=null && resultSet.getString("password_decrypt_type").length()>0){
						ldapAuthDriverConfigurationImpl.setPasswordDecryptType(Numbers.parseInt(resultSet.getString("password_decrypt_type").trim(),ldapAuthDriverConfigurationImpl.getPasswordDecryptType()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Password Decrypt Type Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("MAXQUERYTIMEOUTCOUNT")!=null && resultSet.getString("MAXQUERYTIMEOUTCOUNT").length()>0){
						ldapAuthDriverConfigurationImpl.setMaxQueryTimeoutCount(Numbers.parseLong(resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim(), ldapAuthDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Max Query Timeout Count Parameter for Driver: "+driverName+" is not defined");
					}

					if(resultSet.getString("query_max_exec_time")!=null && resultSet.getString("query_max_exec_time").length()>0){
						ldapAuthDriverConfigurationImpl.setQueryMaxExecTime(Numbers.parseLong(resultSet.getString("query_max_exec_time").trim(), ldapAuthDriverConfigurationImpl.getQueryMaxExecTime()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Query Maximum Exec Time Parameter for Driver :"+driverName+" is not defined ,using default value");
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
		if(this.radLdapDriverList != null && this.radLdapDriverList.size() > 0){
			int size = this.radLdapDriverList.size();
			RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl;
			for(int i= 0;i<size;i++){
				ldapAuthDriverConfigurationImpl = radLdapDriverList.get(i);
				
				postProcessingForUserIdentityAttributes(ldapAuthDriverConfigurationImpl);
				

				List<DataFieldMappingImpl> fieldMappingdList = ldapAuthDriverConfigurationImpl.getAccountDataFieldMapping().getFieldMappingList();
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
				ldapAuthDriverConfigurationImpl.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);

				postProcessingForExpiryDatePatterns(ldapAuthDriverConfigurationImpl);

			}	
		}

	}
	private void postProcessingForExpiryDatePatterns(
			RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl) {
		String strExpiryDatePattern = ldapAuthDriverConfigurationImpl.getExpriryPatterns();
		String[] arrExpiryDatePatterns =null;
		if(strExpiryDatePattern != null && strExpiryDatePattern.trim().length() > 0){
			arrExpiryDatePatterns = strExpiryDatePattern.split(",");
		}if(arrExpiryDatePatterns != null && arrExpiryDatePatterns.length > 0){
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[arrExpiryDatePatterns.length];
			for(int j=0;j<arrExpiryDatePatterns.length;j++){
				tempSimpleDateFormats[j] = new SimpleDateFormat(arrExpiryDatePatterns[j]);
				tempSimpleDateFormats[j].setLenient(false);
			}
			ldapAuthDriverConfigurationImpl.setExpiryDatePatterns(tempSimpleDateFormats);
		}else {
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[1];
			tempSimpleDateFormats[0] =new SimpleDateFormat("MM/dd/yyyy");
			ldapAuthDriverConfigurationImpl.setExpiryDatePatterns(tempSimpleDateFormats);
		}
	}

	private void postProcessingForUserIdentityAttributes(
			RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl) {
		String strUserIdentity = ldapAuthDriverConfigurationImpl.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttribute = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttribute.add(userId);
			}
			ldapAuthDriverConfigurationImpl.setUserIdentityAttribute(userIdentityAttribute);
		}
	}

	private String getQueryForLDAPAuthDriver(){
		return  "SELECT A.*,B.NAME AS DATASOURCE_NAME,B.STATUSCHECKDURATION,B.TIMEOUT,C.NAME AS DRIVER_NAME  FROM tblmldapauthdriver A, tblmldapds B ,tblmdriverinstance C WHERE A.driverinstanceid=?" + 
		"AND B.ldapdsid = (Select ldapdsid from tblmldapauthdriver where driverinstanceid=?) AND C.driverinstanceid=?";

	}
	private String getQueryForReloadingLDAPAuthDriver(){
		return  "SELECT EXPIRYDATE_PATTERN,SEARCHSCOPE,MAXQUERYTIMEOUTCOUNT,QUERY_MAX_EXEC_TIME,USERIDENTITYATTRIBUTES,SEARCHFILTER,PASSWORD_DECRYPT_TYPE FROM TBLMLDAPAUTHDRIVER WHERE DRIVERINSTANCEID=?";

	}

	private String getQueryForFieldMapping(){
		return "SELECT * FROM tblmldapauthfieldmap WHERE LDAPDRIVERID=?";
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

		if(Collectionz.isNullOrEmpty(radLdapDriverList)) {
			return;
		}
		
		int size = this.radLdapDriverList.size();
		RadLDAPAuthDriverConfigurationImpl ldapAuthDriverConfigurationImpl;
		for(int i= 0;i<size;i++){
			ldapAuthDriverConfigurationImpl = radLdapDriverList.get(i);
			postProcessingForUserIdentityAttributes(ldapAuthDriverConfigurationImpl);
			postProcessingForExpiryDatePatterns(ldapAuthDriverConfigurationImpl);

		}	
	}
}
