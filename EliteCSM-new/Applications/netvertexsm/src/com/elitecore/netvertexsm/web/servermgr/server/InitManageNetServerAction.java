/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitManageNetServerAction.java                             
 * ModualName                                     
 * Created on Oct 18, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.web.servermgr.server;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class InitManageNetServerAction extends BaseWebAction {
    
    private static final String FAILURE_FORWARD = "failure";
    private static final String VIEW_FORWARD    = "viewManageNetServer";
    private static final String MODULE          = "INIT MANAGE NET SERVER INSTANCE ACTION";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try {
            Logger.logTrace(MODULE, "Enter the execute method of" + getClass().getName());
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            String strNetServerId = request.getParameter("netserverid");
            long netServerId=0L;
            if(strNetServerId!=null){
            	netServerId = Long.parseLong(strNetServerId.trim());
            }
            if (netServerId >0) {
                String host = "";
                int port = 0;
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                request.setAttribute("netServerTypeList",netServerTypeList);
                request.setAttribute("netServerInstanceData", netServerInstanceData);
                request.setAttribute("netServerId",netServerId);
                
                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                host = serverInstanceData.getAdminHost();
                port = serverInstanceData.getAdminPort();
                
               /* 
                * Comment By Kaushik - Old JMX Code Replaced By new
                * 
                JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
                JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null);
                MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
                ObjectName objName = new ObjectName("Elitecore:type=EliteAdmin");
                EliteNetServerDetails eliteServerDetails = (EliteNetServerDetails) mbeanServerConnection.invoke(objName, "readServerDetails", null, null);
                jmxConnector.close();*/
                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(host,port,netServerCode,true);
                EliteNetServerDetails eliteServerDetails = (EliteNetServerDetails)  remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readServerDetails",null,null);                
                request.setAttribute("eliteLiveServerDetails", eliteServerDetails);
                request.setAttribute("errorCode", "0");
                return mapping.findForward(VIEW_FORWARD);
            } else {
                request.setAttribute("errorCode","-1");
                Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logError(MODULE,"Netserver Id is Null");
                ActionMessage message = new ActionMessage("servermgr.server.manage.server.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
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
            return mapping.findForward(FAILURE_FORWARD);			
        }
        catch (CommunicationException sue) {
            request.setAttribute("errorCode","-1");
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,sue);
            EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
            request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
            return mapping.findForward(VIEW_FORWARD);
        }
        catch (Exception managerExp) {
            request.setAttribute("errorCode", "-1");
            Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.manage.server.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
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
    }
    
}
