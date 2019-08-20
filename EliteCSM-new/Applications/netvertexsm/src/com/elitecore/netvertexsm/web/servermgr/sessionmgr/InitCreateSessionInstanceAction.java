package com.elitecore.netvertexsm.web.servermgr.sessionmgr;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.sessionmgr.form.CreateSessionInstanceForm;

public class InitCreateSessionInstanceAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "initCreateSession";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				CreateSessionInstanceForm createSessionForm = (CreateSessionInstanceForm) form;
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();												
				createSessionForm.setDescription(getDefaultDescription(request));		
				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();				
				createSessionForm.setDatabaseDsList(databaseDSList);				
				               			
				return mapping.findForward(CREATE_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
