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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerConfigMapTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerGlobalConfigurationForm;

public class ListNetServerGlobalConfigurationAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "listNetServerGlobalConfiguration";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "LIST NET SERVER GLOBAL CONFIGURATION ";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		String strNetServerId = request.getParameter("netserverid");
		
		try {
			
			long netServerId=0;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
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
