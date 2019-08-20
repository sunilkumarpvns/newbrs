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
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetServerReloadConfigurationForm;

public class NetServerReloadConfigurationAction extends BaseDictionaryAction{
	private static final String VIEW_FORWARD = "netServerReloadConfiguration";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SERVER RELOADCONFIG ACTION";
	private static final String ACTION_ALIAS  = ConfigConstant.RELOAD_CONFIGURATION_ACTION; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;

		try {
			checkActionPermission(request, ACTION_ALIAS);
			NetServerReloadConfigurationForm netServerReloadConfigurationForm = (NetServerReloadConfigurationForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String strNetServerId = request.getParameter("netserverid");
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
				netServerId = netServerReloadConfigurationForm.getNetServerId();
			}

			if(netServerId != null ){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
				request.setAttribute("netServerId",netServerId);
				doAuditing(staffData, ACTION_ALIAS);
			}
			
			return mapping.findForward(VIEW_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE,e.getMessage());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}

		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.service.reload.configuration.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
