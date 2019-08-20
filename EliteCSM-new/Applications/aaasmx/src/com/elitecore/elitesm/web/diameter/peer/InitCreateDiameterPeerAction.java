/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateDiameterpolicyAction.java                 		
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
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm;

public class InitCreateDiameterPeerAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String INITCREATEDIAMETERPEER = "initCreateDiameterPeer"; 
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_PEER; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
	try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());  
			DiameterPeerForm diameterPeerForm = (DiameterPeerForm)form; 
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			DiameterPeerBLManager blManager = new DiameterPeerBLManager();
			if(request.getParameter("hostIdentity")!=null && !request.getParameter("hostIdentity").trim().equalsIgnoreCase(""))
			{
				diameterPeerForm.setHostIdentity(request.getParameter("hostIdentity")); 
				diameterPeerForm.setRemoteAddress(request.getParameter("hostIdentity"));
			}
			if(request.getParameter("peerProfileId")!=null && !request.getParameter("peerProfileId").equalsIgnoreCase("0"))
			{
				diameterPeerForm.setPeerProfileId(request.getParameter("peerProfileId"));
			}
			
			diameterPeerForm.setSecondaryPeerList(blManager.getDiameterPeerList());
			diameterPeerForm.setPeerProfileList(blManager.getPeerProfileList());
			request.setAttribute("diameterPeerForm", diameterPeerForm);
		return mapping.findForward(INITCREATEDIAMETERPEER);
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
