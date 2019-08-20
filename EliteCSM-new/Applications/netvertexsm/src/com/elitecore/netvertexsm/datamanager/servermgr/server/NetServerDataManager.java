package com.elitecore.netvertexsm.datamanager.servermgr.server;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceConfMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerStartupConfigData;

public interface NetServerDataManager extends DataManager{
    /**
     * @author   dhavalraval
     * @param    netServerInstanceData
     * @return   INetServerInstanceData object
     * @throws   DataManagerException
     * @purpose  This method creates a new instance of NetServerInstanceData.
     */
    public INetServerInstanceData createServerInstance(INetServerInstanceData netServerInstanceData, String serverGroupId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netConfigurationInstanceData 
     * @return  INetConfigurationInstanceData object
     * @throws  DataManagerException
     * @purpose This method creates a new instance of NetConfigurationInstance.
     */
    public INetConfigurationInstanceData createNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerInstanceConfMap
     * @throws  DataManagerException
     * @purpose This method creates a new instance of NetServerInstanceConfMap.
     */
    public void createNetServerInstanceConfMap(INetServerInstanceConfMapData netServerInstanceConfMap) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   lstConfigParamValue
     * @throws  DataManagerException
     * @purpose This method creates a new instance of NetConfigurationValues.
     */
    public void createNetConfigurationValues(List<INetConfigurationValuesData> lstConfigParamValue) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method returns the list of NetServerType.s
     */
    public List getNetServerTypeList()throws DataManagerException;
    
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @param   netServerTypeId
     * @throws  DataManagerException
     * @purpose This method returns the list of NetServerConfigMap.
     */
    public List<INetServerConfigMapData> getNetServerConfigMapList(String netServerTypeId) throws DataManagerException;
    
    /**
     * @author kaushik vira
     * @param  netServerTypeId - Identify NetworkInfo Server Type
     * @param  targetedServerVersion - Target Server Version
     * @return List<INetServerConfigMapData> - all the ServerCofigMap Data for targetedServerVersion of  netServerTypeId.
     */
    
    public List<INetServerConfigMapData> getNetServerConfigMapList(String netServerTypeId,String netServerVersion) throws DataManagerException;
    
       
    /**
     * @author  dhavalraval
     * @param   ConfigId
     * @returns Returns NetConfigurationData class Object
     * @throws  DataManagerException
     * @purpose This method is generated to get root node
     */
    public INetConfigurationData getRootParameterConfigurationData(String configId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @throws  DataManagerException
     * @returns Returns List
     * @purpose This method is generated to get List of NetServerInstance
     */
    public List getNetServerInstanceList() throws DataManagerException;
    
    
    /**
     * @author  kaushik Vira
     * @param   netServerInstanceId - Identifies Server intance
     * @return  List<NetServiceInstanceData> - All the services for the specified server
     * @throws  DataManagerException
     * @purpose This method is generated to get the List of NetDriverInstance.
     */
    
    public List<INetServiceInstanceData> getNetserviceInstanceList(long netServerInstanceId) throws DataManagerException;
    
      
    
    /**
     * @author  dhavalraval
     * @author  updated by - kaushikvira
     * @param   netServerInstanceData
     * @throws  DataManagerException
     * @return  Returns INetServerInstanceData
     * @purpose This method is generated to give the list of NetServerInstance.
     */
    public INetServerInstanceData getNetServerInstance(long  netServetInstanceId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServiceInstanceData
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give the list of NetServiceInstance.
     */
    public INetServiceInstanceData getNetServiceInstanceList(INetServiceInstanceData netServiceInstanceData) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerConfigMapTypeData
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give the list of NetServerConfigMapType.
     */
    public INetServerConfigMapTypeData getNetServerConfigMapTypeList(INetServerConfigMapTypeData netServerConfigMapTypeData) throws DataManagerException;
    
     
    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @return  Returns List
     * @throws  HibernateException 
     * @purpose This method is generated to give list of NetServerInstanceConfMap.
     */
    public List<INetServerInstanceConfMapData> getNetServerInstanceConfMap(long netServerId) throws DataManagerException;
    
    /**
     * @author dhavalraval
     * @throws DataManagerException
     * @param  netServerId
     * @purose This method is generated to delete the netServerInsance by using netServerId.
     */
    public void deleteNetServerInstance(long netServerId) throws DataManagerException;
 
    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purpose Delete Configuration Instance.
     *         generic for All the level server,service,driver.   
     */
    public void deleteNetConfigurationInstance(long configInstanceId) throws DataManagerException;
    
    
    
    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purose  This method is generated to remove the netConfigurationValues.
     */
    public void deleteNetConfigurationValues(long configInstanceId) throws DataManagerException;
    
    
    
    /**
     * @author  Kaushik vira.
     * @param   netServerInstanceId - Identify Server Instance
     * @param   netConfigInstanceData - Identify Configuration Instance
     * @throws  DataManagerException
     * @purpose remove ServerInstancesConfigMap Entry of ServerIntance Specified  by INetConfigurationInstanceData.
     *     
     */        
    public void deleteNetServerInstanceConfMap(long configIntanceId) throws DataManagerException;
    
    
    /**
     * @author  dhavalraval 
     * @param   inetServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to update the details of NetServerInstanceData.
     */
    public void updateBasicDetail(INetServerInstanceData inetServerInstanceData,Timestamp statusChangeDate) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   inetServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to changeAdminInterface details of NetServerInstanceData.
     */
    public void changeAdminInterface(INetServerInstanceData inetServerInstanceData,Timestamp statusChangeDate) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method give list of NetServerInstanceConfMapData by using configInstanceId
     */
    
    public List getNetServerConfigInstanceList(long netServerId) throws DataManagerException;
    
    public List getNetServerConfigInstanceList(long netServerId,String netConfigMapTypeId) throws DataManagerException;
    
    public INetConfigurationInstanceData getConfigurationInstance(long configInstanceId) throws DataManagerException;
    
    public INetConfigurationInstanceData getServerConfigurationInstanceData(long serverId, String configId) throws DataManagerException;
  
    
    public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId)throws DataManagerException;
    
    public void saveNetConfValuesData(List<INetConfigurationValuesData> lstValueData, long configInstanceId)throws DataManagerException;
    
    public String getNetConfigurationName(long configInstanceId)throws DataManagerException ;
    
    public String getNetServerConfigMapByConfigId(String strConfigId) throws DataManagerException;
    
    public List getNetServerConfigMapByServerTypeId(String netServerTypeId,String ConfigVersion) throws DataManagerException;
    
    
    public String getNetServerNameServiceConfig(long configInstanceId) throws DataManagerException;
    
    public INetConfigurationData getNetConfigurationData(String configId) throws DataManagerException;
    
    
    /*
     * @author vaseem
     * @author updated By kaushikvira
     * @param netServerId - Identify Server Instance
     * @param NetServerDataManager - Session Object - This Method Can`t run with out parent Session.
     * @purpose Generate Server Identifier,It`s uses netserver Id as Server Identifier.
     */
    public void generateServerIdentificationInstance(long netServerId,String netServerIdentifier) throws DataManagerException;
    
    public String getNetServerNameServerConfig(long configInstanceId)throws DataManagerException;
    
    
    // Vaseem /////////////////////
    //public INetConfigurationInstanceData getDriverConfigurationInstanceData(String driverId, String configId) throws DataManagerException;
    public INetServerTypeData getNetServerType(String serverTypeId)throws DataManagerException;
  
    public INetServiceTypeData getNetServiceType(String serviceTypeId)throws DataManagerException;
    
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServerInstanceData serverInstanceData, String paramName) throws DataManagerException;
    
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServiceInstanceData serviceInstanceData, String paramName) throws DataManagerException;   
    
