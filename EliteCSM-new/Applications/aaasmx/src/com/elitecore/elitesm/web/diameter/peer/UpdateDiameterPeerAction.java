/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UpdateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm;

public class UpdateDiameterPeerAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_PEER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterPeerForm diameterPeerForm = (DiameterPeerForm)form;	
			DiameterPeerBLManager blManager = new DiameterPeerBLManager();
			DiameterPeerData diameterPeerData = blManager.getDiameterPeerById(diameterPeerForm.getPeerUUID());
			
			convertFormToBean(diameterPeerForm,diameterPeerData);
			diameterPeerData.setLastModifiedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			staffData.setAuditName(diameterPeerData.getName());
			staffData.setAuditId(diameterPeerData.getAuditUId());
			
			blManager.updateDiameterPeerById(diameterPeerData, staffData);
			
			request.setAttribute("diameterPeerData",diameterPeerData);
			request.setAttribute("diameterPeerForm",diameterPeerForm);
			request.setAttribute("responseUrl", "/initSearchDiameterPeer"); 
			ActionMessage message = new ActionMessage("diameter.peer.update");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch (DuplicateInstanceNameFoundException dpfExp) {
	        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
	        Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
	        ActionMessage message = new ActionMessage("diameter.peer.duplicate");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information",message);
	        saveErrors(request,messages);
	   }catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.peer.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void convertFormToBean(DiameterPeerForm form,DiameterPeerData data) {
		data.setPeerUUID(form.getPeerUUID());
		data.setPeerId(form.getPeerId());
		data.setHostIdentity(form.getHostIdentity());
		data.setRealmName(form.getRealmName());
		data.setRemoteAddress(form.getRemoteAddress());
		data.setLocalAddress(form.getLocalAddress());
		data.setDiameterURIFormat(form.getDiameterURIFormat());
		data.setPeerProfileId(form.getPeerProfileId());
		data.setLastModifiedDate(getCurrentTimeStemp());  
		data.setName(form.getName());
		data.setRequestTimeout(form.getRequestTimeout());
		data.setRetransmissionCount(form.getRetransmissionCount());
		data.setAuditUId(form.getAuditUId());
		if(form.getSecondaryPeerName().isEmpty()){
			data.setSecondaryPeerName(null);
		}else{
			data.setSecondaryPeerName(form.getSecondaryPeerName());
		}
	}
}
