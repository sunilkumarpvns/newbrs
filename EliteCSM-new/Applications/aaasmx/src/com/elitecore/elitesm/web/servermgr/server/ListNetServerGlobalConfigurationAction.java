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
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerConfigMapTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerGlobalConfigurationForm;

public class ListNetServerGlobalConfigurationAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "listNetServerGlobalConfiguration";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "LIST NET SERVER GLOBAL CONFIGURATION ";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		String strNetServerId = request.getParameter("netserverid");
		
		try {
			
			String netServerId = null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			ListNetServerGlobalConfigurationForm listNetServerGlobalConfigurationForm = (ListNetServerGlobalConfigurationForm)form;
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			INetServerConfigMapTypeData netServerConfigMapTypeData = new NetServerConfigMapTypeData();
			netServerInstanceData.setNetServerId(netServerId);

			String netConfigMapTypeId = "G";
			List configInstanceList = netServerBLManager.getNetServerConfigInstanceList(netServerId,netConfigMapTypeId);
			listNetServerGlobalConfigurationForm.setConfigInstanceList(configInstanceList);
			listNetServerGlobalConfigurationForm.setNetServerId(netServerId);

			netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
			Logger.logTrace(MODULE,"Returning success forward from "+getClass().getName());
		
//			for(int i=0;i<configInstanceList.size();i++){
			netServerConfigMapTypeData = netServerBLManager.getNetServerConfigMapType(netConfigMapTypeId);
//			}
			
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
			
			request.setAttribute("netServerInstanceData",netServerInstanceData);
			request.setAttribute("netServerConfigMapTypeData",netServerConfigMapTypeData);
			request.setAttribute("netServerInstanceList",netServerInstanceList);			
			request.setAttribute("netServerTypeList",netServerTypeList);
			
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (DataManagerException hExp) {
			hExp.getMessage();
			Logger.logTrace(MODULE,"Error during data Manager Operation, reasib :"+hExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.update.server.global.configuration.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
