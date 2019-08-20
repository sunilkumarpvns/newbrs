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
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;


public class InitSearchFileAction extends BaseWebAction{
    private static final String MODULE = "InitSearchFileAction";
    private static final String VIEW_FORWARD = "initSearchFile";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        try {
            String strNetServerId = request.getParameter("netserverid");
            request.getSession().setAttribute("netserverid", strNetServerId);
            
            String netServerId=null;
			if (strNetServerId != null) {
				netServerId = strNetServerId;
			}
            
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);
            
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        return mapping.findForward(VIEW_FORWARD);
    }
}
