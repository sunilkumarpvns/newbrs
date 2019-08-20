/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitNetServerStartStopAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerLogsForm;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class InitListNetServerLogsAction extends BaseWebAction{

	private static final String VIEW_FORWARD    = "listNetServerLogs";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE          = InitListNetServerLogsAction.class.getSimpleName();
	private static final String ACTION_ALIAS    = ConfigConstant.DOWNLOAD_LOG;

	public ActionForward execute( ActionMapping mapping ,ActionForm form ,HttpServletRequest request , HttpServletResponse response ) throws Exception {
		Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
		List netServerTypeList = null;
		IRemoteCommunicationManager remoteCommunicationManager = null;
		ListNetServerLogsForm listNetServerLogsForm = null;
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		try {
			checkAccess(request, ACTION_ALIAS);
			listNetServerLogsForm = (ListNetServerLogsForm) form;
			String strNetServerId = request.getParameter("netServerId");

			if (strNetServerId == null) {
				strNetServerId = (String) request.getAttribute("netserverid");
			}

			String netServerId;
			if (strNetServerId == null) {
				netServerId = listNetServerLogsForm.getNetServerId();
			}else{
				netServerId = strNetServerId.trim();	
			}


			NetServerBLManager netServerBLManager = new NetServerBLManager();

			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();

			netServerTypeList = netServerBLManager.getNetServerTypeList();

			if (netServerId == null)
				throw new InvalidValueException("netServerId must be specified.");

			if ( Strings.isNullOrBlank(netServerId) == false) {

				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData", netServerInstanceData);
				request.setAttribute("netServerTypeList", netServerTypeList);
				request.setAttribute("netServerId", netServerId);
				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
				remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,true);

				try {
					Map<String,Object> serverLogFileMap = ( Map<String,Object>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION, "retriveServerLogFileList",null,null);
					Map<String,Object> serviceLogFileMap = ( Map<String,Object>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION, "retriveServiceLogFileList",null,null);
					Map<String,Object> cdrFileMap = ( Map<String,Object>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION, "retriveCDRFileList",null,null);
					
					listNetServerLogsForm.setServerLogFileMap(serverLogFileMap);
					listNetServerLogsForm.setServiceLogFileMap(serviceLogFileMap);
					listNetServerLogsForm.setCdrFileMap(cdrFileMap);

				}catch (Exception e) {
					Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
					Logger.logTrace(MODULE, e);
				}
				listNetServerLogsForm.setErrorCode("0");
			}
			Logger.logInfo(MODULE, "Retuning from the execute() " + getClass().getName());
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (UnidentifiedServerInstanceException commExp) {
			commExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch (CommunicationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			listNetServerLogsForm.setErrorCode("-1");
			return mapping.findForward(VIEW_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.downloadlogs.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		finally {
			if (remoteCommunicationManager != null)
				remoteCommunicationManager.close();
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
