package com.elitecore.netvertexsm.web.servermgr.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.passwordutil.PasswordEncryption;

public class SynchronizeBackNetServiceConfigAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "NETSERVICE SYNCHRONIZE BACK";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SYNCHRONIZE_BACK_SERVICE_CONFIGURATION_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());

        IRemoteCommunicationManager remoteCommunicationManager = null;
        try{
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            String strNetServiceId = request.getParameter("netServiceId");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}
            

            if(netServiceId != null ){
                INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);			
                INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());					

                Long serviceInstanceId = netServiceInstanceData.getNetServiceId();
                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
                String ipAddress = serverInstanceData.getAdminHost();
                int port = serverInstanceData.getAdminPort();	        


                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);		        
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
                String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");


                if(strVersion.equalsIgnoreCase(serverInstanceData.getVersion())){
                    Object[] objArgValues = {netServiceTypeData.getAlias(), netServiceInstanceData.getInstanceId(),serverInstanceData.getVersion()};
                    String[] strArgTypes = {"java.lang.String","java.lang.String","java.lang.String"};

                    EliteNetServiceData eliteNetServiceData = (EliteNetServiceData)  remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readServiceConfiguration",objArgValues,strArgTypes);
                    netServiceBLManager.updateServiceDetails(netServiceId,eliteNetServiceData,getLoggedInUserId(request),BaseConstant.SHOW_STATUS_ID);

                    request.setAttribute("responseUrl","/updateNetServiceSynchronizeConfigDetail.do?netserviceid="+serviceInstanceId);
                    ActionMessage message1 = new ActionMessage("servermgr.update.service.configuration.success.1");
                    ActionMessage message2 = new ActionMessage("servermgr.update.service.configuration.success.2");

                    ActionMessages messages = new ActionMessages();
                    messages.add("information",message1);
                    messages.add("information",message2);					
                    saveMessages(request,messages);
                    return mapping.findForward(SUCCESS_FORWARD);
                }else{
                    Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
                    ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
                    ActionMessage message1 = new ActionMessage("servermgr.server.version.mismatch");					
                    ActionMessages messages = new ActionMessages();
                    messages.add("fatal",message);
                    messages.add("fatal",message1);					
                    saveErrors(request,messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("fatal",message);
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
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
        }
        catch(CommunicationException e){
            Logger.logError(MODULE,"Error during Synchronize operation,reason :"+e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.synchronized.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);                  
            saveErrors(request,messages);

        }catch(ClassCastException cce){
            Logger.logError(MODULE,"Error during Synchronize operation,reason :"+cce.getMessage());
            Logger.logTrace(MODULE,cce);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.synchronized.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("fatal",message);
            saveErrors(request,messages);
        }
        finally
        {
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

