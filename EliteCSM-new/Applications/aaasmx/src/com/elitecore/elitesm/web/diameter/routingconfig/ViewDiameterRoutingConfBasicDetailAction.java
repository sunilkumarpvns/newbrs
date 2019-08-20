package com.elitecore.elitesm.web.diameter.routingconfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class ViewDiameterRoutingConfBasicDetailAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";
	private static final String VIEWDIAMETERROUTINGCONFBASICDETAIL = "viewDiameterRoutingConfBasicDetail"; 
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
			
			request.setAttribute("diameterRoutingConfData",diameterRoutingConfData);
			request.setAttribute("diameterRoutingConfForm",diameterRoutingConfForm);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEWDIAMETERROUTINGCONFBASICDETAIL);             
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