package com.elitecore.netvertexsm.web.servermgr.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.core.util.mbean.data.config.EliteNetServiceData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.passwordutil.PasswordEncryption;

public class SynchronizeNetServiceConfigAction extends BaseWebAction {

    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "NETSERVICE SYNCHRONIZE";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SYNCHRONIZE_SERVICE_CONFIGURATION_ACTION;


    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Enter the execute method of :" + getClass().getName());
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try {
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            String strNetServiceId = request.getParameter("netServiceId");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}
            
            if (strNetServiceId == null)
                throw new InvalidValueException("serviceId must be specifice. current value null|empty");

            INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);

            EliteNetServiceData eliteNetServiceData = new EliteNetServiceData();
            INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
            Long serviceInstanceId = netServiceInstanceData.getNetServiceId();
            eliteNetServiceData.setNetServiceId(serviceInstanceId.toString());
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

            
            INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
            String ipAddress = serverInstanceData.getAdminHost();
            int port = serverInstanceData.getAdminPort();

            String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
            remoteCommunicationManager.init(ipAddress, port, netServerCode, true);
            String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "VersionInformation");

            if (strVersion.equalsIgnoreCase(serverInstanceData.getVersion())) {

                Object[] objArgValues = { eliteNetServiceData, serverInstanceData.getVersion() };
                String[] strArgTypes = { "com.elitecore.core.util.mbean.data.config.EliteNetServiceData", "java.lang.String" };

                String strMessage = (String) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "updateServiceConfiguration", objArgValues, strArgTypes);

                netServiceBLManager.markServiceisInSync(serviceInstanceId);

                Logger.logDebug(MODULE, "Synchronize Msg :" + strMessage);
                request.setAttribute("responseUrl", "/updateNetServiceSynchronizeConfigDetail.do?netserviceid=" + serviceInstanceId);
                ActionMessage message = new ActionMessage("servermgr.update.service.configuration.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS_FORWARD);
            } else {
                Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
                ActionMessage message1 = new ActionMessage("servermgr.server.version.mismatch");
                ActionMessages messages = new ActionMessages();
                messages.add("fatal", message);
                messages.add("fatal", message1);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (InvalidValueException e) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("fatal", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (UnidentifiedServerInstanceException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
            Logger.logTrace(MODULE, commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information", messageReason);
            saveErrors(request, messages);
        }
        catch (CommunicationException cce) {
            Logger.logError(MODULE, "Error during Synchronize operation,reason :" + cce.getMessage());
            Logger.logTrace(MODULE, cce);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.synchronized.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn", message);
            messages.add("warn", message1);
            saveErrors(request, messages);
        }
        catch (ClassCastException cce) {
            Logger.logError(MODULE, "Error during Synchronize operation,reason :" + cce.getMessage());
            Logger.logTrace(MODULE, cce);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessage message1 = new ActionMessage("servermgr.service.synchronized.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn", message);
            messages.add("warn", message1);
            saveErrors(request, messages);
        }
        catch (Exception managerExp) {
            Logger.logError(MODULE, "Error during Synchronize operation,reason :" + managerExp.getMessage());
            Logger.logTrace(MODULE, managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.service.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("fatal", message);
            saveErrors(request, messages);

        }
        finally {
            try {
                if (remoteCommunicationManager != null)
                    remoteCommunicationManager.close();
            }
            catch (Throwable e) {
                remoteCommunicationManager = null;
            }
        }
        return mapping.findForward(FAILURE_FORWARD);
    }
 
}
