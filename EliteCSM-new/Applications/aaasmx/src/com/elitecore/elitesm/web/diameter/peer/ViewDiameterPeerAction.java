package com.elitecore.elitesm.web.diameter.peer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm;

public class ViewDiameterPeerAction extends BaseWebAction {

	private static final String VIEW_FORWARD = "viewDiameterPeer";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_PEER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			try {
					checkActionPermission(request, ACTION_ALIAS);
					DiameterPeerForm diameterPeerForm = (DiameterPeerForm)form;
					Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
					DiameterPeerBLManager blManager = new DiameterPeerBLManager();
					String peerUUID="";
					String strPeerUUID = request.getParameter("peerUUID");
					if(Strings.isNullOrBlank(strPeerUUID) == false)
					{
						peerUUID = strPeerUUID;	
					}	
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					DiameterPeerData diameterPeerData = blManager.getDiameterPeerById(peerUUID);
					doAuditing(staffData, ACTION_ALIAS);
					request.setAttribute("diameterPeerData",diameterPeerData);
					
			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");                                                         
				ActionMessages messages = new ActionMessages();                                                                                 
				messages.add("information", message);                                                                                           
				saveErrors(request, messages);                                         
				return mapping.findForward(FAILURE_FORWARD);
			}
			return mapping.findForward(VIEW_FORWARD);
	}

}
