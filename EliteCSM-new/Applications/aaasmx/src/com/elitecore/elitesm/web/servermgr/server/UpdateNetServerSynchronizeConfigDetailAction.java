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
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerSynchronizeConfigDetailForm;

public class UpdateNetServerSynchronizeConfigDetailAction extends BaseWebAction{
    private static final String VIEW_FORWARD = "updateNetServerSynchronizeConfigDetail";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "SYNCHRONIZE CONFIGURATION ACTION";
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

        try {
            UpdateNetServerSynchronizeConfigDetailForm updateNetServerSynchronizeConfigDetailForm = (UpdateNetServerSynchronizeConfigDetailForm)form;
            NetServerBLManager netServerBLManager = new NetServerBLManager();

            String strNetServerId = request.getParameter("netserverid");
			String netServerId=null;
			if(strNetServerId == null){
				netServerId = updateNetServerSynchronizeConfigDetailForm.getNetServerId();
			}else{
				netServerId = strNetServerId;
			}
            
            if(netServerId != null){
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("netServerTypeList",netServerTypeList);
            }

            return mapping.findForward(VIEW_FORWARD);
        } catch (Exception e) {
            Logger.logError(MODULE,e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
        }
        ActionMessage message = new ActionMessage("servermgr.update.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
