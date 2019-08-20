/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitNetServerStartStopAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
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
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.IRemoteServerOperartionManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.NetServerStartStopForm;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class InitNetServerStartStopAction extends BaseWebAction {

	private static final String VIEW_FORWARD    = "netServerStartStop";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE          = "SERVER VIEW START STOP ACTION";

	public ActionForward execute( ActionMapping mapping ,
			ActionForm form ,
			HttpServletRequest request ,
			HttpServletResponse response ) throws Exception {
		Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
		List netServerTypeList = null;
		IRemoteCommunicationManager remoteCommunicationManager = null;

		try {
			NetServerStartStopForm netServerStartStopForm = (NetServerStartStopForm) form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
			INetServerTypeData netServerTypeData = new NetServerTypeData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();

			String strNetServerId = request.getParameter("netserverid");

			if (strNetServerId == null) {
				strNetServerId = (String) request.getAttribute("netserverid");
			}

			Long netServerId=null;
			if (strNetServerId == null) {
				netServerId = netServerStartStopForm.getNetServerId();
			}

			if (netServerId == null)
				throw new InvalidValueException("netServerId must be specified.");

			if (netServerId>0) {

				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				netServerTypeData = netServerBLManager.getNetServerType(netServerInstanceData.getNetServerTypeId());
				boolean isConfigInSync =   netServerBLManager.isConfigInSync(netServerInstanceData.getNetServerId());
				Logger.logDebug(MODULE, "Server Config Sync Status :- " + isConfigInSync + "(Note :-true-Insync,false:-Unsync)");
				netServerStartStopForm.setConfigInSyncState(isConfigInSync);


				IRemoteServerOperartionManager iRemoteServerOperationManager = CommunicationManagerFactory.getRemoteServeraoperarionManager(CommunicationConstant.TELNET, netServerInstanceData, netServerTypeData);

				if (iRemoteServerOperationManager.isProcessRunning()) {
					netServerStartStopForm.setNetServerRestartStatus(true);
					netServerStartStopForm.setNetSeverStatus(true);
				} else {
					try {
						remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
						String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
						remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,true);
						Logger.logDebug(MODULE, "Server Status : start");
						netServerStartStopForm.setNetSeverStatus(true);
					}
					catch(UnidentifiedServerInstanceException e)
					{
						Logger.logDebug(MODULE,"Unidentified Server Found.. No action taken");
						netServerStartStopForm.setNetSeverStatus(true);
					}
					catch (CommunicationException e) {
						Logger.logDebug(MODULE, "Server Status : Stoped." );
						netServerStartStopForm.setNetSeverStatus(false);

					}
					finally {
						if (remoteCommunicationManager != null)
							remoteCommunicationManager.close();
					}
				}
				request.setAttribute("netServerInstanceData", netServerInstanceData);
				request.setAttribute("netServerTypeList", netServerTypeList);
				request.setAttribute("netServerId", netServerId);
			}
			Logger.logInfo(MODULE, "Retuning from the execute() " + getClass().getName());
			return mapping.findForward(VIEW_FORWARD);
		}

		catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.service.start.request.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

}
