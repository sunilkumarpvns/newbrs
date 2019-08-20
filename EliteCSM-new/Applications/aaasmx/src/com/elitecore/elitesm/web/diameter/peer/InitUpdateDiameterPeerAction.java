/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitUpdateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer; 

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm;

public class InitUpdateDiameterPeerAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String INITUPDATEDIAMETERPEER = "initUpdateDiameterPeer"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_PEER; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			DiameterPeerForm diameterPeerForm = (DiameterPeerForm)form;

				DiameterPeerBLManager blManager = new DiameterPeerBLManager();			 
				DiameterPeerData diameterPeerData = blManager.getDiameterPeerById(diameterPeerForm.getPeerUUID());
				setFormData(diameterPeerForm,diameterPeerData);
				diameterPeerForm.setPeerProfileList(blManager.getPeerProfileList());
		 
				List<DiameterPeerData> secondaryPeerList = blManager.getDiameterPeerList();
				int peerListSize = secondaryPeerList.size();
				
				for (int i = 0; i < peerListSize; i++) {
					DiameterPeerData diameterPeer = secondaryPeerList.get(i);
					
					if(diameterPeerData.getName().equals(diameterPeer.getName())){
						secondaryPeerList.remove(i);
						break;
					}
				}
				diameterPeerForm.setSecondaryPeerList(secondaryPeerList);
				
				request.setAttribute("diameterPeerData",diameterPeerData);
				request.setAttribute("diameterPeerForm",diameterPeerForm);
				return mapping.findForward(INITUPDATEDIAMETERPEER);       

		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("general.error");                                                         
			messages.add("information", message);                                                                                           
		}                    
		saveErrors(request, messages); 
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void setFormData(DiameterPeerForm form,DiameterPeerData data) {
	    form.setPeerId(data.getPeerId());
	    form.setPeerUUID(data.getPeerUUID());
		form.setHostIdentity(data.getHostIdentity());
		form.setRealmName(data.getRealmName());
		form.setDiameterURIFormat(data.getDiameterURIFormat());
		form.setRemoteAddress(data.getRemoteAddress());
		form.setLocalAddress(data.getLocalAddress());
		form.setPeerProfileId(data.getPeerProfileId());
		form.setName(data.getName());
		form.setRequestTimeout(data.getRequestTimeout());
		form.setRetransmissionCount(data.getRetransmissionCount());
		form.setAuditUId(data.getAuditUId());
		form.setSecondaryPeerName(data.getSecondaryPeerName());
	}
	 
}
