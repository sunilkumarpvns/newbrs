package com.elitecore.netvertexsm.web.servermgr.server;


import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetPluginData;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.servermgr.BaseConfigurationAction;
import com.elitecore.passwordutil.PasswordEncryption;

public class SynchronizeNetServerConfigAction extends BaseConfigurationAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "NETSERVR SYNCHRONIZE";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SYNCHRONIZE_NET_SERVER_CONFIGURATION_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
        MessageResources messageResources = getResources(request,"resultMessageResources");
        IRemoteCommunicationManager remoteCommunicationManager = null;        
        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();

            String strNetServerId = request.getParameter("netserverid");
            Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            if(netServerId != null ){
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);			
                EliteNetServerData eliteNetServerData = getServerLevelConfiguration(netServerId);

                String ipAddress = netServerInstanceData.getAdminHost();
                int port = netServerInstanceData.getAdminPort();	        


                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                Logger.logDebug(MODULE, "ENCRYPTED SERVER CODE:"+netServerCode);
                
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
                String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
                
                
                if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){

                	printServices(eliteNetServerData);
                	
                	
                    Object[] objArgValues = {eliteNetServerData,netServerInstanceData.getVersion()};
                    String[] strArgTypes = {"com.elitecore.core.util.mbean.data.config.EliteNetServerData","java.lang.String"};
                    
                    
                    //String strMessage = (String) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"updateServerConfiguration",objArgValues,strArgTypes);
                    remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"updateServerConfiguration",objArgValues,strArgTypes);
                    
                    /* Mark Server status as sync */
                    netServerBLManager.markServerisInSync(netServerInstanceData.getNetServerId());

                    
                    request.setAttribute("responseUrl","/updateNetServerSynchronizeConfigDetail.do?netserverid="+netServerId);
                    ActionMessage message = new ActionMessage("servermgr.update.server.configuration.success");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    saveMessages(request,messages);
                    return mapping.findForward(SUCCESS_FORWARD);
                }else{
                    Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                    ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));					
                    ActionMessage message1 = new ActionMessage("servermgr.server.version.mismatch");
                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message);
                    messages.add("information",message1);
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }else{
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));				
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (UnidentifiedServerInstanceException commExp) {
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
        }catch(CommunicationException c){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+c.getMessage());
            Logger.logTrace(MODULE,c);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(c);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);		
            saveErrors(request,messages);		

        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));			
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
    
  
    private void printServices(EliteNetServerData serverData)
    {
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
    private void printConfiguration(List configurationList){
    	if(configurationList!=null){
    		for (Iterator iterator = configurationList.iterator(); iterator.hasNext();) {
    			EliteNetConfigurationData configData = (EliteNetConfigurationData) iterator.next();
    			Logger.logInfo(MODULE, "\t Configuration Key --> " + configData.getNetConfigurationKey());
    		}
    	}
    }
}

