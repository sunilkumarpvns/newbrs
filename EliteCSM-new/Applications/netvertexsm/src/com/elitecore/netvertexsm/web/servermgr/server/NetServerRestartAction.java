/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerRestartAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.web.servermgr.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

/**
 * @author kaushikvira
 */
public class NetServerRestartAction extends BaseWebAction {

	private static final String MODULE          = "RESTART NETSERVER INSTANCE";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.NET_SERVER_RESTART_ACTION;
	
	public ActionForward execute( ActionMapping mapping ,
			ActionForm form ,
			HttpServletRequest request ,
			HttpServletResponse response ) throws Exception {

		Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
		INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
		INetServerTypeData netServerTypeData = new NetServerTypeData();
		String strNetServerId = null;
		try {
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}else{
				strNetServerId = (String) request.getAttribute("netServerId");
			}
			if (strNetServerId == null)
				throw new InvalidValueException("netServerId Detail must be specified.");

			netServerInstanceData.setNetServerId(netServerId);
			netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
			netServerTypeData=netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
			IRemoteServerOperartionManager iRemoteServerOperarionManager = CommunicationManagerFactory.getRemoteServeraoperarionManager(CommunicationConstant.TELNET, netServerInstanceData, netServerTypeData);
			if (!iRemoteServerOperarionManager.initRestartServer()) {
				Logger.logWarn(MODULE, "Server is in Restart Process.." + netServerId);
				request.setAttribute("responseUrl", "/initNetServerStartStop.do=netServerId=" + netServerInstanceData.getNetServerId());
				ActionMessage message = new ActionMessage("servermgr.server.restart.inprocess");
				ActionMessages messages = new ActionMessages();
				messages.add("warn", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE_FORWARD);
			}else
			{
				Logger.logWarn(MODULE, "Server is in Restart Process.." + netServerId);
				request.setAttribute("responseUrl", "/initNetServerStartStop.do?netServerId="+netServerInstanceData.getNetServerId());
				ActionMessage message = new ActionMessage("servermgr.server.restart.initrestart.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);

			}
	        
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
		catch (InitializationFailedException e) {
			Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/initNetServerStartStop.do?netServerId=" + netServerInstanceData.getNetServerId());
			ActionMessage message = new ActionMessage("servermgr.server.restart.initrestart.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (InvalidValueException e) {
			Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/serverGroupManagement.do?method=initSearch");
			ActionMessage message = new ActionMessage("servermgr.server.restart.initrestart.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		catch (DataValidationException e) {
			Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/initNetServerStartStop.do?netServerId=" + netServerInstanceData.getNetServerId());
			ActionMessage message = new ActionMessage("servermgr.server.restart.initrestart.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		catch (DataManagerException exp) {
			Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(exp));
			Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/initNetServerStartStop.do?netServerId=" + netServerInstanceData.getNetServerId());
			ActionMessage message = new ActionMessage("sservermgr.server.restart.initrestart.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		catch (Exception e) {
			Logger.logError(MODULE, "Error during Communication Manager Operation" + EliteExceptionUtils.getMessage(e));
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/initNetServerStartStop.do?netServerId=" + netServerInstanceData.getNetServerId());
			ActionMessage message = new ActionMessage("servermgr.server.restart.initrestart.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE_FORWARD);
	}

}
