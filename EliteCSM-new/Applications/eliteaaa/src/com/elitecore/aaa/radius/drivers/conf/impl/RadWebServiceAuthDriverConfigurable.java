package com.elitecore.aaa.radius.drivers.conf.impl;

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
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.RadWebServiceAuthDriverConfiguration;
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
@XmlRootElement(name = "webservice-auth-drivers")
@ConfigurationProperties(moduleName ="RAD-WSADC-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "webservice-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadWebServiceAuthDriverConfigurable extends Configurable implements DriverConfigurable {

	private static final String MODULE = "RAD-WSADC-CONFIGURABLE";
	private List<RadWebServiceAuthDriverConfigurationImpl> radWebServiceDriverList;
	@XmlTransient
	private Map<String, RadWebServiceAuthDriverConfiguration> radWebServiceDriverMap;

	public RadWebServiceAuthDriverConfigurable(){
		radWebServiceDriverList = new ArrayList<RadWebServiceAuthDriverConfigurationImpl>();
		this.radWebServiceDriverMap = new LinkedHashMap<String, RadWebServiceAuthDriverConfiguration>();
	}
	
	@Override
	@XmlElement(name = "webservice-auth-driver")
	public List<RadWebServiceAuthDriverConfigurationImpl> getDriverConfigurationList() {
		return radWebServiceDriverList;
	}
	public void setRadWebServiceList(
			List<RadWebServiceAuthDriverConfigurationImpl> radWebServiceList) {
		this.radWebServiceDriverList = radWebServiceList;
	}
	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		PreparedStatement psKeyAccountFieldRel = null;
		ResultSet rsKeyAccountFieldRel = null;		

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;
		
		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = new DriverSelectQueryBuilder(DriverTypes.RAD_WEBSERVICE_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();


			List<RadWebServiceAuthDriverConfigurationImpl> webServiceDriverConfigurationList = new ArrayList<RadWebServiceAuthDriverConfigurationImpl>();
			Map<String, RadWebServiceAuthDriverConfiguration> webServiceDriverConfigurationMap = new LinkedHashMap<String, RadWebServiceAuthDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);	
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading web service auth driver configuration, reason: prepared Statement is null");

			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {	

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				query = getQueryForWebServiceAuthDriver();
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Problem reading web service auth driver configuration, reason: prepared Statement is null");
				}
				preparedStatement.setString(1,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				String wsAuthDriverId = null;
				if(resultSet.next()){
					RadWebServiceAuthDriverConfigurationImpl werServiceDriverConfigurationImpl = new RadWebServiceAuthDriverConfigurationImpl();
					werServiceDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);

					String driverName = resultSet.getString("NAME");
					if(driverName!=null)

						werServiceDriverConfigurationImpl.setDriverName(driverName);

					wsAuthDriverId = resultSet.getString("WSAUTHDRIVERID");
					if(resultSet.getString("SERVICEADDRESS")!=null){
						werServiceDriverConfigurationImpl.setServiceAddress(resultSet.getString("SERVICEADDRESS"));
					}
					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						werServiceDriverConfigurationImpl.setUserIdentities(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					if(resultSet.getString("IMSIATTRIBUTE")!=null && resultSet.getString("IMSIATTRIBUTE").trim().length()>0)
						werServiceDriverConfigurationImpl.setIMSIAttribute(resultSet.getString("IMSIATTRIBUTE"));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "IMSI Attribute Parameter for Driver :"+driverName+" is not defined");
					}
					if(resultSet.getString("MAXQUERYTIMEOUTCOUNT")!=null && resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim().length()>0)
						werServiceDriverConfigurationImpl.setMaxQueryTimeoutCount(Numbers.parseInt(resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim(), werServiceDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Max Query Timeout Count Parameter for Driver :"+driverName+" is not defined ,using default value:"+werServiceDriverConfigurationImpl.getMaxQueryTimeoutCount());
					}
					if(resultSet.getString("STATUSCHECKDURATION")!=null && resultSet.getString("STATUSCHECKDURATION").trim().length()>0)
						werServiceDriverConfigurationImpl.setStatusCheckDuration(Numbers.parseInt(resultSet.getString("STATUSCHECKDURATION").trim(), werServiceDriverConfigurationImpl.getStatusCheckDuration()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Status Check Duration Parameter for Driver :"+driverName+" is not defined ,using default value:"+werServiceDriverConfigurationImpl.getStatusCheckDuration());
					}
					if(wsAuthDriverId!=null){
						String keyAccountFieldMappingQuery = getQueryForKeyAccountFieldMapping();
						psKeyAccountFieldRel = connection.prepareStatement(keyAccountFieldMappingQuery);
						psKeyAccountFieldRel.setString(1,wsAuthDriverId);
						rsKeyAccountFieldRel =  psKeyAccountFieldRel.executeQuery();

						AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
						List<DataFieldMappingImpl> fieldMappingList = new ArrayList<DataFieldMappingImpl>();
						while(rsKeyAccountFieldRel.next()){
							String logicalName = rsKeyAccountFieldRel.getString("LOGICALNAME");
							String fieldName = rsKeyAccountFieldRel.getString("WEBMETHODKEY");
							String defaultValue = rsKeyAccountFieldRel.getString("DEFAULTVALUE");
							String valueMapping = rsKeyAccountFieldRel.getString("VALUEMAPPING");
							fieldMappingList.add(new DataFieldMappingImpl(fieldName, defaultValue, valueMapping, logicalName));
						}
						tempAccountDataFieldMapping.setFieldMappingList(fieldMappingList);
						werServiceDriverConfigurationImpl.setAccountDataFieldMapping(tempAccountDataFieldMapping);
						webServiceDriverConfigurationList.add(werServiceDriverConfigurationImpl);
						webServiceDriverConfigurationMap.put(driverInstanceId, werServiceDriverConfigurationImpl);
					}
				}
			}
			this.radWebServiceDriverList = webServiceDriverConfigurationList;
			this.radWebServiceDriverMap = webServiceDriverConfigurationMap;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(rsKeyAccountFieldRel);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(psKeyAccountFieldRel);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadWebServiceAuthDriverConfiguration() throws Exception{


		int size = this.radWebServiceDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadWebServiceAuthDriverConfigurationImpl radWebServiceAuthDriverConfigurationImpl = this.radWebServiceDriverList.get(i);
			queryBuilder.append("'" + radWebServiceAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + this.radWebServiceDriverList.get(size - 1).getDriverInstanceId() + "')");
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
				RadWebServiceAuthDriverConfigurationImpl werServiceDriverConfigurationImpl = (RadWebServiceAuthDriverConfigurationImpl) this.radWebServiceDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = werServiceDriverConfigurationImpl.getDriverInstanceId();

				String query = getQueryForReloadingWebServiceAuthDriver();
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
					throw new SQLException("Problem reading web service auth driver configuration, reason: prepared Statement is null");
				}
				preparedStatement.setString(1,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){

					String driverName = werServiceDriverConfigurationImpl.getDriverName();

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						werServiceDriverConfigurationImpl.setUserIdentities(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					if(resultSet.getString("IMSIATTRIBUTE")!=null && resultSet.getString("IMSIATTRIBUTE").trim().length()>0)
						werServiceDriverConfigurationImpl.setIMSIAttribute(resultSet.getString("IMSIATTRIBUTE"));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "IMSI Attribute Parameter for Driver :"+driverName+" is not defined");
					}
					if(resultSet.getString("MAXQUERYTIMEOUTCOUNT")!=null && resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim().length()>0)
						werServiceDriverConfigurationImpl.setMaxQueryTimeoutCount(Numbers.parseInt(resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim(), werServiceDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Max Query Timeout Count Parameter for Driver :"+driverName+" is not defined ,using default value:"+werServiceDriverConfigurationImpl.getMaxQueryTimeoutCount());
					}
					if(resultSet.getString("STATUSCHECKDURATION")!=null && resultSet.getString("STATUSCHECKDURATION").trim().length()>0)
						werServiceDriverConfigurationImpl.setStatusCheckDuration(Numbers.parseInt(resultSet.getString("STATUSCHECKDURATION").trim(), werServiceDriverConfigurationImpl.getStatusCheckDuration()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Status Check Duration Parameter for Driver :"+driverName+" is not defined ,using default value:"+werServiceDriverConfigurationImpl.getStatusCheckDuration());
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
	
	private String getQueryForWebServiceAuthDriver(){

		return "SELECT A.*,B.NAME from TBLMWSAUTHDRIVER A ,TBLMDRIVERINSTANCE B where A.DRIVERINSTANCEID=B.DRIVERINSTANCEID AND B.DRIVERINSTANCEID=?";
	}
	private String getQueryForReloadingWebServiceAuthDriver() {

		return "SELECT * from TBLMWSAUTHDRIVER WHERE DRIVERINSTANCEID=?";
	}

	private String getQueryForKeyAccountFieldMapping() throws SQLException{

		return "SELECT * FROM  TBLMWSAUTHDRIVER A , TBLMWEBMETHODKEYMAPREL B where A.WSAUTHDRIVERID=B.WSAUTHDRIVERID AND B.WSAUTHDRIVERID=?";
	}
	@PostRead
	public void postReadProcessing() {
		if(radWebServiceDriverList != null && radWebServiceDriverList.size() >0){
			int size  = this.radWebServiceDriverList.size();
			RadWebServiceAuthDriverConfigurationImpl radWebServiceAuthDriverConfigurationImpl;
			for(int i=0;i<size;i++){
				radWebServiceAuthDriverConfigurationImpl = radWebServiceDriverList.get(i);
				
				postProcessingForUserIdentityAttributes(radWebServiceAuthDriverConfigurationImpl);
				
				List<DataFieldMappingImpl> fieldMappingdList = radWebServiceAuthDriverConfigurationImpl.getAccountDataFieldMapping().getFieldMappingList();
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
				radWebServiceAuthDriverConfigurationImpl.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
			}
		}
	}
	private void postProcessingForUserIdentityAttributes(
			RadWebServiceAuthDriverConfigurationImpl radWebServiceAuthDriverConfigurationImpl) {

		String userIdentity =  radWebServiceAuthDriverConfigurationImpl.getUserIdentities();
		if(userIdentity!=null){
			String[] multipleUIds = userIdentity.split(",");
			List<String>useIdentity = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					useIdentity.add(userId);
			}
			radWebServiceAuthDriverConfigurationImpl.setUserIdentityAttribute(useIdentity);
		}
		
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(radWebServiceDriverList != null && radWebServiceDriverList.size() >0){
			int size  = this.radWebServiceDriverList.size();
			RadWebServiceAuthDriverConfigurationImpl radWebServiceAuthDriverConfigurationImpl;
			for(int i=0;i<size;i++){
				radWebServiceAuthDriverConfigurationImpl = radWebServiceDriverList.get(i);
				postProcessingForUserIdentityAttributes(radWebServiceAuthDriverConfigurationImpl);
			}
		}
	
	}
}
