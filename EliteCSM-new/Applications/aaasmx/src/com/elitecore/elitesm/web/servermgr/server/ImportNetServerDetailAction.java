package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ImportNetServerConfigurationForm;

public class ImportNetServerDetailAction extends BaseDictionaryAction {
	private static final String VIEW_FORWARD = "importNetServerDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "IMPORT SERVER ACTION";
	private static final String SUCCESS_FORWARD = "success";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		List netServerTypeList = null;

		try {
			ImportNetServerConfigurationForm importNetServerConfigurationForm = (ImportNetServerConfigurationForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String strNetServerId = request.getParameter("netserverid");
			String netServerId = null;
			if (strNetServerId != null) {
            	netServerId = strNetServerId.trim();
            }
			importNetServerConfigurationForm.setNetServerId(netServerId);

			if( Strings.isNullOrBlank(netServerId) == false){
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
			}

			/*request.setAttribute("responseUrl","/importNetServerDetail.do?netserverid="+netServerInstanceData.getNetServerId());
			ActionMessage message = new ActionMessage("servermgr.server.updateadmininterface.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS_FORWARD);*/
			return mapping.findForward(VIEW_FORWARD);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.server.import.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
