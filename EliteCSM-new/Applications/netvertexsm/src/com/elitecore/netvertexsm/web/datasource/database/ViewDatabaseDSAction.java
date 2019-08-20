package com.elitecore.netvertexsm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.database.form.ViewDatabaseDSForm;




public class ViewDatabaseDSAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewDatabaseDSDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DATABASE_DATASOURCE;
    private static final String MODULE = "VIEW _DATABASE_DS";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
	  	if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewDatabaseDSForm viewDatabaseDSForm = (ViewDatabaseDSForm)form;
				DatabaseDSBLManager blManager = new DatabaseDSBLManager();
				IDatabaseDSData databaseDSData  = new DatabaseDSData();
				String strdatabaseDSId = request.getParameter("databaseId");
			    Long databaseDSId = Long.parseLong(strdatabaseDSId);
				if(databaseDSId == null){
					databaseDSId = viewDatabaseDSForm.getDatabaseId();
				}
				
				if(databaseDSId!=null ){
					databaseDSData.setDatabaseId(databaseDSId);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					databaseDSData = blManager.getDatabaseDS(databaseDSData,staffData,actionAlias);
					request.setAttribute("databaseDSData",databaseDSData);
					
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                Logger.logTrace(MODULE,e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                ActionMessage message1 = new ActionMessage("databaseds.viewdatasource.failure");
                messages.add("information",message1);
                saveErrors(request,messages);
                
                ActionMessages errorHeadingMessage = new ActionMessages();
                ActionMessage message = new ActionMessage("database.datasource.error.heading","viewing");
                errorHeadingMessage.add("errorHeading",message);
                saveMessages(request,errorHeadingMessage);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
            Logger.logWarn(MODULE, "No Access on this Operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("database.datasource.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}