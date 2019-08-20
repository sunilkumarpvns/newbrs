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
import com.elitecore.elitesm.web.servermgr.server.forms.SearchBatchForm;

public class InitSearchBatchAction extends BaseWebAction{
    private static final String MODULE = "InitSearchBatchAction";
    private static final String VIEW_FORWARD = "initSearchBatch";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        SearchBatchForm searchBatchForm = (SearchBatchForm)form;
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        try {
            String strNetServerId = request.getParameter("netserverid");

			if (strNetServerId == null) {
				strNetServerId = (String) request.getAttribute("netserverid");
			}

			String netServerId=null;
			if (strNetServerId != null) {
				netServerId = strNetServerId;
			}
            
            searchBatchForm.setServerId(netServerId);
           
            
            request.getSession().setAttribute("batchList", null);
            
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
