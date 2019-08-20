/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UpdateNetServerStartupConfigAction.java                 		
 * ModualName server    			      		
 * Created on 19 October, 2007
 * Last Modified on                                     
 * @author :  kaushik vira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStartupConfigData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerStartupConfigForm;

public class UpdateNetServerStartupConfigAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "CREATE NET SERVER STARTUP IN";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        UpdateNetServerStartupConfigForm updateNetServerStartupConfigForm =null;
        try {
            Logger.logTrace(MODULE, "Enter the execute method of ;" + getClass().getName());
            
            updateNetServerStartupConfigForm = (UpdateNetServerStartupConfigForm) form;
            INetServerStartupConfigData inetServerStartupConfigData = new NetServerStartupConfigData();
            formToBean(updateNetServerStartupConfigForm,inetServerStartupConfigData,request);
            new NetServerBLManager().createNetServerStartupConfig(inetServerStartupConfigData);
            request.setAttribute("responseUrl", "/initUpdateNetserverStartupConfig.do?netserverid="+updateNetServerStartupConfigForm.getNetServerId());
            ActionMessage message = new ActionMessage("servermgr.server.createServerStarupConfig.success");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveMessages(request, messages);
            return mapping.findForward(SUCCESS_FORWARD);
        }
        catch (Exception managerExp) {
            Logger.logError(MODULE, "Error during data Manager operation,reason : " +getClass().getName()+ " " + managerExp.getMessage());
            Logger.logTrace(MODULE,EliteExceptionUtils.getRootCauseStackTraceAsString(managerExp));
            request.setAttribute("responseUrl", "/viewNetServerInstance?netServerId="+updateNetServerStartupConfigForm.getNetServerId());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.createServerStarupConfig.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
      
        
    }
    
    public void formToBean(UpdateNetServerStartupConfigForm updateNetServerStartupConfigForm ,
                           INetServerStartupConfigData icreateNetServerStartupConfigForm,HttpServletRequest request) {
        
        Date currentDate = new Date();
        icreateNetServerStartupConfigForm.setNetServerId(updateNetServerStartupConfigForm.getNetServerId());
        icreateNetServerStartupConfigForm.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
        icreateNetServerStartupConfigForm.setCommunicationPort(updateNetServerStartupConfigForm.getCommunicationPort());
        icreateNetServerStartupConfigForm.setFailureMsg(updateNetServerStartupConfigForm.getFailureMsg());
        icreateNetServerStartupConfigForm.setLastModifiedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
        icreateNetServerStartupConfigForm.setLastModifiedDate(new Timestamp(currentDate.getTime()));
        icreateNetServerStartupConfigForm.setLoginPrompt(updateNetServerStartupConfigForm.getLoginPrompt());
        icreateNetServerStartupConfigForm.setOperationTimeOut(updateNetServerStartupConfigForm.getOperationTimeOut());
        icreateNetServerStartupConfigForm.setPassword(updateNetServerStartupConfigForm.getPassword());
        icreateNetServerStartupConfigForm.setPasswordPrompt(updateNetServerStartupConfigForm.getPasswordPrompt());
        icreateNetServerStartupConfigForm.setProtocol(updateNetServerStartupConfigForm.getProtocol());
        icreateNetServerStartupConfigForm.setShell(updateNetServerStartupConfigForm.getShell());
        icreateNetServerStartupConfigForm.setShellPrompt(updateNetServerStartupConfigForm.getShellPrompt());
        icreateNetServerStartupConfigForm.setStatusChangeDate(new Timestamp(currentDate.getTime()));
        icreateNetServerStartupConfigForm.setUserName(updateNetServerStartupConfigForm.getUserName());
     }
    
    
    
    
    
    
    
}
