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
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerInstanceForm;

public class ViewNetServerInstanceAction  extends BaseDictionaryAction{
	private static final String VIEW_FORWARD = "viewNetServerInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW NET SERVER INSTANCE ACTION";
	private static final String VIEW_SERVER_ACTION = ConfigConstant.VIEW_SERVER_ACTION; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
	
		try {
			
			checkActionPermission(request, VIEW_SERVER_ACTION);
			ViewNetServerInstanceForm viewNetServerInstanceForm = (ViewNetServerInstanceForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			String strNetServerId = request.getParameter("netserverid");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	             
			String netServerId=null;
    		if(strNetServerId != null){
    			netServerId = strNetServerId;
    		}else{
				netServerId = viewNetServerInstanceForm.getNetServerId();
			}
			
			if(netServerId != null ){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
					
				request.setAttribute("netServerTypeList",netServerTypeList);
				doAuditing(staffData, VIEW_SERVER_ACTION);
				return mapping.findForward(VIEW_FORWARD);
			}else{
				Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.viewserver.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (Exception e) {
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.viewserver.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
