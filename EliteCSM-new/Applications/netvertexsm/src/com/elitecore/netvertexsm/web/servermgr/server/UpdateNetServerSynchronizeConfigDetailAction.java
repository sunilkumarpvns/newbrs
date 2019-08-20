package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetServerSynchronizeConfigDetailForm;

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
			Long netServerId=null;
			if(strNetServerId == null){
				netServerId = updateNetServerSynchronizeConfigDetailForm.getNetServerId();
			}else{
				netServerId = Long.parseLong(strNetServerId);
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
