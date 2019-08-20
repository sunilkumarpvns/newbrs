package com.elitecore.aaa.core.conf.impl;

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
import com.elitecore.aaa.core.conf.MAPGWAuthDriverConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.DriverSelectQueryBuilder;
import com.elitecore.aaa.radius.drivers.conf.impl.RadMAPGWAuthDriverConfigurationImpl;
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
import com.elitecore.core.commons.util.ConfigurationUtil;

@XmlType(propOrder = {})
@XmlRootElement(name = "mapgw-auth-drivers")
@ConfigurationProperties(moduleName ="MAPGW-AUTH-DRIVER-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "mapgw-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadMAPGWAuthDriverConfigurable extends Configurable implements DriverConfigurable {
	
	private static final String MODULE = "MAPGW-AUTH-DRIVER-CONFIGURABLE";
	private static String DEFAULT_VALUE = "DEFAULTVALUE";
	private static String VALUE_MAPPING = "VALUEMAPPING";
	private static final String FIELDMAP_TABLE = "tblmgatewayfieldmap";
	private static final String REMOTEHOST_CONST ="REMOTE_HOST ";
	private static final String LOCALHOST_CONST = "LOCAL_HOST ";
	
	private List<RadMAPGWAuthDriverConfigurationImpl> mapgwAuthDriverConfigList;
	@XmlTransient
	private Map<String, MAPGWAuthDriverConfiguration> mapgwAuthDriverConfigMap;
	

	public RadMAPGWAuthDriverConfigurable(){
		mapgwAuthDriverConfigList = new  ArrayList<RadMAPGWAuthDriverConfigurationImpl>();
		this.mapgwAuthDriverConfigMap = new LinkedHashMap<String, MAPGWAuthDriverConfiguration>();
	}
	
	@Override
	@XmlElement(name = "mapgw-auth-driver")
	public List<RadMAPGWAuthDriverConfigurationImpl> getDriverConfigurationList() {
		return mapgwAuthDriverConfigList;
	}
	public void setMapgwAuthDriverConfigList(
			List<RadMAPGWAuthDriverConfigurationImpl> mapgwAuthDriverConfigList) {
		this.mapgwAuthDriverConfigList = mapgwAuthDriverConfigList;
	}
	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;				
		PreparedStatement psForFieldMapping = null;
		ResultSet rsForFieldMapping = null;
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();

			String baseQuery = new DriverSelectQueryBuilder(DriverTypes.RAD_MAPGW_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();	

			List<RadMAPGWAuthDriverConfigurationImpl> mpgwDriverConfigurationList = new ArrayList<RadMAPGWAuthDriverConfigurationImpl>();
			Map<String, MAPGWAuthDriverConfiguration> mapGWConfMap = new LinkedHashMap<String, MAPGWAuthDriverConfiguration>();
			
			psForDriverInstanceId = connection.prepareStatement(baseQuery);		
			if(psForDriverInstanceId ==  null){
				throw new SQLException("Problem in reading MAP GW auth driver configuration, reason: prepared Statement is null");
 
			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {					
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				/* reading driver configuration details complete.*/

				query = getQueryForMapGWAuthDriver();			
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem in reading MAP GW auth driver configuration, reason: prepared statement received is NULL");
				}

				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);

				preparedStatement.setQueryTimeout(100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					
					RadMAPGWAuthDriverConfigurationImpl radMAPGWAuthDriverConfig = new RadMAPGWAuthDriverConfigurationImpl();
					radMAPGWAuthDriverConfig.setDriverInstanceId(driverInstanceId);

					radMAPGWAuthDriverConfig.setMapGWAuthID(resultSet.getString("mapgwauthid"));
					String driverName = resultSet.getString("DRIVER_NAME");
					if (driverName != null && driverName.trim().length() > 0){
						radMAPGWAuthDriverConfig.setDriverName(driverName);
					} 

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						radMAPGWAuthDriverConfig.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					if (resultSet.getString("LOCALHOSTID") != null && resultSet.getString("LOCALHOSTID").trim().length() > 0){
						radMAPGWAuthDriverConfig.setLocalHost(resultSet.getString("LOCALHOSTID"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Local Host Id is not defined in map gateway driver for "+ driverName );
					} 
					if (resultSet.getString("LOCALHOSTPORT") != null && resultSet.getString("LOCALHOSTPORT").trim().length() > 0){
						radMAPGWAuthDriverConfig.setLocalHostPort(resultSet.getString("LOCALHOSTPORT"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Local Host port is not defined in map gateway driver for "+ driverName);
					} 
					if (resultSet.getString("LOCALHOSTIP") != null && resultSet.getString("LOCALHOSTIP").trim().length() > 0){
						radMAPGWAuthDriverConfig.setLocalHostIP(resultSet.getString("LOCALHOSTIP"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Local Host Ip is not defined in map gateway driver for "+ driverName);
					} 
					if (resultSet.getString("REMOTEHOSTID") != null && resultSet.getString("REMOTEHOSTID").trim().length() > 0){
						radMAPGWAuthDriverConfig.setRemoteHost(resultSet.getString("REMOTEHOSTID"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Remote Host Id is not defined in map gateway driver for "+ driverName);
					} 
					if (resultSet.getString("REMOTEHOSTPORT") != null && resultSet.getString("REMOTEHOSTPORT").trim().length() > 0){
						radMAPGWAuthDriverConfig.setRemoteHostPort(resultSet.getString("REMOTEHOSTPORT"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Remote Host Port is not defined in map gateway driver for "+ driverName);
					} 
					if (resultSet.getString("REMOTEHOSTIP") != null && resultSet.getString("REMOTEHOSTIP").trim().length() > 0){
						radMAPGWAuthDriverConfig.setRemoteHostIP(resultSet.getString("REMOTEHOSTIP"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Remote Host Ip is not defined in map gateway driver for "+ driverName);
					} 
					try{
						if (resultSet.getString("MAPGWCONNPOOLSIZE") != null &&resultSet.getString("MAPGWCONNPOOLSIZE").trim().length() > 0){
							try {
								int mapGWConnPoolSize = Integer.parseInt(resultSet.getString("MAPGWCONNPOOLSIZE"));
								radMAPGWAuthDriverConfig.setNoOfMAPGWConnections(mapGWConnPoolSize);
							} catch (NumberFormatException e){

							}
						} else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Map Gateway Connection pool size configuration not found for MAP Gateway Driver: " + driverName + " Default value: " + radMAPGWAuthDriverConfig.getNoOfMAPGWConnections() + " will be used.");
						}
					} catch (SQLException e){
					}
					try {
						if (resultSet.getString("REQUESTTIMEOUT") != null &&resultSet.getString("REQUESTTIMEOUT").trim().length() > 0){
							try{
								long requestTimeout = Long.parseLong(resultSet.getString("REQUESTTIMEOUT"));
								if (requestTimeout < 100){
									requestTimeout = 1000;
									radMAPGWAuthDriverConfig.setRequestTimeout(requestTimeout);
								}
								radMAPGWAuthDriverConfig.setRequestTimeout(requestTimeout);

							} catch (NumberFormatException e){
							}
						} else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Request Timeout configuration not found for MAP Gateway Driver: " + driverName + " Default value: " + radMAPGWAuthDriverConfig.getRequestTimeout() + " will be used.");
						}
					} catch (SQLException e){
					}
					try {
						if (resultSet.getString("MAXQUERYTIMEOUTCOUNT") != null &&resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim().length() > 0){
							try{
								int maxQueryTimeoutCount = Numbers.parseInt(resultSet.getString("MAXQUERYTIMEOUTCOUNT"),100);
								if (maxQueryTimeoutCount < 1){
									maxQueryTimeoutCount = 100;
									radMAPGWAuthDriverConfig.setMaxQueryTimeoutCount(maxQueryTimeoutCount);
								}
								radMAPGWAuthDriverConfig.setMaxQueryTimeoutCount(maxQueryTimeoutCount);
							} catch (NumberFormatException e){
							}
						}else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Max Request Timeout configuration not found for MAP Gateway Driver: " + driverName + " Default value: " + radMAPGWAuthDriverConfig.getMaxQueryTimeoutCount() + " will be used.");
						} 
					} catch (Exception e){
					}
					try {
						if(resultSet.getString("STATUSCHECKDURATION")!=null && resultSet.getString("STATUSCHECKDURATION").trim().length()>0)
							radMAPGWAuthDriverConfig.setStatusCheckDuration(Numbers.parseInt(resultSet.getString("STATUSCHECKDURATION"),radMAPGWAuthDriverConfig.getStatusCheckDuration()));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Status Check Duration Parameter for Driver :"+driverName+" is not defined ,using default value");
						}
					} catch (Exception e){
					}

					if (resultSet.getString("SENDAUTHINFO") != null && resultSet.getString("SENDAUTHINFO").trim().length() > 0){
						radMAPGWAuthDriverConfig.setIsSAIEnabled(ConfigurationUtil.stringToBoolean(resultSet.getString("SENDAUTHINFO"), radMAPGWAuthDriverConfig.getIsSAIEnabled()));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Send Authentication info configuration for Driver :"+driverName+" is not defined ,Default is: " + radMAPGWAuthDriverConfig.getIsSAIEnabled());
					} 

					if (resultSet.getString("NUMBEROFTRIPLETS") != null && resultSet.getString("NUMBEROFTRIPLETS").trim().length() > 0){
						try {
							radMAPGWAuthDriverConfig.setNumberOfSIMTriplets(Integer.parseInt(resultSet.getString("NUMBEROFTRIPLETS")));
						} catch (NumberFormatException e){
						}
					}
					psForFieldMapping = connection.prepareStatement(getQueryForFieldMapping());
					if(psForFieldMapping == null){
							throw new SQLException("Problem reading rad db auth driver configuration, reason: prepared statement received is NULL");
					}
					psForFieldMapping.setString(1, radMAPGWAuthDriverConfig.getMapGWAuthID());
					psForFieldMapping.setQueryTimeout(100);
					rsForFieldMapping = psForFieldMapping.executeQuery();

					AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
					List<DataFieldMappingImpl> fieldMappingList = new ArrayList<DataFieldMappingImpl>();
					while (rsForFieldMapping.next()){
						String defaultValue = rsForFieldMapping.getString(DEFAULT_VALUE);
						String valueMapping = rsForFieldMapping.getString(VALUE_MAPPING);
						DataFieldMappingImpl dataFieldMappingImpl = new DataFieldMappingImpl(rsForFieldMapping.getString("profilefield"), defaultValue, valueMapping, rsForFieldMapping.getString("logicalname"));
						fieldMappingList.add(dataFieldMappingImpl);
					}
					tempAccountDataFieldMapping.setFieldMappingList(fieldMappingList);
					radMAPGWAuthDriverConfig.setAccountDataFieldMapping(tempAccountDataFieldMapping);
					mpgwDriverConfigurationList.add(radMAPGWAuthDriverConfig);
					mapGWConfMap.put(driverInstanceId, radMAPGWAuthDriverConfig);
				}

			}
			this.mapgwAuthDriverConfigList = mpgwDriverConfigurationList;
			this.mapgwAuthDriverConfigMap = mapGWConfMap;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(rsForFieldMapping);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(psForFieldMapping);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@DBReload
	public void reloadMAPGWDriverConfiguration() throws Exception{

		int size = this.mapgwAuthDriverConfigList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadMAPGWAuthDriverConfigurationImpl radMAPGWAuthDriverConfigurationImpl = mapgwAuthDriverConfigList.get(i);
			queryBuilder.append("'" + radMAPGWAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + mapgwAuthDriverConfigList.get(size - 1).getDriverInstanceId() + "')");
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
				RadMAPGWAuthDriverConfigurationImpl radMAPGWAuthDriverConfig = (RadMAPGWAuthDriverConfigurationImpl) this.mapgwAuthDriverConfigMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));
				String driverInstanceId = radMAPGWAuthDriverConfig.getDriverInstanceId();

				String query = getQueryForReloadingMapGWAuthDriver();			
				preparedStatement = conn.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem in reading MAP GW auth driver configuration, reason: prepared statement received is NULL");
				}

				preparedStatement.setString(1,driverInstanceId);

				preparedStatement.setQueryTimeout(100);			
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					String driverName = radMAPGWAuthDriverConfig.getDriverName();

					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						radMAPGWAuthDriverConfig.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 

					try {
						if (resultSet.getString("REQUESTTIMEOUT") != null &&resultSet.getString("REQUESTTIMEOUT").trim().length() > 0){
							try{
								long requestTimeout = Long.parseLong(resultSet.getString("REQUESTTIMEOUT"));
								if (requestTimeout < 100){
									requestTimeout = 1000;
									radMAPGWAuthDriverConfig.setRequestTimeout(requestTimeout);
								}
								radMAPGWAuthDriverConfig.setRequestTimeout(requestTimeout);

							} catch (NumberFormatException e){
							}
						} else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Request Timeout configuration not found for MAP Gateway Driver: " + driverName + " Default value: " + radMAPGWAuthDriverConfig.getRequestTimeout() + " will be used.");
						}
					} catch (SQLException e){
					}
					try {
						if (resultSet.getString("MAXQUERYTIMEOUTCOUNT") != null &&resultSet.getString("MAXQUERYTIMEOUTCOUNT").trim().length() > 0){
							try{
								int maxQueryTimeoutCount = Numbers.parseInt(resultSet.getString("MAXQUERYTIMEOUTCOUNT"),100);
								if (maxQueryTimeoutCount < 1){
									maxQueryTimeoutCount = 100;
									radMAPGWAuthDriverConfig.setMaxQueryTimeoutCount(maxQueryTimeoutCount);
								}
								radMAPGWAuthDriverConfig.setMaxQueryTimeoutCount(maxQueryTimeoutCount);
							} catch (NumberFormatException e){
							}
						}else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Max Request Timeout configuration not found for MAP Gateway Driver: " + driverName + " Default value: " + radMAPGWAuthDriverConfig.getMaxQueryTimeoutCount() + " will be used.");
						} 
					} catch (Exception e){
					}
					try {
						if(resultSet.getString("STATUSCHECKDURATION")!=null && resultSet.getString("STATUSCHECKDURATION").trim().length()>0)
							radMAPGWAuthDriverConfig.setStatusCheckDuration(Numbers.parseInt(resultSet.getString("STATUSCHECKDURATION"),radMAPGWAuthDriverConfig.getStatusCheckDuration()));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Status Check Duration Parameter for Driver :"+driverName+" is not defined ,using default value");
						}
					} catch (Exception e){
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
	
	private String getQueryForFieldMapping(){
		return "SELECT * FROM " + FIELDMAP_TABLE +" WHERE mapgwauthid=?";
	}

	private String getQueryForMapGWAuthDriver(){
		return "SELECT A.*, B.NAME AS DRIVER_NAME FROM TBLMMAPGWAUTHDRIVER A, TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID = ? AND B.DRIVERINSTANCEID=? ";
	}
	private String getQueryForReloadingMapGWAuthDriver(){
		return "SELECT * FROM TBLMMAPGWAUTHDRIVER WHERE DRIVERINSTANCEID=? ";
	}

	@PostRead
	public void postReadProcessing() {
		if(mapgwAuthDriverConfigList != null && mapgwAuthDriverConfigList.size() >0){
			int size  = this.mapgwAuthDriverConfigList.size();
			RadMAPGWAuthDriverConfigurationImpl mapgwAuthDriverConfigurationImpl ;
			for(int i=0;i<size;i++){
				mapgwAuthDriverConfigurationImpl = this.mapgwAuthDriverConfigList.get(i);
				
				postProcessingForUserIdentityAttributes(mapgwAuthDriverConfigurationImpl);
				
				List<DataFieldMappingImpl> fieldMappingdList = mapgwAuthDriverConfigurationImpl.getAccountDataFieldMapping().getFieldMappingList();
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
				mapgwAuthDriverConfigurationImpl.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
				
				String remoteHostId = mapgwAuthDriverConfigurationImpl.getRemoteHost();
				String localHostId= mapgwAuthDriverConfigurationImpl.getLocalHost();
				remoteHostId = REMOTEHOST_CONST + remoteHostId + ":" + mapgwAuthDriverConfigurationImpl.getRemoteHostPort() + " [" + mapgwAuthDriverConfigurationImpl.getRemoteHostIP() + "]";
				localHostId = LOCALHOST_CONST + localHostId + ":" + mapgwAuthDriverConfigurationImpl.getLocalHostPort() + " [" + mapgwAuthDriverConfigurationImpl.getLocalHostIP() + "]";
			
				mapgwAuthDriverConfigurationImpl.setRemoteHost(remoteHostId);
				mapgwAuthDriverConfigurationImpl.setLocalHost(localHostId);
				
				if(mapgwAuthDriverConfigurationImpl.getNoOfMAPGWConnections()<1){
					int mapGWConnPoolSize = 10;
					mapgwAuthDriverConfigurationImpl.setNoOfMAPGWConnections(mapGWConnPoolSize);
				}
			
			}
		}
		
	}
	private void postProcessingForUserIdentityAttributes(RadMAPGWAuthDriverConfigurationImpl mapgwAuthDriverConfigurationImpl) {
		String strUserIdentity = mapgwAuthDriverConfigurationImpl.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttribute = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttribute.add(userId);
			}
			mapgwAuthDriverConfigurationImpl.setUserIdentityAttributes(userIdentityAttribute);
		}
		
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(mapgwAuthDriverConfigList != null && mapgwAuthDriverConfigList.size() >0){
			int size  = this.mapgwAuthDriverConfigList.size();
			RadMAPGWAuthDriverConfigurationImpl mapgwAuthDriverConfigurationImpl ;
			for(int i=0;i<size;i++){
				mapgwAuthDriverConfigurationImpl = this.mapgwAuthDriverConfigList.get(i);
				postProcessingForUserIdentityAttributes(mapgwAuthDriverConfigurationImpl);
			}
		}
		
	}
}
