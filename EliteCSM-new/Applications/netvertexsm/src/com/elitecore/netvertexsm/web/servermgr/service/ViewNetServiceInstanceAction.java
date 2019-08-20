package com.elitecore.netvertexsm.web.servermgr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.service.form.ViewNetServiceInstanceForm;

public class ViewNetServiceInstanceAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewNetServiceInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW NET SERVICE INSTANCE ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		List lstNetDriverType = null;
		
		try {
			ViewNetServiceInstanceForm viewNetServiceInstanceForm = (ViewNetServiceInstanceForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			
			INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
			List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
			List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
			String strNetServiceId = request.getParameter("netserviceid");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}else{
				netServiceId = viewNetServiceInstanceForm.getNetServiceId();
			}
			
			if(netServiceId != null ){
				netServiceInstanceData.setNetServiceId(netServiceId);
				netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());
				request.setAttribute("netServiceInstanceData",netServiceInstanceData);
				request.setAttribute("netServiceTypeList",netServiceTypeList);
				request.setAttribute("netServerInstanceList",netServerInstanceList);
				return mapping.findForward(VIEW_FORWARD);
			}else{
				Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.viewservice.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}		
		} catch (Exception e) {
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.viewservice.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
