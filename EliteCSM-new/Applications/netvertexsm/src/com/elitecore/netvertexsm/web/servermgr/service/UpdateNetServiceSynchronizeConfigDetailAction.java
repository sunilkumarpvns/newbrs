package com.elitecore.netvertexsm.web.servermgr.service;

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
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.service.form.UpdateNetServiceSynchronizeConfigDetailForm;

public class UpdateNetServiceSynchronizeConfigDetailAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "updateNetServiceSynchronizeConfigDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SYNCHRONIZE SERVICE CONFIGURATION ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		LogManager.getLogger().trace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;
		List lstNetDriverType = null;
		
		try {
			UpdateNetServiceSynchronizeConfigDetailForm updateNetServiceSynchronizeConfigDetailForm = (UpdateNetServiceSynchronizeConfigDetailForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();

			List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
			List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String strNetServiceId = request.getParameter("netserviceid");
			Long netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = Long.parseLong(strNetServiceId);
			}else{
				netServiceId = updateNetServiceSynchronizeConfigDetailForm.getNetServiceId();
			}
			
			if(netServiceId != null){
				netServiceInstanceData.setNetServiceId(netServiceId);
				netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());
				request.setAttribute("netServiceInstanceData",netServiceInstanceData);

//			}
				netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
							
				Long netServerId = netServerInstanceData.getNetServerId();
				
				if(netServerId != null){
					netServerInstanceData.setNetServerId(netServerId);
					netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
					request.setAttribute("netServerInstanceData",netServerInstanceData);
					request.setAttribute("netServiceTypeList",netServiceTypeList);
					request.setAttribute("netServerTypeList",netServerTypeList);
					request.setAttribute("netServerInstanceList",netServerInstanceList);
				}
				return mapping.findForward(VIEW_FORWARD);
			}else{
				LogManager.getLogger().trace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.update.service.configuration.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		} catch (Exception exp) {
			LogManager.getLogger().trace(MODULE,exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
		}
		LogManager.getLogger().trace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.update.service.configuration.success");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
