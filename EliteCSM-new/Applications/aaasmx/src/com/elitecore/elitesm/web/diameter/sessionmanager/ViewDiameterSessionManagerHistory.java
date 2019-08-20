package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterSessionManagerHistory extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewDiameterSessionManagerHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewCGPolicyDetails";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_DIAMETER_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm)form;
				DiameterSessionManagerBLManager diameterSessionManagerBLManager = new  DiameterSessionManagerBLManager();
				
				String strSessionManagerID = request.getParameter("sessionManagerId");
				String sessionManagerId;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				
				if(strSessionManagerID != null){
					sessionManagerId =strSessionManagerID;
				}else{
					sessionManagerId=updateDiameterSessionManagerForm.getSessionManagerId();
				}
				
				if(Strings.isNullOrBlank(strSessionManagerID) == false){
					DiameterSessionManagerData data = new DiameterSessionManagerData();
					data.setSessionManagerId(sessionManagerId);
					data = diameterSessionManagerBLManager.getDiameterSessionManagerDataById(sessionManagerId);					
					
					HistoryBLManager historyBlManager= new HistoryBLManager();

					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String name=request.getParameter("name");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(sessionManagerId != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(data.getName());
						staffData.setAuditId(data.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					
					request.setAttribute("diameterSessionManagerData",data);
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
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
