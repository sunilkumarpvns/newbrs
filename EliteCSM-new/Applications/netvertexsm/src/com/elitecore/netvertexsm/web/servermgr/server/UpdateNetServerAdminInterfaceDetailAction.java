package com.elitecore.netvertexsm.web.servermgr.server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerStartupConfigData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetServerAdminInterfaceDetailForm;

public class UpdateNetServerAdminInterfaceDetailAction extends BaseWebAction {

    private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
    private static final String UPDATE_FORWARD  = "updateNetServerAdminInterfaceDetail";
    private static final String VIEW_FORWARD    = "viewNetServerInstance";
    private static final String MODULE          = "CHANCE ADMIN INTERFACE";
    private static final String SUB_MODULE_ACTIONALIAS          = ConfigConstant.UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION;


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of : " + getClass().getName());

        NetServerBLManager netServerBLManager = new NetServerBLManager();
        String strNetServerId = request.getParameter("netserverid");
        Long netServerId=null;
        List netServerTypeList = null;

        try {
			
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
        	
            UpdateNetServerAdminInterfaceDetailForm updateNetServerAdminInterfaceDetailForm = (UpdateNetServerAdminInterfaceDetailForm) form;
            netServerTypeList = netServerBLManager.getNetServerTypeList();
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);

            if (updateNetServerAdminInterfaceDetailForm.getAction() == null) {

                if (netServerId != null) {
                    INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                    netServerInstanceData.setNetServerId(netServerId);
                    netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                    updateNetServerAdminInterfaceDetailForm.setNetServerId(netServerId);
                    convertBeanToForm(netServerInstanceData, updateNetServerAdminInterfaceDetailForm);
                    request.setAttribute("netServerInstanceData", netServerInstanceData);
                    request.setAttribute("updateNetServerAdminInterfaceDetailForm", updateNetServerAdminInterfaceDetailForm);
                    request.setAttribute("netServerTypeList", netServerTypeList);
                }

                return mapping.findForward(UPDATE_FORWARD);
            } else if (updateNetServerAdminInterfaceDetailForm.getAction() != null && updateNetServerAdminInterfaceDetailForm.getAction().equalsIgnoreCase("update")) {

                INetServerInstanceData inetServerInstanceData = convertFormToAdminInterfaceBean(updateNetServerAdminInterfaceDetailForm);
                netServerBLManager.updateAdminInterface(inetServerInstanceData);

                if (updateNetServerAdminInterfaceDetailForm.getChkUpdateStarupConf() != null && updateNetServerAdminInterfaceDetailForm.getChkUpdateStarupConf().equalsIgnoreCase("1")) {
                    Logger.logDebug(MODULE,"AdminInterface Updated by netServerBLManager.updateAdminDetails(inetServerInstanceData,iNetServerStartupConfigData) ");
                    NetServerStartupConfigData iNetServerStartupConfigData = convertFormToServerStartupBean(updateNetServerAdminInterfaceDetailForm,request);
                    netServerBLManager.updateAdminDetails(inetServerInstanceData,iNetServerStartupConfigData);
                } else {
                    Logger.logDebug(MODULE,"AdminInterface Updated by  netServerBLManager.updateAdminInterface(inetServerInstanceData) ");
                    netServerBLManager.updateAdminInterface(inetServerInstanceData);
                }

                request.setAttribute("netServerTypeList", netServerTypeList);
                request.setAttribute("responseUrl","/viewNetServerInstance.do?netserverid="+inetServerInstanceData.getNetServerId());
                ActionMessage message = new ActionMessage("servermgr.server.updateadmininterface.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }else{
                throw new DataManagerException("No action Specified");
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (DataValidationException exp) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
        }
        catch (Exception exp) {
            Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
        }
        ActionMessage message = new ActionMessage("servermgr.update.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information", message);
        saveErrors(request, messages);
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        message = new ActionMessage("servermgr.error.heading","updating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);
        return mapping.findForward(FAILURE_FORWARD);
    }

    private void convertBeanToForm( INetServerInstanceData netServerInstanceData , UpdateNetServerAdminInterfaceDetailForm updateNetServerAdminInterfaceDetailForm ) {

        if (netServerInstanceData != null) {
            updateNetServerAdminInterfaceDetailForm.setNetServerId(netServerInstanceData.getNetServerId());
            updateNetServerAdminInterfaceDetailForm.setAdminInterfaceIP(netServerInstanceData.getAdminHost());
            updateNetServerAdminInterfaceDetailForm.setAdminInterfacePort(netServerInstanceData.getAdminPort());
            updateNetServerAdminInterfaceDetailForm.setChkUpdateStarupConf("1");

            if (netServerInstanceData.getStartupConfig() != null) {
                updateNetServerAdminInterfaceDetailForm.setShellPrompt(netServerInstanceData.getStartupConfig().getShellPrompt());
                updateNetServerAdminInterfaceDetailForm.setCommunicationPort(netServerInstanceData.getStartupConfig().getCommunicationPort());
                updateNetServerAdminInterfaceDetailForm.setFailureMsg(netServerInstanceData.getStartupConfig().getFailureMsg());
                updateNetServerAdminInterfaceDetailForm.setLoginPrompt(netServerInstanceData.getStartupConfig().getLoginPrompt());
                updateNetServerAdminInterfaceDetailForm.setOperationTimeOut(netServerInstanceData.getStartupConfig().getOperationTimeOut());
                updateNetServerAdminInterfaceDetailForm.setShell(netServerInstanceData.getStartupConfig().getShell());
                updateNetServerAdminInterfaceDetailForm.setPassword(netServerInstanceData.getStartupConfig().getPassword());
                updateNetServerAdminInterfaceDetailForm.setUserName(netServerInstanceData.getStartupConfig().getUserName());
                updateNetServerAdminInterfaceDetailForm.setPasswordPrompt(netServerInstanceData.getStartupConfig().getPasswordPrompt());
                updateNetServerAdminInterfaceDetailForm.setProtocol(netServerInstanceData.getStartupConfig().getProtocol());

            } else {
                updateNetServerAdminInterfaceDetailForm.setCommunicationPort(23);
                updateNetServerAdminInterfaceDetailForm.setShell("bash");
                updateNetServerAdminInterfaceDetailForm.setShellPrompt("$");
                updateNetServerAdminInterfaceDetailForm.setFailureMsg("ogin failure");
                updateNetServerAdminInterfaceDetailForm.setLoginPrompt("ogin");
                updateNetServerAdminInterfaceDetailForm.setOperationTimeOut(5000);
                updateNetServerAdminInterfaceDetailForm.setPasswordPrompt("assword");
            }
        }

    }

    private INetServerInstanceData convertFormToAdminInterfaceBean( UpdateNetServerAdminInterfaceDetailForm updateNetServerAdminInterfaceDetailForm ) {
        INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
        if (updateNetServerAdminInterfaceDetailForm != null) {
            netServerInstanceData = new NetServerInstanceData();
            netServerInstanceData.setNetServerId(updateNetServerAdminInterfaceDetailForm.getNetServerId());
            netServerInstanceData.setAdminHost(updateNetServerAdminInterfaceDetailForm.getAdminInterfaceIP());
            netServerInstanceData.setAdminPort(updateNetServerAdminInterfaceDetailForm.getAdminInterfacePort());
            updateNetServerAdminInterfaceDetailForm.getChkUpdateStarupConf();
        }
        return netServerInstanceData;
    }

    private NetServerStartupConfigData convertFormToServerStartupBean(UpdateNetServerAdminInterfaceDetailForm updateNetServerAdminInterfaceDetailForm,HttpServletRequest request ) {
        NetServerStartupConfigData icreateNetServerStartupConfigData = new NetServerStartupConfigData();
        if (updateNetServerAdminInterfaceDetailForm != null) {
            Date currentDate = new Date();
            icreateNetServerStartupConfigData.setNetServerId(updateNetServerAdminInterfaceDetailForm.getNetServerId());
            icreateNetServerStartupConfigData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
            icreateNetServerStartupConfigData.setCommunicationPort(updateNetServerAdminInterfaceDetailForm.getCommunicationPort());
            icreateNetServerStartupConfigData.setFailureMsg(updateNetServerAdminInterfaceDetailForm.getFailureMsg());
            icreateNetServerStartupConfigData.setLastModifiedByStaffId(Long.parseLong(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId()));
            icreateNetServerStartupConfigData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
            icreateNetServerStartupConfigData.setLoginPrompt(updateNetServerAdminInterfaceDetailForm.getLoginPrompt());
            icreateNetServerStartupConfigData.setOperationTimeOut(updateNetServerAdminInterfaceDetailForm.getOperationTimeOut());
            icreateNetServerStartupConfigData.setPassword(updateNetServerAdminInterfaceDetailForm.getPassword());
            icreateNetServerStartupConfigData.setPasswordPrompt(updateNetServerAdminInterfaceDetailForm.getPasswordPrompt());
            icreateNetServerStartupConfigData.setProtocol(updateNetServerAdminInterfaceDetailForm.getProtocol());
            icreateNetServerStartupConfigData.setShell(updateNetServerAdminInterfaceDetailForm.getShell());
            icreateNetServerStartupConfigData.setShellPrompt(updateNetServerAdminInterfaceDetailForm.getShellPrompt());
            icreateNetServerStartupConfigData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
            icreateNetServerStartupConfigData.setUserName(updateNetServerAdminInterfaceDetailForm.getUserName());
        }
        return icreateNetServerStartupConfigData;
    }

}