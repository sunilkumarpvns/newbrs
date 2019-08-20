package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.elitecore.aaa.core.conf.HttpAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
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
@XmlRootElement(name = "http-auth-drivers")
@ConfigurationProperties(moduleName = "NAS-HTTP-AUTH-DRIVER-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "http-auth-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterHttpAuthDriverConfigurable extends Configurable{

	private final String MODULE = "NAS-HTTP-AUTH-DRIVER-CONFIGURABLE";
	private static String RESPONSE_PARAMETER_FIELDMAP_TABLE = "tblmhttpauthdriverfieldmap";
	private static String LOGICALNAME = "LOGICALNAME";
	private static String RESPONSE_PARAMETER_INDEX = "RESPONSEPARAMINDEX";
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";
	
	private List<DiameterHttpAuthDriverConfigurationImpl> httpAuthDriverConfigurationImplList;
	@XmlTransient
	private Map<String, HttpAuthDriverConfiguration> nasHttpAuthDriverMap;
	
	public DiameterHttpAuthDriverConfigurable(){
		httpAuthDriverConfigurationImplList = new ArrayList<DiameterHttpAuthDriverConfigurationImpl>();
		this.nasHttpAuthDriverMap = new LinkedHashMap<String, HttpAuthDriverConfiguration>();
	}
	
	@XmlElement(name = "http-auth-driver")
	public List<DiameterHttpAuthDriverConfigurationImpl> getHttpAuthDriverConfigurationImpls() {
		return httpAuthDriverConfigurationImplList;
	}

	public void setHttpAuthDriverConfigurationImpls(List<DiameterHttpAuthDriverConfigurationImpl> httpAuthDriverConfigurationImpls) {
		this.httpAuthDriverConfigurationImplList = httpAuthDriverConfigurationImpls;
	}

	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;	
		PreparedStatement psForResponseParameterFieldMapping = null;
		ResultSet rsForResponseParameterFieldMapping = null;
		
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;


		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIAMETER_HTTP_AUTH_DRIVER.value +"' AND (DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")))";
				
			List<DiameterHttpAuthDriverConfigurationImpl> httpAuthDriverConfigurationList = new ArrayList<DiameterHttpAuthDriverConfigurationImpl>();
			Map<String, HttpAuthDriverConfiguration> httpDriverConfMap = new LinkedHashMap<String, HttpAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);	//NOSONAR - Reason: SQL binding mechanisms should be used	
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading diameter http auth driver configuration, reason: Prepared statement is null");

			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				query = getQueryForDriverConfigurationParameter();
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading Nas Http Auth Driver Configuration");
				}
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					DiameterHttpAuthDriverConfigurationImpl httpAuthDriverConfigurations = new DiameterHttpAuthDriverConfigurationImpl();
					httpAuthDriverConfigurations.setDriverInstanceId(driverInstanceId);
					httpAuthDriverConfigurations.setHttpAuthDriverId(resultSet.getString("HTTPAUTHDRIVERID"));
					String driverName = resultSet.getString("NAME");
					if(driverName!=null)
						httpAuthDriverConfigurations.setDriverName(driverName);

					httpAuthDriverConfigurations.setHttpURL(resultSet.getString("HTTP_URL"));
					httpAuthDriverConfigurations.setStatusCheckDuration(resultSet.getInt("STATUSCHECKDURATION"));
					httpAuthDriverConfigurations.setMaxQueryTimeoutCount(resultSet.getInt("MAXQUERYTIMEOUTCOUNT"));

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						httpAuthDriverConfigurations.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					}

					String strExpiryDatePatterns = resultSet.getString("EXPIRYDATEFORMATS"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						httpAuthDriverConfigurations.setStrExpriryPatterns(strExpiryDatePatterns);	
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					query = getQueryForResponseParameterMapping();
					psForResponseParameterFieldMapping = connection.prepareStatement(query);

					if(psForResponseParameterFieldMapping == null){
							throw new SQLException("Problem reading diameter http  auth driver response parameter field mapping configuration, reason: prepared statement received is NULL");
					}
					psForResponseParameterFieldMapping.setString(1, httpAuthDriverConfigurations.getHttpAuthDriverId());
					rsForResponseParameterFieldMapping =  psForResponseParameterFieldMapping.executeQuery();

					StringWriter stringBuffer = new StringWriter();
					PrintWriter out = new PrintWriter(stringBuffer);
					out.println();
					AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
					List<DataFieldMappingImpl> dataFieldMappingList = new ArrayList<DataFieldMappingImpl>();
					while(rsForResponseParameterFieldMapping.next()){
						String logicalName = rsForResponseParameterFieldMapping.getString(LOGICALNAME);
						String index  = rsForResponseParameterFieldMapping.getString(RESPONSE_PARAMETER_INDEX); 
						String defaultValue = rsForResponseParameterFieldMapping.getString(DEFAULT_VALUE);
						String valueMapping = rsForResponseParameterFieldMapping.getString(VALUE_MAPPING);
						
						if(logicalName !=null && logicalName.trim().length()>0 && index !=null && index.trim().length()>0){
							try {
								int iIndex = Integer.parseInt(index);
								if (iIndex > 0){
									dataFieldMappingList.add(new DataFieldMappingImpl(String.valueOf(iIndex-1), defaultValue, valueMapping, logicalName));
									out.println("        "+logicalName +" = " + index);
									out.println("            Default Value: "  + defaultValue);
									out.println("            Value Mapping: " + valueMapping);
								} 
							} catch (Exception e) {
							}

						}
					}
					tempAccountDataFieldMapping.setFieldMappingList(dataFieldMappingList);
					httpAuthDriverConfigurations.setAccountDataFieldMapping(tempAccountDataFieldMapping);
					httpAuthDriverConfigurationList.add(httpAuthDriverConfigurations);
					httpDriverConfMap.put(driverInstanceId, httpAuthDriverConfigurations);
					out.close();
				}

			}
			this.httpAuthDriverConfigurationImplList = httpAuthDriverConfigurationList;
			this.nasHttpAuthDriverMap = httpDriverConfMap;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(rsForResponseParameterFieldMapping);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(psForResponseParameterFieldMapping);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadDiameterHttpAuthDriverConfiguration() throws Exception {
		int size = this.httpAuthDriverConfigurationImplList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			DiameterHttpAuthDriverConfigurationImpl nasHttpAuthDriverConfigurationImpl = httpAuthDriverConfigurationImplList.get(i);
			queryBuilder.append("'" + nasHttpAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + httpAuthDriverConfigurationImplList.get(size - 1).getDriverInstanceId() + "')");
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
				DiameterHttpAuthDriverConfigurationImpl httpAuthDriverConfiguration = (DiameterHttpAuthDriverConfigurationImpl) this.nasHttpAuthDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = httpAuthDriverConfiguration.getDriverInstanceId();

				String query = getQueryForReloadingDriverConfigurationParameter();
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading Nas Http Auth Driver Configuration");
				}
				preparedStatement.setString(1,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					String driverName = httpAuthDriverConfiguration.getDriverName();
					
					httpAuthDriverConfiguration.setStatusCheckDuration(resultSet.getInt("STATUSCHECKDURATION"));
					httpAuthDriverConfiguration.setMaxQueryTimeoutCount(resultSet.getInt("MAXQUERYTIMEOUTCOUNT"));

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						httpAuthDriverConfiguration.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					}

					String strExpiryDatePatterns = resultSet.getString("EXPIRYDATEFORMATS"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						httpAuthDriverConfiguration.setStrExpriryPatterns(strExpiryDatePatterns);	
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
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
		if(httpAuthDriverConfigurationImplList != null && httpAuthDriverConfigurationImplList.size() >0){
			int size = this.httpAuthDriverConfigurationImplList.size();
			DiameterHttpAuthDriverConfigurationImpl diameterHttpAuthDriverConfigurations;
			for(int j=0;j<size;j++){
				diameterHttpAuthDriverConfigurations = httpAuthDriverConfigurationImplList.get(j);

				postProcessingForExpiryDatePattern(diameterHttpAuthDriverConfigurations);
				postProcessingForUserIdentityAttributes(diameterHttpAuthDriverConfigurations);				
				
				List<DataFieldMappingImpl> fieldMappingdList = diameterHttpAuthDriverConfigurations.getAccountDataFieldMapping().getFieldMappingList();
				Map<String, List<DataFieldMapping>> dataFieldMappingMap = new HashMap<String, List<DataFieldMapping>>();
				if(fieldMappingdList!=null){
					int numOfFieldMapping = fieldMappingdList.size();
					for(int k=0;k<numOfFieldMapping;k++){
						DataFieldMappingImpl dataFieldMapping = fieldMappingdList.get(k);
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
				diameterHttpAuthDriverConfigurations.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
			}
		}
	}

	private void postProcessingForUserIdentityAttributes(
			DiameterHttpAuthDriverConfigurationImpl diameterHttpAuthDriverConfigurations) {
		String strUserIdentity = diameterHttpAuthDriverConfigurations.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttrList = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttrList.add(userId);
			}	
			diameterHttpAuthDriverConfigurations.setUserIdentityAttribute(userIdentityAttrList);
		}
	}

	private void postProcessingForExpiryDatePattern(
			DiameterHttpAuthDriverConfigurationImpl diameterHttpAuthDriverConfigurations) {
		String strExpiryDatePattern = diameterHttpAuthDriverConfigurations.getStrExpriryPatterns();
		String[] arrExpiryDatePatterns =null;
		if(strExpiryDatePattern != null && strExpiryDatePattern.trim().length() > 0){
			arrExpiryDatePatterns = strExpiryDatePattern.split(",");
		}
		if(arrExpiryDatePatterns != null && arrExpiryDatePatterns.length > 0){
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[arrExpiryDatePatterns.length];
			for(int i=0;i<arrExpiryDatePatterns.length;i++){
				tempSimpleDateFormats[i] = new SimpleDateFormat(arrExpiryDatePatterns[i]);
				tempSimpleDateFormats[i].setLenient(false);
			}
			diameterHttpAuthDriverConfigurations.setExpiryPatterns(tempSimpleDateFormats);
		}else {
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[1];
			tempSimpleDateFormats[0] =new SimpleDateFormat("MM/dd/yyyy");
			diameterHttpAuthDriverConfigurations.setExpiryPatterns(tempSimpleDateFormats);
		}
	}

	private String getQueryForDriverConfigurationParameter(){
		return "SELECT A.NAME,B.* FROM TBLMDRIVERINSTANCE A, TBLMHTTPAUTHDRIVER B  where A.DRIVERINSTANCEID=? AND B.DRIVERINSTANCEID=?";
	}
	private String getQueryForReloadingDriverConfigurationParameter(){
		return "SELECT  STATUSCHECKDURATION, MAXQUERYTIMEOUTCOUNT,EXPIRYDATEFORMATS,USERIDENTITYATTRIBUTES FROM TBLMHTTPAUTHDRIVER where DRIVERINSTANCEID=?";
	}
	private String getQueryForResponseParameterMapping(){
		return "SELECT * FROM " + RESPONSE_PARAMETER_FIELDMAP_TABLE +" WHERE HTTPAUTHDRIVERID=?";
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(httpAuthDriverConfigurationImplList != null && httpAuthDriverConfigurationImplList.size() >0){
			int size = this.httpAuthDriverConfigurationImplList.size();
			DiameterHttpAuthDriverConfigurationImpl diameterHttpAuthDriverConfigurations;
			for(int j=0;j<size;j++){
				diameterHttpAuthDriverConfigurations = httpAuthDriverConfigurationImplList.get(j);
				postProcessingForExpiryDatePattern(diameterHttpAuthDriverConfigurations);
				postProcessingForUserIdentityAttributes(diameterHttpAuthDriverConfigurations);				
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
		List<String> servicePolicies = diameterEAPServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
	
}