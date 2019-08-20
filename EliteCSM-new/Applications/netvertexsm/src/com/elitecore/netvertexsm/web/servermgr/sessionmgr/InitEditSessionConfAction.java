package com.elitecore.netvertexsm.web.servermgr.sessionmgr;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionFieldMapData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.sessionmgr.form.CreateSessionConfForm;

public class InitEditSessionConfAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "initEditSessionConf";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SESSION_MANAGER;
    private static final String MODULE = "EDIT_SESSION_CONFIGURATION_INSTANCE_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
	  	if(checkAccess(request, ACTION_ALIAS)){
			try{
				CreateSessionConfForm createSessionConfForm = (CreateSessionConfForm) form;
				
				SessionBLManager sessionBLManager=new SessionBLManager();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				
				SessionConfData sessionConfData=sessionBLManager.getSessionConfData();
				Iterator<SessionFieldMapData> iterator=sessionConfData.getSessionFieldMapDataset().iterator();
				ArrayList<SessionFieldMapData> coreSessionList=new ArrayList<SessionFieldMapData>();
				ArrayList<SessionFieldMapData> subSessionList=new ArrayList<SessionFieldMapData>();
				
				createSessionConfForm.setSessionConfID(sessionConfData.getSessionConfID());
				createSessionConfForm.setDataSourceID(sessionConfData.getDataSourceID());
				
				if(sessionConfData.getBatchUpdate().equalsIgnoreCase("true") || sessionConfData.getBatchUpdate().equalsIgnoreCase("1")){
					createSessionConfForm.setBatchUpdate("1");
				}else if(sessionConfData.getBatchUpdate().equalsIgnoreCase("false") || sessionConfData.getBatchUpdate().equalsIgnoreCase("0")){
					createSessionConfForm.setBatchUpdate("0");
				}else{
					createSessionConfForm.setBatchUpdate("2");
				}
				createSessionConfForm.setBatchSize(sessionConfData.getBatchSize());
				createSessionConfForm.setBatchUpdateInterval(sessionConfData.getBatchUpdateInterval());
				createSessionConfForm.setDbQueryTimeout(sessionConfData.getDbQueryTimeout());
				createSessionConfForm.setDatabaseDSList(databaseDSBLManager.getDatabaseDSList());
				createSessionConfForm.setSecondaryDataSourceID(sessionConfData.getSecondaryDataSourceID());
				
				while(iterator.hasNext()){
					SessionFieldMapData sessionFieldMapData=iterator.next();
					if(sessionFieldMapData.getDatatype()==0)
						sessionFieldMapData.setDataTypeName("String");
					else
						sessionFieldMapData.setDataTypeName("TimeStamp");
					if(sessionFieldMapData.getType().equalsIgnoreCase("Core"))
						coreSessionList.add(sessionFieldMapData);
					if(sessionFieldMapData.getType().equalsIgnoreCase("SubSession"))
						subSessionList.add(sessionFieldMapData);
				}
				sessionConfData.setCoreSessionList(coreSessionList);
				sessionConfData.setSubSessionList(subSessionList);
				request.setAttribute("sessionConfData", sessionConfData);
				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("driver.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("session.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);
	            return mapping.findForward(FAILURE_FORWARD);
			} 
		}else{
            Logger.logWarn(MODULE, "No Access on this Operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("session.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}

