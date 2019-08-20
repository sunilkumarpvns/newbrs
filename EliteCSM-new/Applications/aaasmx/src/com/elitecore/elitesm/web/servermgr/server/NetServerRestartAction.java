/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerRestartAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

/**
 * @author kaushikvira
 */
public class NetServerRestartAction extends BaseDictionaryAction {

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
			String netServerId=null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
				strNetServerId = (String) request.getAttribute("netServerId");
			}
			if (strNetServerId == null)
				throw new InvalidValueException("netServerId Detail must be specified.");

			netServerInstanceData.setNetServerId(netServerId);
			netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
			netServerTypeData=netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
			IRemoteServerOperartionManager iRemoteServerOperarionManager = CommunicationManagerFactory.getRemoteServeraoperarionManager(CommunicationConstant.TELNET, netServerInstanceData,netServerTypeData);
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
				doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
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
			request.setAttribute("responseUrl", "/listNetServerInstance.do");
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
