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

import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionFieldMapData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class ViewSessionConfAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewSessionConf";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_SESSION_MANAGER;
    private static final String MODULE = "VIEW_SESSION_CONFIGURATION_INSTANCE_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
	  	if(checkAccess(request, ACTION_ALIAS)){
			try{
				
				SessionBLManager sessionBLManager=new SessionBLManager();
			
				SessionConfData sessionConfData=sessionBLManager.getSessionConfData();
				if(sessionConfData==null){
					sessionConfData=new SessionConfData();
					sessionConfData.setSessionConfID(1);
					sessionConfData.setDataSourceID(null);
					sessionConfData.setBatchUpdate("false");
					sessionConfData.setBatchSize(100);
					sessionConfData.setBatchUpdateInterval(1);
					
					sessionBLManager.createSessionConfiguration(sessionConfData);
					sessionConfData=sessionBLManager.getSessionConfData();
				}
				DatabaseDSData databaseDSData =sessionBLManager.getDatabaseDS(sessionConfData.getDataSourceID());
				if(databaseDSData==null){
					databaseDSData=new DatabaseDSData();
					databaseDSData.setName("");
				}
				//sessionConfData.setDatabaseDS(databaseDSData);
				
				Iterator<SessionFieldMapData> iterator=sessionConfData.getSessionFieldMapDataset().iterator();
				ArrayList<SessionFieldMapData> coreSessionList=new ArrayList<SessionFieldMapData>();
				ArrayList<SessionFieldMapData> subSessionList=new ArrayList<SessionFieldMapData>();
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
	            messages.add("information", new ActionMessage("session.config.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("session.error.heading","viewing");
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
            message = new ActionMessage("session.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            	            
	        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}

