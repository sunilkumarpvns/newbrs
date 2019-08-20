package com.elitecore.elitesm.web.diameter.routingconfig;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm;

public class UpdateOverloadConfigurationAction extends BaseWebAction {
	private static final String MODULE = "DIAMETER-ROUTING-TABLE";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		SearchDiameterRoutingConfForm diameterRoutingOverloadConfigForm = (SearchDiameterRoutingConfForm)form;
		
		try{
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingTableData diameterRoutingTableData = null;;
			String routingId = request.getParameter("routingTableId");
			if(Strings.isNullOrBlank(routingId) == false) {
					diameterRoutingTableData= diameterRoutingConfBLManager.getDiameterRoutingTableData(routingId);
					
					diameterRoutingTableData.setRoutingTableName(diameterRoutingOverloadConfigForm.getRoutingTableName());
					if(diameterRoutingOverloadConfigForm.getOverloadAction().equals(OverloadAction.REJECT.val)){
						diameterRoutingTableData.setResultCode(diameterRoutingOverloadConfigForm.getResultCode());
					}else{
						diameterRoutingTableData.setResultCode(0);
					}
					
					diameterRoutingTableData.setRoutingScript(diameterRoutingOverloadConfigForm.getRoutingScript());
					diameterRoutingTableData.setOverloadAction(diameterRoutingOverloadConfigForm.getOverloadAction());
					diameterRoutingConfBLManager.updateOverloadConfiguration(diameterRoutingTableData, staffData, ACTION_ALIAS);
					
					request.setAttribute("responseUrl", "/initSearchDiameterRoutingConfig");
					request.setAttribute("searchDiameterRoutingConfForm", diameterRoutingOverloadConfigForm);
					request.setAttribute("routingTableId",routingId);
					request.setAttribute("actionType","tableviewrouting");
					ActionMessage message = new ActionMessage("diameter.routingconf.updatetable.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message=null;
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}


}
