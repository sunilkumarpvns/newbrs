/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitNetServerStartStopAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.web.servermgr.server;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerLogsForm;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author Milan Paliwal
 */
public class InitListNetServerLogsAction extends BaseWebDispatchAction{

	private static final String VIEW_FORWARD    = "listNetServerLogs";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE          = InitListNetServerLogsAction.class.getSimpleName();
	private static final String ACTION_ALIAS    = "User Trace Logs";
	private String serverName;
	private long bytesRead = 0L;
	private IRemoteCommunicationManager remoteCommunicationManager = null;
	private static INetServerInstanceData netServerInstanceData = null;
	private boolean isFirstCall = false;
	
	LogMonitorCommunicationManager communicationManager = new LogMonitorCommunicationManager();
	
	public ActionForward search( ActionMapping mapping ,ActionForm form ,HttpServletRequest request , HttpServletResponse response ) throws Exception {
		Logger.logInfo(MODULE, "Enter search() method of " + getClass().getName());
		List netServerTypeList = null;
		
		ListNetServerLogsForm listNetServerLogsForm = null;
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		try {
			checkAccess(request, ACTION_ALIAS);
			listNetServerLogsForm = (ListNetServerLogsForm) form;
			String strNetServerId = request.getParameter("netServerId");

			if (strNetServerId == null) {
				strNetServerId = (String) request.getAttribute("netserverid");
			}
			
			Long netServerId;
			if (strNetServerId == null) {
				netServerId = listNetServerLogsForm.getNetServerId();
			}else{
				netServerId = Long.parseLong(strNetServerId.trim());	
			}
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			netServerInstanceData = new NetServerInstanceData();
			netServerTypeList = netServerBLManager.getNetServerTypeList();

			if (netServerId == null)
				throw new InvalidValueException("netServerId must be specified.");

			if (netServerId != null && netServerId>0) {

				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				request.setAttribute("netServerInstanceData", netServerInstanceData);
				request.setAttribute("netServerTypeList", netServerTypeList);
				request.setAttribute("netServerId", netServerId);
				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                
				remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
				remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,true);
				
				HashMap<String, List<LogMonitorInfo>> hashMap = communicationManager.getLogMonitors(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode);
				if(hashMap!=null){
					Logger.logInfo(MODULE, "HashMapSize : "+hashMap.size());
				}else{
					Logger.logInfo(MODULE, "HashMapSize is Empty");
				}
				ArrayList<LogMonitorData> logMonitorDatas = new ArrayList<LogMonitorData>();
				String logMonitorTypes = "";
				if(hashMap != null && hashMap.isEmpty() == false){
					for(Entry<String, List<LogMonitorInfo>> entry : hashMap.entrySet()){						
						logMonitorTypes += entry.getKey()+",";
						List<LogMonitorInfo> logMonitorInfoList = (List<LogMonitorInfo>)entry.getValue();
						if(logMonitorInfoList!=null){
							Logger.logInfo(MODULE, "logMonitorInfoList size : "+logMonitorInfoList.size());
						}else{
							Logger.logInfo(MODULE, "logMonitorInfoList is Empty");
						}
						for(LogMonitorInfo logMonitorInfo:logMonitorInfoList){
							Logger.logInfo(MODULE, "Expression  : "+logMonitorInfo.getExpression());
							Logger.logInfo(MODULE, "Duration 	: "+logMonitorInfo.getDuration());
							LogMonitorData logMonitorData = new LogMonitorData();
							logMonitorData.setLogMonitorType(entry.getKey());							
							logMonitorData.setExpression(logMonitorInfo.getExpression());
							logMonitorData.setStartTime(simpleDateFormatPool.get().format(new Date(logMonitorInfo.getStartTime())));
							logMonitorData.setExpiryTime(simpleDateFormatPool.get().format(new Date(logMonitorInfo.getExpiryTime())));
							logMonitorData.setDuration(logMonitorInfo.getDuration());
							logMonitorDatas.add(logMonitorData);
						}
					}
				}
				listNetServerLogsForm.setLogMonitorDatas(logMonitorDatas);
				request.setAttribute("logMonitorDatas",logMonitorDatas);
				request.setAttribute("registeredLogMonitorTypes",logMonitorTypes);
				listNetServerLogsForm.setErrorCode("0");
			}
			request.setAttribute("serverLogForm",listNetServerLogsForm);
			Logger.logInfo(MODULE, "Returning from the search() " + getClass().getName());
			isFirstCall = true;
			
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
		}catch (CommunicationException commExp) {
			commExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("closeBttn", "yes");
			
			ActionMessage message = new ActionMessage("servermgr.server.communicate.fail",netServerInstanceData.getAdminHost());						
			ActionMessage messageReason = new ActionMessage("servermgr.server.down");			
			ActionMessages messages = new ActionMessages();			
 			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			
			ActionMessages errorHeadingMessage = new ActionMessages();
			ActionMessage errorHeading = new ActionMessage("servermgr.server.trace.view.failed");
            errorHeadingMessage.add("errorHeading",errorHeading);            
            saveMessages(request,errorHeadingMessage);	
			return mapping.findForward(FAILURE_FORWARD);
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
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward deleteAllLogMonitors(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter deleteAllLogMonitors() method of " + getClass().getName());
		try {
				String totalLogMonitors = request.getParameter("totalLogMonitors");
				short iTotalLogMonitors = (StringUtils.isBlank(totalLogMonitors))?0:Short.parseShort(totalLogMonitors);				
				Logger.logDebug(MODULE, "Total LogMonitors : "+totalLogMonitors);
				
				String[] selectedLogMonitors = request.getParameterValues("dataRowCheckBox");
				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
				Logger.logDebug(MODULE, "Selected LogMonitors : "+selectedLogMonitors);
				
				short deleteSuccessCount = 0 ;				
				if(iTotalLogMonitors == selectedLogMonitors.length){
					Logger.logInfo(MODULE, "Deleting All LogMonitors");	
					communicationManager.deleteAllLogMonitors(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode);
					deleteSuccessCount = iTotalLogMonitors ;
					
				}else{
					Logger.logInfo(MODULE, "Deleting Selected LogMonitors Only");
					for(String logMonitor : selectedLogMonitors){									
						LogMonitorData logMonitorData = new LogMonitorData();
						String logMonitorType = logMonitor.split("JOIN")[0];
						String logMonitorExpression = logMonitor.split("JOIN")[1];
						Logger.logDebug(MODULE, "LogMonitor Type: "+logMonitorType);
						Logger.logDebug(MODULE, "LogMonitor Expression: "+logMonitorExpression);
						
						logMonitorData.setLogMonitorType(logMonitorType);
						logMonitorData.setExpression(logMonitorExpression);
											
						boolean isLogMonitorDeleted = communicationManager.deleteLogMonitor(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,logMonitorData);					
						if(isLogMonitorDeleted){
							deleteSuccessCount++ ;
							Logger.logInfo(MODULE,"Deleted Log Monitor : "+logMonitorData);
						}else{
							Logger.logInfo(MODULE,"Failed to deleted Log Monitor : "+logMonitorData);
						}
					}
				}
				
				boolean isAnyLogMonitorDeleted = false;
				if(deleteSuccessCount>0){
					isAnyLogMonitorDeleted = true;					
				}
				
			 	if(isAnyLogMonitorDeleted){
               		Logger.logDebug(MODULE, deleteSuccessCount+" Log Monitor(s) deleted Successfully");
                   	ActionMessage message = new ActionMessage("logmonitor.delete.all.success", ""+deleteSuccessCount);
    	            ActionMessages messages = new ActionMessages();
    	            messages.add("information", message);
    	            saveMessages(request,messages);
                   	request.setAttribute("responseUrl","/initListNetServerLogs.do?method=search&netServerId=" + netServerInstanceData.getNetServerId());               		
               		return mapping.findForward(SUCCESS);
               		
               	}else{
        	        Logger.logError(MODULE, "Error in deleting All Log Monitors");
        	        ActionMessage message = new ActionMessage("logmonitor.delete.all.failure");
        	        ActionMessages messages = new ActionMessages();
        	        messages.add("information", message);
        	        saveErrors(request, messages);
        	        
                    ActionMessages errorHeadingMessage = new ActionMessages();
                    message = new ActionMessage("logmonitor.error.heading","deleting");
                    errorHeadingMessage.add("errorHeading",message);
                    saveMessages(request,errorHeadingMessage);
                    
               		return mapping.findForward(FAILURE);
               	}
				
				
		} catch (Exception e) {
			Logger.logDebug(MODULE, "Error while deleting All Log Monitors, Reason : "+e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("logmonitor.delete.all.failure");
			ActionMessage messageReason = new ActionMessage("logmonitor.delete.all.failure.reason",e.getMessage());			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("logmonitor.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            
			return mapping.findForward(FAILURE_FORWARD);
		}
	}	
	
	
	public ActionForward deleteLogMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter deleteLogMonitor() method of " + getClass().getName());
		try {
				String logMonitorType = request.getParameter("logMonitorType");
				String expression = request.getParameter("expression");
				Logger.logInfo(MODULE, "logMonitorType : " + logMonitorType);
				Logger.logInfo(MODULE, "expression : " + expression);
				
				LogMonitorData logMonitorData = new LogMonitorData();
				logMonitorData.setLogMonitorType(logMonitorType);
				logMonitorData.setExpression(expression);

				Logger.logInfo(MODULE,logMonitorData);

				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
				boolean isLogMonitorDeleted = communicationManager.deleteLogMonitor(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,logMonitorData);
				
			 	if(isLogMonitorDeleted){
               		Logger.logError(MODULE, "Log Monitor deleted Successfully");
                   	ActionMessage message = new ActionMessage("logmonitor.delete.success", logMonitorType);
    	            ActionMessages messages = new ActionMessages();
    	            messages.add("information", message);
    	            saveMessages(request,messages);
                   	request.setAttribute("responseUrl","/initListNetServerLogs.do?method=search&netServerId=" + netServerInstanceData.getNetServerId());               		
               		return mapping.findForward(SUCCESS);
               		
               	}else{
        	        Logger.logError(MODULE, "Error in deleting Log Monitor");
        	        ActionMessage message = new ActionMessage("logmonitor.delete.failure");
        	        ActionMessages messages = new ActionMessages();
        	        messages.add("information", message);
        	        saveErrors(request, messages);
        	        
                    ActionMessages errorHeadingMessage = new ActionMessages();
                    message = new ActionMessage("logmonitor.error.heading","deleting");
                    errorHeadingMessage.add("errorHeading",message);
                    saveMessages(request,errorHeadingMessage);
                    
               		return mapping.findForward(FAILURE);
               	}
				
				
		} catch (Exception e) {
			Logger.logDebug(MODULE, "Error while deleting Log Monitor, Reason : "+e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("logmonitor.delete.failure");
			ActionMessage messageReason = new ActionMessage("logmonitor.delete.failure.reason",e.getMessage());			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("logmonitor.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            
			return mapping.findForward(FAILURE_FORWARD);
		}
	}	

	public ActionForward editLogMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter editLogMonitor() method of " + getClass().getName());
		try {

				String oldLogMonitorType = request.getParameter("oldLogMonitorType");
				String oldExpression = request.getParameter("oldExpression");
				LogMonitorData oldLogMonitorData = new LogMonitorData();
				oldLogMonitorData.setLogMonitorType(oldLogMonitorType);
				oldLogMonitorData.setExpression(oldExpression);
				
				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
				boolean isLogMonitorDeleted = communicationManager.deleteLogMonitor(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,oldLogMonitorData);
				
			 	if(isLogMonitorDeleted){	
			 		
					String newlogMonitorType = request.getParameter("editLogMonitorType");
					String newExpression = request.getParameter("editExpression");
					String newDuration = request.getParameter("editDuration");
					
					LogMonitorData newLogMonitorData = new LogMonitorData();
					newLogMonitorData.setLogMonitorType(newlogMonitorType);
					newLogMonitorData.setExpression(newExpression);					
					newLogMonitorData.setDuration((newDuration!=null && newDuration.trim().length()>0)?Long.parseLong(newDuration):0);
					
					Logger.logInfo(MODULE,newLogMonitorData);
					boolean isLogMonitorAdded = communicationManager.addLogMonitors(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,newLogMonitorData);
			 	
				 	if(isLogMonitorAdded){
	               		Logger.logError(MODULE, "Log Monitor Updated Successfully");
	                   	ActionMessage message = new ActionMessage("logmonitor.update.success", "");
	    	            ActionMessages messages = new ActionMessages();
	    	            messages.add("information", message);
	    	            saveMessages(request,messages);
	                   	request.setAttribute("responseUrl","/initListNetServerLogs.do?method=search&netServerId=" + netServerInstanceData.getNetServerId());               		
	               		return mapping.findForward(SUCCESS);
	               		
	               	}else{
	        	        Logger.logError(MODULE, "Error in updating Log Monitor");
	        	        ActionMessage message = new ActionMessage("logmonitor.update.failure");
	        	        ActionMessages messages = new ActionMessages();
	        	        messages.add("information", message);
	        	        saveErrors(request, messages);
	        	        
	                    ActionMessages errorHeadingMessage = new ActionMessages();
	                    message = new ActionMessage("logmonitor.error.heading","updating");
	                    errorHeadingMessage.add("errorHeading",message);
	                    saveMessages(request,errorHeadingMessage);
	                    
	               		return mapping.findForward(FAILURE);
	               	}
			 	}else{
        	        Logger.logError(MODULE, "Error in updating Log Monitor");
        	        ActionMessage message = new ActionMessage("logmonitor.update.failure");
        	        ActionMessages messages = new ActionMessages();
        	        messages.add("information", message);
        	        saveErrors(request, messages);
        	        
                    ActionMessages errorHeadingMessage = new ActionMessages();
                    message = new ActionMessage("logmonitor.error.heading","updating");
                    errorHeadingMessage.add("errorHeading",message);
                    saveMessages(request,errorHeadingMessage);
                    
               		return mapping.findForward(FAILURE);			 		
			 	}
				
		} catch (Exception e) {
			Logger.logDebug(MODULE, "Error while updating Log Monitor, Reason : "+e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("logmonitor.update.failure");
			ActionMessage messageReason = new ActionMessage("logmonitor.update.failure.reason",e.getMessage());			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("logmonitor.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	
	public ActionForward addLogMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter addLogMonitor() method of " + getClass().getName());
		try {
				String logMonitorType = request.getParameter("logMonitorType");
				String expression = request.getParameter("expression");
				String duration   = request.getParameter("duration");
				String startTime  = request.getParameter("startTime");
				String expiryTime = request.getParameter("expiryTime");
				
				LogMonitorData logMonitorData = new LogMonitorData();
				logMonitorData.setLogMonitorType(logMonitorType);
				logMonitorData.setExpression(expression);
				if(null!=duration && 0<duration.trim().length()){
				logMonitorData.setDuration(Long.parseLong(duration));
				}
				logMonitorData.setStartTime(startTime);
				logMonitorData.setExpiryTime(expiryTime);
				
				Logger.logInfo(MODULE,logMonitorData);

				String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
				boolean isLogMonitorCreated = communicationManager.addLogMonitors(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,logMonitorData);				
				
               	if(isLogMonitorCreated){
               		Logger.logError(MODULE, "Log Monitor Created Successfully");
                   	ActionMessage message = new ActionMessage("logmonitor.create.success", "");
    	            ActionMessages messages = new ActionMessages();
    	            messages.add("information", message);
    	            saveMessages(request,messages);
                   	request.setAttribute("responseUrl","/initListNetServerLogs.do?method=search&netServerId=" + netServerInstanceData.getNetServerId());               		
               		return mapping.findForward(SUCCESS);
               		
               	}else{
               		isFirstCall = true;
        	        Logger.logError(MODULE, "Error while creating Log Monitor");
        	        ActionMessage message = new ActionMessage("logmonitor.create.failure");
        	        ActionMessages messages = new ActionMessages();
        	        messages.add("information", message);
        	        saveErrors(request, messages);
        	        
                    ActionMessages errorHeadingMessage = new ActionMessages();
                    message = new ActionMessage("logmonitor.error.heading","creating");
                    errorHeadingMessage.add("errorHeading",message);
                    saveMessages(request,errorHeadingMessage);
                    
               		return mapping.findForward(FAILURE);
               	}
				
		} catch (Exception e) {
			Logger.logDebug(MODULE, "Error while creating Log Monitor, Reason : "+e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("logmonitor.create.failure");
			ActionMessage messageReason = new ActionMessage("logmonitor.create.failure.reason",e.getMessage());			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("logmonitor.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	public ActionForward readLog(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter readLog() method of " + getClass().getName());
		PrintWriter out = null ;
		try {
			out = response.getWriter();
			String isFromTraceLog = request.getParameter("isFromTraceLog");
			Logger.logInfo(MODULE,"isFromTraceLog: "+isFromTraceLog);
			
			if(isFromTraceLog!=null && isFromTraceLog.trim().equalsIgnoreCase("true")){
				String data = null;
				String newFile = null;				
					/* If User close the log window and views the logs again then again reading 
					 * last 50,000 characters of log file. */
					Logger.logInfo(MODULE,"isFirstCall: "+isFirstCall);
					Logger.logInfo(MODULE,"BEFORE bytesRead: "+bytesRead);
					if(isFirstCall==true){
						isFirstCall = false;
						if(bytesRead>0){
							bytesRead = bytesRead-50000;
							if(bytesRead<0){
								bytesRead = 0;
							}
						}
					}
					Logger.logInfo(MODULE,"AFTER bytesRead: "+bytesRead);
					
					Object[] objArgValues = {bytesRead};
					String[] strArgTypes = {"long"};
					List<Object> logDataList = (List<Object>) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION, "retriveMonitorLog",objArgValues,strArgTypes);
					if(logDataList!=null){
						data = (String) logDataList.get(0);
						bytesRead = (Long) logDataList.get(1);
						request.getSession().setAttribute("bytesRead", bytesRead);
						newFile=(String) logDataList.get(2);
						out.println(newFile);
						out.println(data);
					}else{
						out.println("null");
			}
			}
		}catch (Exception commExp) {
			commExp.printStackTrace();
			Logger.logError(MODULE, "Error during Communicating with server , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.server.communicate.fail",netServerInstanceData.getAdminHost());
			ActionMessage messageReason = new ActionMessage("servermgr.server.down");			
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			messages.add("information",messageReason);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}finally{
			if(out!=null){
				out.flush();
				out.close();				
		}
		}
		return null;
	}
	
	private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		};
	};
}
