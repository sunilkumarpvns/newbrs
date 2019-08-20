package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.exception.AddNewServerConfigOpFailedException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerConfigurationForm;

public class ListNetServerConfigurationAction extends BaseDictionaryAction {

    private static final String SUCCESS_FORWARD = "listNetServerConfiguration";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "LIST NET SERVER CONFIGURATION";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_NET_SERVER_CONFIGURATION_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse reponse ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        String strNetServerId = request.getParameter("netserverid");

        try {
			String netServerId= null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            
            ListNetServerConfigurationForm listNetServerConfigurationForm = (ListNetServerConfigurationForm) form;
            INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
            netServerInstanceData.setNetServerId(netServerId);
            netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());

            Logger.logInfo(MODULE, "--START-- Checking New Configuration(ServerType) is/are added for "+netServerId);
            netServerBLManager.addNewServerConfiguration(netServerId);
            Logger.logInfo(MODULE, "--END-- Checking New Configuration(ServerType) is/are added for "+netServerId);

            List netServerconfigInstanceMapList = netServerBLManager.getNetServerConfigInstanceList(netServerId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            listNetServerConfigurationForm.setConfigInstanceList(netServerconfigInstanceMapList);
            listNetServerConfigurationForm.setNetServerId(netServerId);
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);

            doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
            Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());
            return mapping.findForward(SUCCESS_FORWARD);
        } 
        catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        } catch (AddNewServerConfigOpFailedException hExp) {
            Logger.logError(MODULE, "Error during Data Manager Operation, reason :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.addnewconfig.failed","Server");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            messages.add("information1", messageReason);
            saveErrors(request, messages);
        }
        catch (DataManagerException hExp) {
            Logger.logError(MODULE, "Error during Data Manager Operation, reason :" + hExp.getMessage());
            Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Error when listing the server level configuration, reason :" + e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.update.server.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
        }
        return mapping.findForward(FAILURE_FORWARD);
    }
}