    public List getNetConfigParameterValueList(long configInstanceId, String parameterId)throws DataManagerException;
    
    public void addNetConfValuesData(List lstValueData) throws DataManagerException;
    
    public void deleteNetConfValuesData(List lstValueData) throws DataManagerException;   
    
    public List getNetConfigParamStartupModeList(String startUpModeType) throws DataManagerException;  
    // Vaseem End /////////////////////
    
    public INetServerStartupConfigData createServerStartupConfigInstance(INetServerStartupConfigData netServerStartupConfigData) throws DataManagerException;
    
    public void updateAdminDetails(INetServerInstanceData inetServerInstanceData,NetServerStartupConfigData iNetServerStartupConfigData,Timestamp statusChangeDate) throws DataManagerException;
    
    public void changeIsConfigInSyncStaus(long netServerId,String syncStatusType,Timestamp statusChangeDate) throws DataManagerException ;
    
    public INetServerVersionData getCompatibleVersion(long netServerIntanceId)throws DataManagerException;
    
    public INetServerVersionData getCompatibleVersion(String netServerTypeId,String netServerVersion)throws DataManagerException;
    
    public List getNewVersionServerConfigurationInstance(String compatibleVersion) throws DataManagerException;
    
    public boolean getServerConfigurationInstanceStatus(String compatibleVersion)throws DataManagerException;
    
    public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId,String configId) throws DataManagerException;
    
    /*
     * @author kaushikVira
     * @param netServerTypeId - Identify Server Type
     * @param serverVersionId - identify Server Version
     * @return configuration version for the ServerType
     */
    public String resolveConfigurationVersion( String netServerTypeId,String serverVersionId) throws DataManagerException ;
    
    /*
     * @author kashikvira
     * @param  alias - Configuration Alias.
     * @param  configVersion - identify Configuration Version.
     */
    public INetConfigurationData getCompatibleUpgradeVersionOfConfigrationData(String alias,String targetedServerVersion) throws DataManagerException;
    
    
    /*
     * @author kaushik vira
     * @param INetConfigurationInstanceData - Identity ConfigurationInstance
     * @param 
     * @return List<INetConfigurationParameterData> - return list of configuration Parameter.
     */
    public List<INetConfigurationValuesData> getConfigurationParameterValueDataList(INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException;
    
    /*
     * @author kaushik vira
     * @param configId - Identity Configuration
     * @return Map<String,String> - return parameter map<parameterId,ParentParameterid> for specified configId.
     */
    public Map<String, INetConfigurationParameterData> getConfigurationParameterMap( String configId ) throws DataManagerException ;
    
    public void upadateNetServerConfigurationInstance(INetConfigurationInstanceData netConfigurationInstanceData) throws DataManagerException ;
    
    public INetServerInstanceData updateServerVersion(long netServerInstanceId,String ServerVersion,Timestamp statusChangeDate) throws DataManagerException;
    
    public List getNetServerInstanceListByTypeId(String netServerTypeId) throws DataManagerException;
    
    public INetServerInstanceData getNetServerInstanceServerConfig(long configInstanceId) throws DataManagerException ;
    
    public List<INetConfigurationParameterData> getNetConfigurationParameterDataList(INetServerInstanceData serverInstanceData, String paramName) throws DataManagerException;
    	
//    public String getNetServerNamePluginConfig(Long configInstanceId)throws DataManagerException ;
    
    public List<INetConfigurationInstanceData> getNetServerConfiguration(Long netServerId) throws DataManagerException;
    
    public void deleteServerInsatnceGroupRelations(long netServerId) throws DataManagerException;
}

