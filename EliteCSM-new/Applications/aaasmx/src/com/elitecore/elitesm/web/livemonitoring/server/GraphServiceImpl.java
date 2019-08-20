package com.elitecore.elitesm.web.livemonitoring.server;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.openmbean.CompositeData;


import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.livemonitoring.client.GraphService;
import com.elitecore.elitesm.web.livemonitoring.client.ServerInstanceBean;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GraphServiceImpl extends RemoteServiceServlet implements GraphService{
	private static final String MODULE = "GRAPH SERVICE";
	
	private static final long serialVersionUID = 1L;
	

	public ServerInstanceBean getServerInstance(String serverId) throws IllegalArgumentException {
		ServerInstanceBean serverInstanceBean = null;
		Logger.logInfo(MODULE, "Fetching server instance...");
		try{
			serverInstanceBean = new ServerInstanceBean();
			NetServerBLManager serverBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = serverBLManager.getNetServerInstance(serverId);
			serverInstanceBean.setNetServerId(netServerInstanceData.getNetServerId());
			serverInstanceBean.setServerCode(netServerInstanceData.getNetServerCode());
			serverInstanceBean.setJmxPort(netServerInstanceData.getAdminPort());
			serverInstanceBean.setServerIP(netServerInstanceData.getAdminHost());
			serverInstanceBean.setServerName(netServerInstanceData.getName());
			INetServerTypeData netServerTypeData = serverBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
			serverInstanceBean.setServerType(netServerTypeData.getName());
		}catch(Exception e){
			Logger.logTrace(MODULE,e);
		}
		return serverInstanceBean;
	}

	public List<Long[]> getGraphData(ServerInstanceBean serverInstanceBean, String graph) {
		IRemoteCommunicationManager remoteCommunicationManager = null;
		List<Long[]> data=null;
		try{
			String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceBean.getServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
			remoteCommunicationManager.init(serverInstanceBean.getServerIP(),serverInstanceBean.getJmxPort(),netServerCode,true);
			
			if( "MEMORYUSAGE".equals(graph) || "THREADSTATISTICS".equals(graph) ){

				if("MEMORYUSAGE".equals(graph)){

					CompositeData compositeData = (CompositeData)remoteCommunicationManager.getAttribute("java.lang:type=Memory","HeapMemoryUsage");
					Object obj = compositeData.get("used");
					data = new ArrayList<Long[]>();
					Long[] longData = new Long[2];
					longData[0] = new Date().getTime();
					Long usedMemory=(Long)obj;
					usedMemory/=1024;
					longData[1] = usedMemory;
					data.add(longData);

				}else if("THREADSTATISTICS".equals(graph)){

					Integer activeThreadCount = (Integer)remoteCommunicationManager.getAttribute("java.lang:type=Threading","ThreadCount");
					Integer peakThreadCount = (Integer)remoteCommunicationManager.getAttribute("java.lang:type=Threading","PeakThreadCount");

					data = new ArrayList<Long[]>();
					Long[] longData = new Long[3];
					longData[0] = new Date().getTime();
					longData[1] = activeThreadCount.longValue();
					longData[2] =peakThreadCount.longValue();
					data.add(longData);


				}


			}else{
				
				
				Object params[] = {graph,this.getThreadLocalRequest().getSession().getId()};
				String signs[] = {"java.lang.String","java.lang.String"};
				data = (List<Long[]>)remoteCommunicationManager.execute(MBeanConstants.ONLINE_REPORT,"retrieveLastUpdatedData",params,signs);
			}
			return data;
			
		}catch(CommunicationException e){
			Logger.logError(MODULE, "Connection error while calling retrieveLastUpdatedData() : [" + serverInstanceBean.getServerName()+"] "+ e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while calling retrieveLastUpdatedData() :   [" + serverInstanceBean.getServerName()+"] "+ e.getMessage());

		}finally{
			try{
				if(remoteCommunicationManager != null)
					remoteCommunicationManager.close();  
			}
			catch (Throwable e) {
				remoteCommunicationManager = null;
			}
		}
		return null;
	}
}
