/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ViewNetServerLogReportAction.java                            
 * ModualName Server                                      
 * Created on Jan 7, 2008
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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLogReportForm;


public class ViewNetServerLogReportAction extends BaseWebAction {
    
    @SuppressWarnings("hiding")
    private static final String MODULE          = "Init View LogReport ";
    
    private static final String VIEW_FORWARD    = "viewLogReport";
    
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_LOG_REPORT_ACTION;
    
    @Override
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {

    	LogManager.getLogger().info(MODULE, "Enter execute method of " + getClass().getName());

    	try {
    		checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
    		ViewNetServerLogReportForm viewNetServerLogReportForm = (ViewNetServerLogReportForm) form;

    		NetServerBLManager netServerBLManager = new NetServerBLManager();
    		INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
    		List netServerTypeList = netServerBLManager.getNetServerTypeList();
    		String strNetServerId = request.getParameter("netServerId");
    		viewNetServerLogReportForm.setRadioChoice("summary");
    		Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}else{
    			netServerId = viewNetServerLogReportForm.getNetServerId();
    		}

    		if (netServerId != null) {
    			netServerInstanceData.setNetServerId(netServerId);
    			netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
    			request.setAttribute("netServerInstanceData", netServerInstanceData);
    			request.setAttribute("netServerTypeList", netServerTypeList);
    			request.setAttribute("netServerId",netServerId);
    			return mapping.findForward(VIEW_FORWARD);
    		}
    		LogManager.getLogger().trace(MODULE, "Returning error forward from " + getClass().getName());
    		ActionMessage message = new ActionMessage("servermgr.server.view.logreport.failure","");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveErrors(request, messages);
    		return mapping.findForward(FAILURE_FORWARD);

    	}
    	catch(ActionNotPermitedException e){
    		LogManager.getLogger().error(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
    	catch (Exception e) {
    		LogManager.getLogger().error(MODULE, "Returning error forward from " + getClass().getName());
    		LogManager.getLogger().trace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.view.logreport.failure","");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveErrors(request, messages);
    		return mapping.findForward(FAILURE_FORWARD);
    	}
    }
}
