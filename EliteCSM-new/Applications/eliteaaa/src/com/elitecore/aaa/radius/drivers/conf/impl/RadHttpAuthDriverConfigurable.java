package com.elitecore.aaa.radius.drivers.conf.impl;

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
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.HttpAuthDriverConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.DBUtility;
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
@ConfigurationProperties(moduleName ="RAD-HTTP-AUTH-DRIVER-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "http-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadHttpAuthDriverConfigurable extends Configurable implements DriverConfigurable {
	
	private final String MODULE = "RAD-HTTP-AUTH-DRIVER-CONFIGURABLE";
	private static String RESPONSE_PARAMETER_FIELDMAP_TABLE = "tblmhttpauthdriverfieldmap";
	private static String LOGICALNAME = "LOGICALNAME";
	private static String RESPONSE_PARAMETER_INDEX = "RESPONSEPARAMINDEX";
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";

	private List<RadHttpAuthDriverConfigurationData> radHttpAuthDriverConfigurationList;
	private Map<String, HttpAuthDriverConfiguration> radHttpAuthDriverMap;

	public RadHttpAuthDriverConfigurable(){
		radHttpAuthDriverConfigurationList = new ArrayList<RadHttpAuthDriverConfigurationData>();
		this.radHttpAuthDriverMap = new LinkedHashMap<String, HttpAuthDriverConfiguration>();
	}
	
	@Override
	@XmlElement(name = "http-auth-driver")
	public List<RadHttpAuthDriverConfigurationData> getDriverConfigurationList() {
		return radHttpAuthDriverConfigurationList;
	}

	public void setHttpAuthDriverConfigurationList(List<RadHttpAuthDriverConfigurationData> httpAuthDriverConfigurationImpls) {
		this.radHttpAuthDriverConfigurationList = httpAuthDriverConfigurationImpls;
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

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = new DriverSelectQueryBuilder(DriverTypes.RAD_HTTP_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();

			List<RadHttpAuthDriverConfigurationData> httpAuthDriverConfigurationList = new ArrayList<RadHttpAuthDriverConfigurationData>();
			Map<String, HttpAuthDriverConfiguration> httpDriverConfMap = new LinkedHashMap<String, HttpAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);		
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading rad http auth driver configuration, reason: Prepared statement is null");

			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				query = getQueryForDriverConfigurationParameter();
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading RadHttp Driver Configuration");
				}
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					RadHttpAuthDriverConfigurationData httpAuthDriverConfigurations = new RadHttpAuthDriverConfigurationData();
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
							throw new SQLException("Problem reading rad http  auth driver response parameter field mapping configuration, reason: prepared statement received is NULL");
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
			this.radHttpAuthDriverConfigurationList = httpAuthDriverConfigurationList;
			this.radHttpAuthDriverMap = httpDriverConfMap;
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
	public void reloadHttpAuthDriverConfiguration() throws Exception{


		int size = this.radHttpAuthDriverConfigurationList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadHttpAuthDriverConfigurationData radHttpAuthDriverConfigurationImpl = radHttpAuthDriverConfigurationList.get(i);
			queryBuilder.append("'" + radHttpAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + radHttpAuthDriverConfigurationList.get(size - 1).getDriverInstanceId() + "')");
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
				RadHttpAuthDriverConfigurationData httpAuthDriverConfiguration = (RadHttpAuthDriverConfigurationData) this.radHttpAuthDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = httpAuthDriverConfiguration.getDriverInstanceId();

				String query = getQueryForReloadingDriverConfigurationParameter();
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Prepared Statement is null for reading RadHttp Driver Configuration");
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
		if(radHttpAuthDriverConfigurationList != null && radHttpAuthDriverConfigurationList.size() >0){
			int size = this.radHttpAuthDriverConfigurationList.size();
			RadHttpAuthDriverConfigurationData radHttpAuthDriverConfigurations;
			for(int j=0;j<size;j++){
				radHttpAuthDriverConfigurations = radHttpAuthDriverConfigurationList.get(j);
				
				postProcessingForExpiryDatePattern(radHttpAuthDriverConfigurations);

				postProcessingForUserIdentityAttributes(radHttpAuthDriverConfigurations);
				
				List<DataFieldMappingImpl> fieldMappingdList = radHttpAuthDriverConfigurations.getAccountDataFieldMapping().getFieldMappingList();
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
				radHttpAuthDriverConfigurations.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
			}
		}
	}
	
	private void postProcessingForUserIdentityAttributes(RadHttpAuthDriverConfigurationData radHttpAuthDriverConfigurations) {
		String strUserIdentity = radHttpAuthDriverConfigurations.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttrList = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttrList.add(userId);
			}	
			radHttpAuthDriverConfigurations.setUserIdentityAttribute(userIdentityAttrList);
		}
	}
	private void postProcessingForExpiryDatePattern(
			RadHttpAuthDriverConfigurationData radHttpAuthDriverConfigurations) {
		String strExpiryDatePattern = radHttpAuthDriverConfigurations.getStrExpriryPatterns();
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
			radHttpAuthDriverConfigurations.setExpiryPatterns(tempSimpleDateFormats);
		}else {
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[1];
			tempSimpleDateFormats[0] =new SimpleDateFormat("MM/dd/yyyy");
			radHttpAuthDriverConfigurations.setExpiryPatterns(tempSimpleDateFormats);
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
		if(radHttpAuthDriverConfigurationList != null && radHttpAuthDriverConfigurationList.size() >0){
			int size = this.radHttpAuthDriverConfigurationList.size();
			RadHttpAuthDriverConfigurationData radHttpAuthDriverConfigurations;
			for(int j=0;j<size;j++){
				radHttpAuthDriverConfigurations = radHttpAuthDriverConfigurationList.get(j);
				postProcessingForExpiryDatePattern(radHttpAuthDriverConfigurations);
				postProcessingForUserIdentityAttributes(radHttpAuthDriverConfigurations);
			}
		}
	
	}
}
