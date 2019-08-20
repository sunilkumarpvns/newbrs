package com.elitecore.elitesm.web.servermgr.server;

//import java.util.List;

/*

 * Comment By Kaushik - Old JMX Code Replaced By new
import javax.management.MBeanServerConnection;
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

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SignalServiceStartUpAction  extends BaseDictionaryAction{
//	private static final String VIEW_FORWARD = "viewNetServerDetail";
	private static final String SUCCESS_FORWARD = "success";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SERVICE START UP";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		ViewNetServerLiveDetailsForm netServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
		IRemoteCommunicationManager remoteCommunicationManager = null;
		try {
			String host = "";
			int port = 0;
			String strNetServerId = request.getParameter("netServerId");
			
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
			String netServiceName = request.getParameter("netServiceName");			
			String netServiceInstance = request.getParameter("instanceId");			
			if(netServerId != null && netServiceName != null && netServiceInstance != null){
				netServerLiveDetailsForm.setNetServerId(netServerId);
				INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
				host = serverInstanceData.getAdminHost();
				port = serverInstanceData.getAdminPort();
				String[] argType = {"java.lang.String","java.lang.String"};
				Object[] argValue = {netServiceName,netServiceInstance};				
//				ViewNetServerLiveDetailsForm viewNetServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
				/*
                                Comment By Kaushik - Old JMX Code Replaced By new 
                                JMXServiceURL url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
				JMXConnector  jmxConnector = JMXConnectorFactory.connect(url, null);
	                        MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
	                        ObjectName objName =  new ObjectName("Elitecore:type=EliteAdmin");
	                        mbeanServerConnection.invoke(objName,"startService",argValue,argType);
				jmxConnector.close();*/

				String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
				remoteCommunicationManager.init(host,port,netServerCode,true);
				remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","startService",argValue,argType);

				request.setAttribute("responseUrl","/viewNetServerLiveDetails.do?netServerId="+netServerId);
				ActionMessage message = new ActionMessage("servermgr.service.start.request.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}else{
				Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.service.start.request.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		} catch (UnidentifiedServerInstanceException commExp) {
			commExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
		} catch (CommunicationException e) {
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);

		} catch (Exception e) {
			Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.service.start.request.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}
		finally{
			try{
				if(remoteCommunicationManager != null)
					remoteCommunicationManager.close();  
			}
			catch (Throwable e) {
				remoteCommunicationManager = null;
			}
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
