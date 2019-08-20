package com.elitecore.elitesm.web.diameter.diameterconcurrency;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterPolicyGroupHistoryAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewDiameterConcurrencyHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_CONCURRENCY;
	private static final String MODULE = "ViewDiameterPolicyGroupHistoryAction";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)form;
				DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
				
				DiameterConcurrencyData diameterConcurrencyData = new DiameterConcurrencyData();
				String strDiaConConfigId = request.getParameter("diaConConfigId");
				String diaConConfigId = strDiaConConfigId;
				if(Strings.isNullOrBlank(diaConConfigId) == true){
					diaConConfigId = diameterConcurrencyForm.getDiaConConfigId();
				}

				if( Strings.isNullOrBlank(diaConConfigId) == false ){
					diameterConcurrencyData.setDiaConConfigId(diaConConfigId);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					diameterConcurrencyData = blManager.getDiameterConcurrencyDataById(diaConConfigId);
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String name=request.getParameter("name");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(diaConConfigId != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(diameterConcurrencyData.getName());
						staffData.setAuditId(diameterConcurrencyData.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					request.setAttribute("diameterConcurrencyData",diameterConcurrencyData);
				}

				return mapping.findForward(VIEW_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("diameterconcurrency.view.failure");
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
