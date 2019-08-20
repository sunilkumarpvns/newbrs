package com.elitecore.netvertexsm.web.servermgr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.data.live.EliteNetServiceDetails;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.service.form.ViewNetServiceLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewNetServiceLiveDetailsAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewNetServiceDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW NET SERVICE INSTANCE ACTION ";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		LogManager.getLogger().trace(MODULE,"Enter execute method of "+getClass().getName());
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
		INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
		ViewNetServiceLiveDetailsForm viewNetServiceLiveDetailsForm = (ViewNetServiceLiveDetailsForm)form;
		IRemoteCommunicationManager remoteCommunicationManager = null;

		try {
			String host = "";
			int port = 0;
			String strNetServiceId = request.getParameter("netserviceid");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}
			if(netServiceId != null){
				netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
				request.setAttribute("netServiceInstanceData",netServiceInstanceData);	

				Long netServerId = netServerInstanceData.getNetServerId();

				INetServiceTypeData netServiceType = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
				INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);

				List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
				List netServerTypeList = netServerBLManager.getNetServerTypeList();
				List netServerInstanceList = netServerBLManager.getNetServerInstanceList();

				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerInstanceList",netServerInstanceList);			
				request.setAttribute("netServerTypeList",netServerTypeList);
				request.setAttribute("netServiceTypeList",netServiceTypeList);
				System.out.println("Server Instance Id : "+netServerInstanceData.getNetServerId());
				String[] argName = {"java.lang.String","java.lang.String"};
				Object[] argValue = {netServiceType.getAlias(),netServiceInstanceData.getInstanceId()};				

				host = serverInstanceData.getAdminHost();
				port = serverInstanceData.getAdminPort();

				/*
				 * Comment By Kaushik - Old JMX Code Replaced By new
				 * JMXServiceURL url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
				JMXConnector  jmxConnector = JMXConnectorFactory.connect(url, null);
                                MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
	                        ObjectName objName =  new ObjectName("Elitecore:type=EliteAdmin");
                                EliteNetServiceDetails eliteServiceDetails = (EliteNetServiceDetails)mbeanServerConnection.invoke(objName,"readServiceDetails",argValue,argName);
				jmxConnector.close();*/

				String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
				remoteCommunicationManager.init(host,port,netServerCode,true);
				EliteNetServiceDetails eliteServiceDetails = (EliteNetServiceDetails) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readServiceDetails",argValue,argName);
				request.setAttribute("eliteLiveServiceDetails",eliteServiceDetails);
				viewNetServiceLiveDetailsForm.setErrorCode("0");
				return mapping.findForward(VIEW_FORWARD);
			}else{
				LogManager.getLogger().error(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.service.livedetails");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		} catch (UnidentifiedServerInstanceException commExp) {
			commExp.printStackTrace();
			LogManager.getLogger().error(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);			
		} catch(CommunicationException sue){
			LogManager.getLogger().error(MODULE,"Returning error forward from "+ getClass().getName());
			LogManager.getLogger().trace(MODULE,sue);
			viewNetServiceLiveDetailsForm.setErrorCode("-1");
			EliteNetServiceDetails eliteServiceDetails = new EliteNetServiceDetails();
			request.setAttribute("eliteLiveServiceDetails",eliteServiceDetails);
			return mapping.findForward(VIEW_FORWARD);			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Returning error forward from "+ getClass().getName());
			LogManager.getLogger().trace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.service.livedetails");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

}
