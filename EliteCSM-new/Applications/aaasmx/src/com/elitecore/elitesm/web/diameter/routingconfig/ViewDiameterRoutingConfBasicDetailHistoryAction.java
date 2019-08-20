package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterRoutingConfBasicDetailHistoryAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";
	private static final String VIEW_HISTORY_ACTION = "viewDiameterRoutingConfBasicDetailHistory"; 
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try {	
			checkActionPermission(request,ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DiameterRoutingConfBLManager diamterRoutingConfBLManager = new DiameterRoutingConfBLManager();			 
			DiameterRoutingConfData diameterRoutingConfData = diamterRoutingConfBLManager.getDiameterRoutingConfData(diameterRoutingConfForm.getRoutingConfigId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strRoutingConfId = request.getParameter("routingConfigId");
			String routingConfId = strRoutingConfId;
			if(Strings.isNullOrBlank(routingConfId) == true){
				routingConfId = diameterRoutingConfForm.getRoutingConfigId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(routingConfId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(diameterRoutingConfData.getName());
				staffData.setAuditId(diameterRoutingConfData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			
			request.setAttribute("diameterRoutingConfData",diameterRoutingConfData);
			request.setAttribute("diameterRoutingConfForm",diameterRoutingConfForm);
			return mapping.findForward(VIEW_HISTORY_ACTION);             
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.routingconf.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}                    
		return mapping.findForward(FAILURE_FORWARD); 
	}
}