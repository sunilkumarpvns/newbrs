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
import com.elitecore.netvertexsm.web.servermgr.server.form.NetServerStartForm;

public class NetServerStartAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "netServerStart";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SERVER START ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;
		
		try {
			NetServerStartForm netServerStartForm = (NetServerStartForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String strNetServerId = request.getParameter("netserverid");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}else{
				netServerId = netServerStartForm.getNetServerId();
			}
			
			if(netServerId != null){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
				request.setAttribute("netServerId",netServerId);
			}
			
			return mapping.findForward(VIEW_FORWARD);
		} catch (Exception e) {
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.service.start.request.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}

