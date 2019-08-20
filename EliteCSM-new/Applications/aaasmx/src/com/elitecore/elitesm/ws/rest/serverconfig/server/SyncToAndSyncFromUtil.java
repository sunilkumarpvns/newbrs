package com.elitecore.elitesm.ws.rest.serverconfig.server;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetPluginData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class SyncToAndSyncFromUtil {

	public static String MODULE ="ServerSynchronizeToController";
	public static final String ELITERMSERVER = "Elite_RM_Server";
	public static final String ELITECSMSERVER = "Elite_CSM_Server";

	public static String synchronizFromData(String serverInstanceName, String serverType) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException, ConnectException{

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		IRemoteCommunicationManager remoteCommunicationManager = null;

		if(serverType.equalsIgnoreCase(ELITECSMSERVER)){
			if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
				return "You are not allow to Synchronize From operation, Reason : Entered server name's type is Resource Manager";
			}
		}else if(serverType.equalsIgnoreCase(ELITERMSERVER)){
			if(netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false){
				return "You are not allow to Synchronize From operation, Reason : Entered server name's type is CSM Server";
			}
		}

		String netServerId = netServerInstanceData.getNetServerId();
		String ipAddress = netServerInstanceData.getAdminHost();
		int port = netServerInstanceData.getAdminPort();

		String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);

		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
		remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
		String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
		if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
			Object[] objArgValues = {};
			String[] strArgTypes = {};

			EliteNetServerData eliteNetServerData = (EliteNetServerData)  remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"readServerConfiguration",objArgValues,strArgTypes);
			netServerBLManager.updateServerDetails(netServerId,eliteNetServerData,staffData.getStaffId(),BaseConstant.SHOW_STATUS_ID);
		}

		try {
			if (remoteCommunicationManager != null)
				remoteCommunicationManager.close();
		} catch (Throwable e) {
			remoteCommunicationManager = null;
		}
		return null;
	}

	public static String synchronizToData(String serverInstanceName, String serverType) throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException, ConnectException {

		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(serverInstanceName.trim());
		IRemoteCommunicationManager remoteCommunicationManager = null;

		if(serverType.equalsIgnoreCase(ELITECSMSERVER)){
			if(netServerBLManager.isEliteCSMServer(netServerInstanceData.getNetServerTypeId()) == false){
				return "You are not allow to  Synchronize To operation, Reason : Entered server name's type is Resource Manager";
			}
		}else if(serverType.equalsIgnoreCase(ELITERMSERVER)){
			if(netServerBLManager.isRMManager(netServerInstanceData.getNetServerTypeId()) == false){
				return "You are not allow to  Synchronize To operation, Reason : Entered server name's type is CSM Server";
			}
		}

		String netServerId = netServerInstanceData.getNetServerId();
		if(netServerId != null ){
			EliteNetServerData eliteNetServerData = getServerLevelConfiguration(netServerId);

			String ipAddress = netServerInstanceData.getAdminHost();
			int port = netServerInstanceData.getAdminPort();	        

			String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);

			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
			remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
			String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");


			if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){

				printServices(eliteNetServerData);

				Object[] objArgValues = {eliteNetServerData,netServerInstanceData.getVersion()};
				String[] strArgTypes = {"com.elitecore.core.util.mbean.data.config.EliteNetServerData","java.lang.String"};

				remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"updateServerConfiguration",objArgValues,strArgTypes);

				/* Mark Server status as sync */
				netServerBLManager.markServerisInSync(netServerInstanceData.getNetServerId());
			}
		}

		try {
			if (remoteCommunicationManager != null)
				remoteCommunicationManager.close();
		} catch (Throwable e) {
			remoteCommunicationManager = null;
		}
		return null;
	}

	protected static EliteNetServerData getServerLevelConfiguration(String serverId)throws DataManagerException{
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
		EliteNetServerData eliteNetServerData = new EliteNetServerData();
		if(serverId != null ){
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);			
			INetServerTypeData netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());			
			List lstServerConfigInstance = netServerBLManager.getServerConfigurationInstance(serverId);


			eliteNetServerData.setNetServerId(serverId);
			eliteNetServerData.setNetConfigurationList(new ArrayList());
			eliteNetServerData.setNetServiceList(new ArrayList());
			eliteNetServerData.setNetServerName(netServerTypeData.getAlias());
			eliteNetServerData.setPluginList(new ArrayList());
			int sizeOne = lstServerConfigInstance.size();
			for(int i=0;i<sizeOne;i++){
				NetConfigurationInstanceData netConfigInstanceData = (NetConfigurationInstanceData)lstServerConfigInstance.get(i);
				byte[] returnServerBytes = netServerBLManager.getServerConfigurationStream(serverId,netConfigInstanceData.getConfigId());
				EliteNetConfigurationData eliteNetConfigData = new EliteNetConfigurationData();
				eliteNetConfigData.setNetConfigurationData(returnServerBytes);
				eliteNetConfigData.setNetConfigurationId(netConfigInstanceData.getConfigId());
				eliteNetConfigData.setNetConfigurationKey(netConfigInstanceData.getNetConfiguration().getAlias());
				eliteNetServerData.getNetConfigurationList().add(eliteNetConfigData);

			}

			List lstServicesInstance = netServiceBLManager.getNetServiceInstanceList(serverId);
			int sizeTwo = lstServicesInstance.size();
			for(int j=0;j<sizeTwo;j++){
				NetServiceInstanceData netServiceInstanceData = (NetServiceInstanceData)lstServicesInstance.get(j);

				EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
				INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
				String serviceInstanceId = netServiceInstanceData.getNetServiceId();
				eliteNetServiceData.setNetServiceId(serviceInstanceId);
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
	private static void printServices(EliteNetServerData serverData) {
		Logger.logInfo(MODULE," Sending Server Object... ");
		Logger.logInfo(MODULE," Server Name :  "+ serverData.getNetServerName());
		Logger.logInfo(MODULE," Version Name :  "+ serverData.getVersion());

		List serviceList = serverData.getNetServiceList();
		printConfiguration(serverData.getNetConfigurationList());
		List pluginList = serverData.getPluginList();

		if(pluginList!=null){
			for (Iterator iterator = pluginList.iterator(); iterator.hasNext();) {
				EliteNetPluginData eliteNetPluginData = (EliteNetPluginData) iterator.next();
				Logger.logInfo(MODULE," Plugin Name :  "+eliteNetPluginData.getPluginName());
				printConfiguration(eliteNetPluginData.getNetConfigurationDataList());
			}
		}
		if(serviceList!=null){
			for (Iterator iterator = serviceList.iterator(); iterator.hasNext();) {
				EliteNetServiceData eliteNetServiceData = (EliteNetServiceData) iterator.next();
				Logger.logInfo(MODULE," Service Name :  "+ eliteNetServiceData.getNetServiceName());
				printConfiguration(eliteNetServiceData.getNetConfigurationList());

			}
		}
	}

	private static void printConfiguration(List configurationList){
		if(configurationList!=null){
			for (Iterator iterator = configurationList.iterator(); iterator.hasNext();) {
				EliteNetConfigurationData configData = (EliteNetConfigurationData) iterator.next();
				Logger.logInfo(MODULE, "\t Configuration Key --> " + configData.getNetConfigurationKey());
			}
		}
	}
}
