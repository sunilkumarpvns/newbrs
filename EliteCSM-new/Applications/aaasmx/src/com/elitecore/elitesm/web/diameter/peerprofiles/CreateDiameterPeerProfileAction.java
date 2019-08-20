/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles; 

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm;

public class CreateDiameterPeerProfileAction extends BaseWebAction{ 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPEERPROFILE";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_PEER_PROFILE; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm)form; 
			DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
			DiameterPeerProfileData diameterPeerProfileData = new DiameterPeerProfileData();
			
			String stringsArray=diameterPeerProfileForm.getCipherSuites();
			String [] paramArray=stringsArray.split(",");
 			diameterPeerProfileForm.setListCipherSuites(paramArray);
 			
			convertFromToBean(diameterPeerProfileForm,diameterPeerProfileData);
			diameterPeerProfileData.setLastModifiedByStaffId(currentUser);
			diameterPeerProfileData.setCreatedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			diameterPeerProfileBLManager.createDiameterPeerProfile(diameterPeerProfileData,staffData);

			request.setAttribute("responseUrl", "/initSearchDiameterPeerProfiles");    
			ActionMessage message = new ActionMessage("diameter.peerprofile.create");
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
			ActionMessage message = new ActionMessage("diameter.peerprofile.create.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void convertFromToBean(DiameterPeerProfileForm form,DiameterPeerProfileData data) {
		data.setProfileName(form.getProfileName());
		data.setDescription(form.getDescription());
		data.setExclusiveAuthAppIds(form.getExclusiveAuthAppIds());
		data.setExclusiveAcctAppIds(form.getExclusiveAcctAppIds());
		data.setTransportProtocol(form.getTransportProtocol());
		if(form.getSocketReceiveBufferSize() != null){
			data.setSocketReceiveBufferSize(form.getSocketReceiveBufferSize());
		}else{
			data.setSocketReceiveBufferSize(-1);
		}
		if(form.getSocketSendBufferSize() != null){
			data.setSocketSendBufferSize(form.getSocketSendBufferSize());
		} else{
			data.setSocketSendBufferSize(-1);
		}
		data.setTcpNagleAlgorithm(form.getTcpNagleAlgorithm());
		data.setDwrDuration(form.getDwrDuration());
		data.setInitConnectionDuration(form.getInitConnectionDuration());
		data.setRetryCount(form.getRetryCount());
		data.setRedirectHostAvpFormat(form.getRedirectHostAvpFormat().toString());
		data.setSessionCleanUpCER(form.getSessionCleanUpCER().toString());
		data.setSessionCleanUpDPR(form.getSessionCleanUpDPR().toString());
		data.setCerAvps(form.getCerAvps());
		data.setDprAvps(form.getDprAvps());
		data.setDwrAvps(form.getDwrAvps());
		data.setSendDPRCloseEvent(form.getSendDPRCloseEvent());
		data.setCreateDate(getCurrentTimeStemp());
		data.setLastModifiedDate(getCurrentTimeStemp());
		data.setFollowRedirection(form.getFollowRedirection());
		data.setHotlinePolicy(form.getHotlinePolicy().trim());
		data.setMinTlsVersion(form.getMinTlsVersion());
		data.setMaxTlsVersion(form.getMaxTlsVersion());
		if(Strings.isNullOrBlank(form.getServerCertificateId()) == false && ("0".equals(form.getServerCertificateId()) == false)){
			data.setServerCertificateId(form.getServerCertificateId());
		}else{
			data.setServerCertificateId(null);
		}
		data.setClientCertificateRequest(form.getClientCertificateRequest());
		data.setValidateCertificateExpiry((form.getValidateCertificateExpiry()) != null ? form.getValidateCertificateExpiry(): "false");
		data.setValidateCertificateRevocation(form.getValidateCertificateRevocation() != null ? form.getValidateCertificateRevocation():"false");
		data.setAllowCertificateCA((form.getAllowCertificateCA()) != null ? form.getAllowCertificateCA(): "false");
		data.setValidateHost((form.getValidateHost()) !=null ? form.getValidateHost():"false");
		data.setSecurityStandard(form.getSecurityStandard());
		/*
		 * build cipher suite list
		 *
		 * TLS_RSA_WITH_3DES_EDE_CBC_SHA=10
		 * TLS_RSA_WITH_AES_128_CBC_SHA=47
		 * TLS_RSA_WITH_DES_CBC_SHA=9 
		 */
		
		StringBuffer strCipherSuiteList = new StringBuffer();
		
		String[] strCiphersuit=form.getListCipherSuites();
		String[] strcipherSuitArray =new String[4000];
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		
		for(int i=0;i<strCiphersuit.length;i++){
			if(strCiphersuit[i] != null || strCiphersuit[i] != " "){
				for(CipherSuites cipherSuit:cipherSuites){
					if(strCiphersuit[i].trim().equals(cipherSuit.name())){
						strcipherSuitArray[i]=String.valueOf(cipherSuit.code);
					}
				}
			}
		}
		
		
		for (int i = 0; i < strcipherSuitArray.length; i++) {
			if(strcipherSuitArray[i] != null){
				if(strCipherSuiteList.length()>0){
			   		strCipherSuiteList.append(",");
			   		strCipherSuiteList.append(strcipherSuitArray[i]);
			   	}else{
			   		strCipherSuiteList.append(strcipherSuitArray[i]);
			   	}
			}
		}
		data.setCiphersuiteList(strCipherSuiteList.toString());
		data.setServerCertificateName(form.getServerCertificateName());
		data.setDhcpIPAddress(form.getDhcpIPAddress());
		data.setHaIPAddress(form.getHaIPAddress());
	}           
}
