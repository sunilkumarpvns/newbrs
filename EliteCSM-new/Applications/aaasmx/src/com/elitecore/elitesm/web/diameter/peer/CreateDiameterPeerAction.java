/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer; 

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm;

public class CreateDiameterPeerAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_PEER; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterPeerForm diameterPeerForm = (DiameterPeerForm)form; 
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			DiameterPeerData diameterPeerData = new DiameterPeerData();
			convertFormToBean(diameterPeerForm,diameterPeerData);
			diameterPeerData.setCreatedByStaffId(currentUser);
			diameterPeerData.setLastModifiedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			diameterPeerBLManager.createDiameterPeer(diameterPeerData, staffData);
			request.setAttribute("responseUrl", "/initSearchDiameterPeer");    
			ActionMessage message = new ActionMessage("diameter.peer.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			return mapping.findForward(SUCCESS_FORWARD);             
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.peer.create.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void convertFormToBean(DiameterPeerForm form,DiameterPeerData data) {
		data.setHostIdentity(form.getHostIdentity());
		data.setRealmName(form.getRealmName());
		data.setRemoteAddress(form.getRemoteAddress());
		data.setLocalAddress(form.getLocalAddress());
		data.setDiameterURIFormat(form.getDiameterURIFormat());
		data.setPeerProfileId(form.getPeerProfileId());
		data.setCreateDate(getCurrentTimeStemp());
		data.setLastModifiedDate(getCurrentTimeStemp());  
		data.setName(form.getName());
		data.setRequestTimeout(form.getRequestTimeout());
		data.setRetransmissionCount(form.getRetransmissionCount());
		
		if(form.getSecondaryPeerName().isEmpty()){
			data.setSecondaryPeerName(null);
		}else{
			data.setSecondaryPeerName(form.getSecondaryPeerName());
		}
	}           
}
