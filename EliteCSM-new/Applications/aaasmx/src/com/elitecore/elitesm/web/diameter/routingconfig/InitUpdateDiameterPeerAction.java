package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class InitUpdateDiameterPeerAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String INITUPDATEDIAMETERPEER = "initUpdateDiameterRoutingConfPeer"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form; 
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
			diameterRoutingConfForm.setDiameterPeersList(diameterPeerDataList);
			
			DiameterRoutingConfBLManager diamterRoutingConfBLManager = new DiameterRoutingConfBLManager();			 
			DiameterRoutingConfData diameterRoutingConfData = diamterRoutingConfBLManager.getDiameterRoutingConfData(diameterRoutingConfForm.getRoutingConfigId());
			
			request.setAttribute("diameterRoutingConfData",diameterRoutingConfData);
			request.setAttribute("diameterRoutingConfForm", diameterRoutingConfForm);
			return mapping.findForward(INITUPDATEDIAMETERPEER);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}                                                                                           
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
