/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ViewNetServerLogReportDetailAction.java                            
 * ModualName Server                                      
 * Created on Jan 7, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.web.servermgr.server;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.netvertexsm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 *
 */
public class ViewNetServerLogReportDetailAction extends BaseWebAction {

	private static final String MODULE          = "View LogReport Detail";

	private static final String VIEW_FORWARD    = "viewLogReportDetail";

	private static final String FAILURE_FORWARD = "failure";

	@Override
	public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {

		LogManager.getLogger().info(MODULE, "Enter the execute method of :" + getClass().getName());
		IRemoteCommunicationManager remoteCommunicationManager = null;

		String strNetServerId = request.getParameter("netServerId");
		String triBandnNumber = request.getParameter("triBandnNumber");
		String date = request.getParameter("date");

		LogManager.getLogger().debug(MODULE, "Tracking Request parameters :");
		LogManager.getLogger().debug(MODULE, "date :" + date);
		LogManager.getLogger().debug(MODULE, "triBandnNumber :" + triBandnNumber);
		LogManager.getLogger().debug(MODULE, "netServerid :" + strNetServerId);
		INetServerInstanceData serverInstanceData = null;
		try {
			if (EliteGenericValidator.isBlankOrNull(strNetServerId))
				throw new NullValueException("netServerId must be specified.");
    		Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}
			NetServerBLManager netServerBlManager = new NetServerBLManager();
			serverInstanceData = netServerBlManager.getNetServerInstance(netServerId);
			request.setAttribute("netServerInstanceData",serverInstanceData);

			String ipAddress = serverInstanceData.getAdminHost();
			int port = serverInstanceData.getAdminPort();

			Map paramMap = new HashMap();

			paramMap.put(ServermgrConstant.FILE_PATTERN, "EliteRadiusLog");
			paramMap.put(ServermgrConstant.TRIBANDNUMBER, triBandnNumber);
			paramMap.put(ServermgrConstant.RECORD_DATE, date);

			Object[] objArgValues = { paramMap };
			String[] strArgTypes = { "java.util.Map" };

			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
			String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
			remoteCommunicationManager.init(ipAddress, port, netServerCode, true);
			Map resultMap = (HashMap) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "getDetailLoginAttemptsReport", objArgValues, strArgTypes);

			if(resultMap == null || resultMap.containsKey("FileNotFound"))
				throw new FileNotFoundException("FileNotFoundException on Server");

			request.setAttribute("errorCode", "0");
			request.setAttribute("resultMap",resultMap);
			request.setAttribute("date",date);

			return mapping.findForward(VIEW_FORWARD);
		}
		catch (FileNotFoundException commExp) {
			LogManager.getLogger().error(MODULE, "Error during  operation , reason : " + commExp.getMessage());
			LogManager.getLogger().trace(MODULE, commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.view.logreport.failure","Detail");
			ActionMessage messageReason = new ActionMessage("servermgr.server.view.logreport.filenotfound");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			messages.add("information", messageReason);
			saveErrors(request, messages);
		}
		catch (UnidentifiedServerInstanceException commExp) {
			LogManager.getLogger().error(MODULE, "Error during  operation , reason : " + commExp.getMessage());
			LogManager.getLogger().trace(MODULE, commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			messages.add("information", messageReason);
			saveErrors(request, messages);
		}
		catch (CommunicationException e) {
			request.setAttribute("errorCode", "-1");
			return mapping.findForward(VIEW_FORWARD);
		}
		catch (NullValueException e) {
			LogManager.getLogger().error(MODULE, "Error during  operation , reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage messageReason = new ActionMessage("servermgr.server.view.logreport.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", messageReason);
			saveErrors(request, messages);
		}
		catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error during  operation , reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage messageReason = new ActionMessage("servermgr.server.view.logreport.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", messageReason);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);

	}
}
