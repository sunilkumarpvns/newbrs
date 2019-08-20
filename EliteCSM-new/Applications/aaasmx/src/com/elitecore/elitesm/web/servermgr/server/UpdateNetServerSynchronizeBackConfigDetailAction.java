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
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerSynchronizeBackConfigDetailForm;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerSynchronizeConfigDetailForm;

public class UpdateNetServerSynchronizeBackConfigDetailAction extends BaseDictionaryAction{
	private static final String VIEW_FORWARD = "updateNetServerSynchronizeBackConfigDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SYNCHRONIZE BACK CONFIGURATION ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;
		
		try {
			UpdateNetServerSynchronizeBackConfigDetailForm updateNetServerSynchronizeConfigDetailForm = (UpdateNetServerSynchronizeBackConfigDetailForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String strNetServerId = request.getParameter("netserverid");
			String netServerId=null;
			if(strNetServerId == null){
				netServerId = updateNetServerSynchronizeConfigDetailForm.getNetServerId();
			}else{
				netServerId = strNetServerId;
			}
			if(netServerId != null){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
			}

			return mapping.findForward(VIEW_FORWARD);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.update.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
