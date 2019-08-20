package com.elitecore.netvertexsm.web.servermgr;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class BaseConfigurationAction extends BaseWebAction{


	protected EliteNetServerData getServiceLevelConfiguration(Long serviceId)throws DataManagerException{
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
		

		if (serviceId == null)
			throw new InvalidValueException("serviceId must be specifice. current value null|empty");

		INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(serviceId);
		INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());			
		INetServerTypeData netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());	
		EliteNetServerData eliteNetServerData = new EliteNetServerData();
		eliteNetServerData.setNetServerId(Long.toString(netServiceInstanceData.getNetServerId()));
		eliteNetServerData.setNetConfigurationList(new ArrayList());
		eliteNetServerData.setNetServiceList(new ArrayList());
		eliteNetServerData.setNetServerName(netServerTypeData.getAlias());
		eliteNetServerData.setPluginList(new ArrayList());
		eliteNetServerData.setNetServiceList(new ArrayList());
		List serverConfigurationList = netServerBLManager.getServerConfiguration(netServiceInstanceData.getNetServerId());
		for(int i=0;i<serverConfigurationList.size();i++){
			NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)serverConfigurationList.get(i);
			byte[] returnServerBytes = netServerBLManager.getServerConfigurationStream(netServiceInstanceData.getNetServerId(),netConfigInstanceData.getConfigId());
			EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
			eliteNetConfigData.setNetConfigurationData(returnServerBytes);
			eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
			eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
			eliteNetServerData.getNetConfigurationList().add(eliteNetConfigData);

		}


		EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
		INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
		Long serviceInstanceId = netServiceInstanceData.getNetServiceId();
		eliteNetServiceData.setNetServiceId(Long.toString(serviceInstanceId));
		eliteNetServiceData.setNetInstanceId(netServiceInstanceData.getInstanceId());
		eliteNetServiceData.setNetConfigurationList(new ArrayList());
		eliteNetServiceData.setNetDriverList(new ArrayList());
		eliteNetServiceData.setNetSubServiceList(new ArrayList());
		eliteNetServiceData.setNetServiceName(netServiceTypeData.getAlias());

		List lstServiceConfigInstance = netServiceBLManager.getServiceConfigurationInstance(serviceInstanceId);

		for ( int ja = 0; ja < lstServiceConfigInstance.size(); ja++ ) {
			NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData) lstServiceConfigInstance.get(ja);

			byte[] returnServiceBytes = netServiceBLManager.getServiceConfigurationStream(serviceInstanceId, netConfigInstanceData.getConfigId());
			EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
			eliteNetConfigData.setNetConfigurationData(returnServiceBytes);
			eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
			eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
			eliteNetServiceData.getNetConfigurationList().add(eliteNetConfigData);
		}

		eliteNetServerData.getNetServiceList().add(eliteNetServiceData);
		return eliteNetServerData;
	}
	protected EliteNetServerData getServerLevelConfiguration(Long serverId)throws DataManagerException{
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
//		PluginBLManager pluginBLManger = new PluginBLManager();
		EliteNetServerData eliteNetServerData = new EliteNetServerData();
		if(serverId != null ){
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);			
			INetServerTypeData netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());			
			List lstServerConfigInstance = netServerBLManager.getServerConfigurationInstance(serverId);


			eliteNetServerData.setNetServerId(Long.toString(serverId));
			eliteNetServerData.setNetConfigurationList(new ArrayList());
			eliteNetServerData.setNetServiceList(new ArrayList());
			eliteNetServerData.setNetServerName(netServerTypeData.getAlias());
			eliteNetServerData.setPluginList(new ArrayList());
			for(int i=0;i<lstServerConfigInstance.size();i++){
				NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstServerConfigInstance.get(i);
				byte[] returnServerBytes = netServerBLManager.getServerConfigurationStream(serverId,netConfigInstanceData.getConfigId());
				EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
				eliteNetConfigData.setNetConfigurationData(returnServerBytes);
				eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
				eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
				eliteNetServerData.getNetConfigurationList().add(eliteNetConfigData);

			}
/*			List lstPluginInstance = pluginBLManger.getPluginInstanceList(netServerInstanceData.getNetServerId());

			for(int k=0;k<lstPluginInstance.size();k++){				
				PluginInstanceData pluginInstanceData = (PluginInstanceData)lstPluginInstance.get(k);
				PluginTypeData pluginTypeData = pluginBLManger.getPluginType(pluginInstanceData.getPluginTypeId());					
				EliteNetPluginData eliteNetPluginData = new EliteNetPluginData(); 
				Long pluginInstanceId = pluginInstanceData.getPluginInstanceId();
				eliteNetPluginData.setNetConfigurationDataList(new ArrayList());

				eliteNetPluginData.setPluginName(pluginTypeData.getAlias());


				List lstPluginConfigInstance = pluginBLManger.getPluginConfigurationInstance(pluginInstanceId);
				for(int ka=0;ka<lstPluginConfigInstance.size();ka++){
					NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstPluginConfigInstance.get(ka);
					byte[] returnDriverBytes = pluginBLManger.getPluginConfigurationStream(pluginInstanceId,netConfigInstanceData.getConfigId());
					EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
					eliteNetConfigData.setNetConfigurationData(returnDriverBytes);
					eliteNetConfigData.setNetConfigurationId(pluginTypeData.getAlias());
					eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
					eliteNetPluginData.getNetConfigurationDataList().add(eliteNetConfigData);
				}
				eliteNetServerData.getPluginList().add(eliteNetPluginData);
			}

*/			List lstServicesInstance = netServiceBLManager.getNetServiceInstanceList(serverId);
			for(int j=0;j<lstServicesInstance.size();j++){
				NetServiceInstanceData netServiceInstanceData = (NetServiceInstanceData)lstServicesInstance.get(j);

				EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
				INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
				Long serviceInstanceId = netServiceInstanceData.getNetServiceId();
				eliteNetServiceData.setNetServiceId(Long.toString(serviceInstanceId));
				eliteNetServiceData.setNetInstanceId(netServiceInstanceData.getInstanceId());
				eliteNetServiceData.setNetConfigurationList(new ArrayList());
				eliteNetServiceData.setNetDriverList(new ArrayList());
				eliteNetServiceData.setNetServiceName(netServiceTypeData.getAlias());
				eliteNetServiceData.setNetSubServiceList(new ArrayList());

				List lstServiceConfigInstance = netServiceBLManager.getServiceConfigurationInstance(serviceInstanceId);

				for(int ja=0;ja<lstServiceConfigInstance.size();ja++){
					NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstServiceConfigInstance.get(ja);

					byte[] returnServiceBytes = netServiceBLManager.getServiceConfigurationStream(serviceInstanceId,netConfigInstanceData.getConfigId());
					EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
					eliteNetConfigData.setNetConfigurationData(returnServiceBytes);
					eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
					eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
					eliteNetServiceData.getNetConfigurationList().add(eliteNetConfigData);
				}

				
				eliteNetServerData.getNetServiceList().add(eliteNetServiceData);
			}

		}
		return eliteNetServerData;
	}
	

}
