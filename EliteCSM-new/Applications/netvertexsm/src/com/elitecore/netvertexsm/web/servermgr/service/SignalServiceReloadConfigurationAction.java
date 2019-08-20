package com.elitecore.netvertexsm.web.servermgr.service;

/*
 * Comment By Kaushik - Old JMX Code Replaced By new
 * import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
*/
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SignalServiceReloadConfigurationAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SERVICE RELOAD CONFIGURATION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		LogManager.getLogger().info(MODULE,"Enter execute method of "+getClass().getName());
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		ViewNetServerLiveDetailsForm viewNetServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
		IRemoteCommunicationManager remoteCommunicationManager = null;

		try {
			String host = "";
			int port = 0;
			String strNetServiceId = request.getParameter("netserviceid");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}
			INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);
			Long netServerId = netServiceInstanceData.getNetServerId();
			String netServiceName = netServiceInstanceData.getName();
			String netServiceInstance = netServiceInstanceData.getInstanceId();

			if(netServerId != null && netServiceName != null && netServiceInstance != null ){
				viewNetServerLiveDetailsForm.setNetServerId(netServerId);
				INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
				host = serverInstanceData.getAdminHost();
				port = serverInstanceData.getAdminPort();
				String[] argType = {"java.lang.String","java.lang.String"};
				Object[] argValue = {netServiceName,netServiceInstance};	
				/*
				 * Comment By Kaushik - Old JMX Code Replaced By new
				 * JMXServiceURL url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
				JMXConnector  jmxConnector = JMXConnectorFactory.connect(url, null);
				MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
				ObjectName objName =  new ObjectName("Elitecore:type=EliteAdmin");
				mbeanServerConnection.invoke(objName,"reloadServiceConfigurations",argValue,argType);
				jmxConnector.close();*/

				String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
				remoteCommunicationManager.init(host,port,netServerCode,true);
				remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","reloadServiceConfigurations",argValue,argType);

				request.setAttribute("responseUrl","/viewNetServiceLiveDetails.do?netserviceid="+netServiceId);
				ActionMessage message = new ActionMessage("servermgr.service.reload.configuration.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}else{
				LogManager.getLogger().trace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.service.reload.configuration.failure");
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
		}
		catch(CommunicationException e)
		{
			LogManager.getLogger().info(MODULE, "Returning error forward from " + getClass().getName());
			LogManager.getLogger().trace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.service.reload.configuration.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		catch (Exception e) {
			LogManager.getLogger().info(MODULE, "Returning error forward from " + getClass().getName());
			LogManager.getLogger().trace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.service.reload.configuration.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		finally {            
			try{
				if(remoteCommunicationManager != null)
					remoteCommunicationManager.close();  
			}
			catch (Throwable e) {
				remoteCommunicationManager = null;
			}
		}
	}
}

