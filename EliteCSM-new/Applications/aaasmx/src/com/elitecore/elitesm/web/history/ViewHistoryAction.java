package com.elitecore.elitesm.web.history;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class ViewHistoryAction extends BaseWebAction{

	private static final String VIEW_FORWARD = "viewHistoryDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DATABASE_DATASOURCE;
	private static final String MODULE = "ViewHistoryAction";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				HistoryBLManager blManager= new HistoryBLManager();
				String strAuditUid = request.getParameter("auditUid");
				String strSytemAuditId=request.getParameter("systemAuditId");
				String name=request.getParameter("name");
				String actionName=request.getParameter("actionName");
				if(strSytemAuditId != null){
					request.setAttribute("systemAuditId", strSytemAuditId);
				}
				
				
				if(Strings.isNullOrEmpty(strAuditUid) == false){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					staffData.setAuditId(strAuditUid);
					staffData.setAuditName(name);
					
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=blManager.getHistoryData(strAuditUid);
					
					request.setAttribute("name", name);
					request.setAttribute("actionName", actionName);
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
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
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
