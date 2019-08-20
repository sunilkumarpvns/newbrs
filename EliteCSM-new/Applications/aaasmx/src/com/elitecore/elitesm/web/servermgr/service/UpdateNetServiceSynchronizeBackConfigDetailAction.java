package com.elitecore.elitesm.web.servermgr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.service.forms.UpdateNetServiceSynchronizeBackConfigDetailForm;

public class UpdateNetServiceSynchronizeBackConfigDetailAction extends BaseDictionaryAction{
	private static final String VIEW_FORWARD = "updateNetServiceSynchronizeBackConfigDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SYNCHRONIZE BACK SERVICE CONFIGURATION ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		LogManager.getLogger().trace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;
		List lstNetDriverType = null;
		
		try {
			UpdateNetServiceSynchronizeBackConfigDetailForm updateNetServiceSynchronizeConfigDetailForm = (UpdateNetServiceSynchronizeBackConfigDetailForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();

			List netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
			List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String strNetServiceId = request.getParameter("netserviceid");
			String netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = strNetServiceId;
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
							
				String netServerId = netServerInstanceData.getNetServerId();
				
				if(netServerId != null ){
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
			exp.printStackTrace();
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
