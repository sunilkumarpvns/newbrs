package com.elitecore.elitesm.datamanager.servermgr.service;
import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceConfigMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceConfMapData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;

public interface NetServiceDataManager extends DataManager{
    
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
     * @param   lstConfigParamValue
     * @throws  DataManagerException
     * @purpose This method creates a new instance of NetConfigurationValues.
     */
    public void createNetConfigurationValues(List lstConfigParamValue) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method returns the list of NetServiceType.
     */
    public List getNetServiceTypeList() throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerTypeId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method returns the list of NetServiceType
     */
    public List getNetServiceTypeList(String netServerTypeId) throws DataManagerException;
    
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
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to get the List of NetServiceInstance
     */
    public List getNetServiceInstanceList() throws DataManagerException;
    
    
    /**
     * @author  dhavalraval
     * @param   netServerConfigMapTypeData
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give the list of NetServerConfigMapType.
     */
    public INetServiceConfigMapTypeData getNetServiceConfigMapTypeList(INetServiceConfigMapTypeData netServiceConfigMapTypeData) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to get the List of NetServiceInstance.
     */
    public List getNetserviceInstanceList(String netServerId) throws DataManagerException;
    
 
    /**
     * @author  dhavalraval
     * @param   netServiceInstanceData
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give the list of NetServiceInstance.
     */
    public INetServiceInstanceData getNetServiceInstance(INetServiceInstanceData netServiceInstanceData) throws DataManagerException;
     
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method is generated to give list of NetServiceInstanceConfMap.
     */
    public List<INetServiceInstanceConfMapData> getNetServiceInstanceConfMap(String netServiceId) throws DataManagerException;
    
      
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @throws  DataManagerException
     * @purpose This method is generated to delete the netServiceInstance by using netServiceId.
     */
    public void deleteNetServiceInstance(String netServiceId) throws DataManagerException;
    
      /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purpose This method is generated to remove the netConfigurationInstance.
     */
    public void deleteNetConfigurationInstance(String configInstanceId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   configInstanceId
     * @throws  DataManagerException
     * @purose  This method is generated to remove the netConfigurationValues.
     */
    public void deleteNetConfigurationValues(String configInstanceId) throws DataManagerException;
    
    
  
    public void deleteNetServiceInstanceConfMap(String configIntanceId) throws DataManagerException;
         
    
      
    /**
     * @author  dhavalraval 
     * @param   inetServerInstanceData
     * @param   statusChangeDate
     * @throws  DataManagerException
     * @purpose This method is generated to update the details of NetServerInstanceData.
     */
    public void updateBasicDetail(INetServiceInstanceData inetServiceInstanceData,Timestamp statusChangeDate) throws DataManagerException;
    
    
    /**
     * @author   dhavalraval
     * @param    netServiceInstanceData
     * @return   INetServiceInstanceData object
     * @throws   DataManagerException
     * @purpose  This method creates a new instance of NetServiceInstanceData.
     */
    public INetServiceInstanceData createServiceInstance(INetServiceInstanceData netServiceInstanceData) throws DataManagerException;
    
    
    /**
     * @author  dhavalraval
     * @return  Returns List
     * @param   netServiceTypeId
     * @param   netServerId
     * @throws  DataManagerException
     * @purpose This method returns the list of NetServiceConfigMap.
     */
    public List<INetServiceConfigMapData> getNetServiceConfigMapList(String netServiceTypeId,String netServerId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServerInstanceConfMap
     * @throws  DataManagerException
     * @purpose This method creates a new instance of NetServerInstanceConfMap.
     */
    public void createNetServiceInstanceConfMap(INetServiceInstanceConfMapData netServiceInstanceConfMap) throws DataManagerException;
    
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method give list of NetServiceInstanceConfMapData by using configInstanceId
     */
    public List getNetServiceConfigInstanceList(String netServiceId) throws DataManagerException;
    
    /**
     * @author  dhavalraval
     * @param   netServiceId
     * @return  Returns List
     * @throws  DataManagerException
     * @purpose This method give list of NetServiceInstanceConfMapData by using configInstanceId
     */
    public List getNetServiceConfigInstanceList(String netServiceId,String netConfigMapTypeId) throws DataManagerException;
    
    
    public INetConfigurationInstanceData getConfigurationInstance(String configInstanceId) throws DataManagerException;
    
    public INetConfigurationInstanceData getServerConfigurationInstanceData(String serverId, String configId) throws DataManagerException;
    
    public INetConfigurationInstanceData getServiceConfigurationInstanceData(String serviceId, String configId) throws DataManagerException;    
    
    public INetConfigurationParameterData getNetConfigurationParameterData(String parameterId,String configId)throws DataManagerException;
    
    public void saveNetConfValuesData(List lstValueData, String configInstanceId)throws DataManagerException;
    
    public String getNetConfigurationName(String configInstanceId)throws DataManagerException ;
    
    public String getNetServiceNameServiceConfig(String configInstanceId)throws DataManagerException ;
    
    
    public INetConfigurationData getNetConfigurationData(String configId) throws DataManagerException;
    
    // Vaseem /////////////////////
    public INetServiceTypeData getNetServiceType(String serviceTypeId)throws DataManagerException;
    public String getMaxServiceInstance(String netServiceTypeId, long netServerId)throws DataManagerException;   
    public void updateServiceInstanceId(long netServiceId, String instanceId)throws DataManagerException;   
    
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServerInstanceData serverInstanceData, String paramName) throws DataManagerException;
    public INetConfigurationParameterData getNetConfigurationParameterData(INetServiceInstanceData serviceInstanceData, String paramName) throws DataManagerException;
    public List getNetConfigParameterValueList(String configInstanceId, String parameterId)throws DataManagerException;
    public void addNetConfValuesData(List lstValueData) throws DataManagerException;
    public void deleteNetConfValuesData(List lstValueData) throws DataManagerException;   
    
    public INetServiceInstanceData getNetServiceInstanceServiceConfig(String configInstanceId)throws DataManagerException;
    public void removeNetConfValuesData(List lstValueData) throws DataManagerException;
    
    public INetServiceInstanceData getNetServiceInstance(String netServerId, String netServiceName, String instanceId)throws DataManagerException;
    public INetServiceTypeData getNetServiceType(String serverTypeId, String netServiceName)throws DataManagerException;   
    // Vaseem End /////////////////////
    
    public void changeIsConfigInSyncStaus(String netServiceId,String syncStatusType,Timestamp statusChangeDate)throws DataManagerException;
    
    public List<INetServiceConfigMapData> getNetServiceConfigMapList( String netServiceTypeId,String netServerType,String ServerVersion) throws DataManagerException ;
   
    public INetServiceInstanceData getNetServiceInstanceData(long netServiceId) throws DataManagerException;
	public List<INetConfigurationParameterData> getNetConfigParameterDataList(INetServiceInstanceData netServiceInstanceData, String alias)throws DataManagerException;

	public String getNetServiceTypeIdByName(String serviceName) throws DataManagerException;

	public NetServiceInstanceData getServiceDetailByServiceNameAndServerInstanceId(String serviceName, String serverInstanceId) throws DataManagerException;

	public String getConfigIdByServiceTypeId(String serviceTypeId) throws DataManagerException;
}

