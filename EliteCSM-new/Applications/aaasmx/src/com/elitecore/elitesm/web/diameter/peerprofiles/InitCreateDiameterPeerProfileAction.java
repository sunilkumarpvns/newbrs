/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm;

public class InitCreateDiameterPeerProfileAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPEERPROFILE";
	private static final String INITCREATEDIAMETERPEERPROFILE = "initCreateDiameterPeerProfile"; 
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_PEER_PROFILE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm)form; 
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			if(request.getParameter("profileName")!=null && !request.getParameter("profileName").trim().equalsIgnoreCase(""))
			{
				diameterPeerProfileForm.setProfileName(request.getParameter("profileName")); 	  
			}
			diameterPeerProfileForm.setDescription(getDefaultDescription(userName));
			diameterPeerProfileForm.setDwrDuration(Long.parseLong("6")); 
			diameterPeerProfileForm.setInitConnectionDuration(Long.parseLong("60"));
			diameterPeerProfileForm.setRetryCount(Long.parseLong("0"));  
			DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
			diameterPeerProfileForm.setSessionCleanUpCER(Boolean.TRUE);
			diameterPeerProfileForm.setSessionCleanUpDPR(Boolean.TRUE);
			List<ServerCertificateData> serverCertificateList = blManager.getListOfServerCertificate();
			List<String> collectionList=new ArrayList<String>();
			
			SecurityStandard[] securityStandardList=SecurityStandard.values();
			for(SecurityStandard securityStandard:securityStandardList){
				collectionList.add(securityStandard.val);
			}
			Collections.sort(collectionList);
			
			List<CipherSuites> cipherSuitesList=new ArrayList<CipherSuites>();
			
			TLSVersion minTLSVer=TLSVersion.fromVersion("TLSv1");
			TLSVersion maxTLSVer=TLSVersion.fromVersion("TLSv1");
			
			cipherSuitesList=CipherSuites.getSupportedCipherSuites(minTLSVer,maxTLSVer);
			
			diameterPeerProfileForm.setLstConnectionStandard(collectionList);
			diameterPeerProfileForm.setServerCertificateDataList(serverCertificateList);
			
			request.setAttribute("diameterPeerProfileForm", diameterPeerProfileForm);
			request.setAttribute("cipherSuitList", cipherSuitesList);
		return mapping.findForward(INITCREATEDIAMETERPEERPROFILE);
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
