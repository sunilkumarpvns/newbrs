/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateServerAction.java                 		
 * ModualName server    			      		
 * Created on 19 October, 2007
 * Last Modified on                                     
 * @author :  kaushik vira
 */                                                     
package com.elitecore.elitesm.web.servermgr.server; 
  
import java.util.List;

import javax.servlet.http.HttpServletRequest;    
import javax.servlet.http.HttpServletResponse;  
 
import org.apache.struts.action.ActionForm;     
import org.apache.struts.action.ActionForward;  
import org.apache.struts.action.ActionMapping;  
import org.apache.struts.action.ActionMessage;  
import org.apache.struts.action.ActionMessages; 
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateNetServerStartupConfigForm;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerInstanceForm;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
                                                                               
public class InitUpdateNetServerStartupConfigAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="SERVER";
	private static final String VIEW_FORWARD = "viewUpdateNetServerStartupConfig"; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			Logger.logTrace(MODULE,"Enter the execute method of ;"+getClass().getName());

			UpdateNetServerStartupConfigForm updateNetServerStartupConfig = (UpdateNetServerStartupConfigForm) form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			String strNetServerId = request.getParameter("netserverid");

			String netServerId=null;
			if (strNetServerId != null) {
				netServerId = strNetServerId;
			}
			
			if(netServerId != null ){
				updateNetServerStartupConfig.setNetServerId(netServerId);
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());

				INetServerStartupConfigData netServerStartupConfig = netServerInstanceData.getStartupConfig();

				if(netServerStartupConfig != null)
				{
					updateNetServerStartupConfig.setShellPrompt(netServerStartupConfig.getShellPrompt());
					updateNetServerStartupConfig.setCommunicationPort(netServerStartupConfig.getCommunicationPort());
					updateNetServerStartupConfig.setFailureMsg(netServerStartupConfig.getFailureMsg());
					updateNetServerStartupConfig.setLoginPrompt(netServerStartupConfig.getLoginPrompt());
					updateNetServerStartupConfig.setOperationTimeOut(netServerStartupConfig.getOperationTimeOut());
					updateNetServerStartupConfig.setShell(netServerStartupConfig.getShell());
					updateNetServerStartupConfig.setPassword(netServerStartupConfig.getPassword());
					updateNetServerStartupConfig.setUserName(netServerStartupConfig.getUserName());
					updateNetServerStartupConfig.setPasswordPrompt(netServerStartupConfig.getPasswordPrompt());
					updateNetServerStartupConfig.setProtocol(netServerStartupConfig.getProtocol());
				}
				else
				{
					updateNetServerStartupConfig.setCommunicationPort(23);
					updateNetServerStartupConfig.setShell("bash");
					updateNetServerStartupConfig.setFailureMsg("ogin failure");
					updateNetServerStartupConfig.setLoginPrompt("ogin");
					updateNetServerStartupConfig.setOperationTimeOut(5000);
					updateNetServerStartupConfig.setPasswordPrompt("assword");
				}
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
				return mapping.findForward(VIEW_FORWARD);
			}else{
				Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.server.createServerStarupConfig.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}

		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.server.createServerStarupConfig.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}         

}																
