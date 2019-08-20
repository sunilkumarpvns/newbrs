/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UpdateDiameterpolicyAction.java                 		
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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm;

public class UpdateDiameterPeerProfileAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER-PROFILE";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_PEER_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm)form;	
			DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
			
			String stringsArray=diameterPeerProfileForm.getCipherSuites();
			String [] paramArray=stringsArray.split(",");
 			diameterPeerProfileForm.setListCipherSuites(paramArray);
			
 			DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(diameterPeerProfileForm.getPeerProfileId());

			convertFormToBean(diameterPeerProfileForm,diameterPeerProfileData);
			diameterPeerProfileData.setLastModifiedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			staffData.setAuditName(diameterPeerProfileData.getProfileName());
			staffData.setAuditId(diameterPeerProfileData.getAuditUId());

			blManager.updateDiameterPeerProfileById(diameterPeerProfileData,staffData);

			request.setAttribute("diameterPeerProfileData",diameterPeerProfileData);
			request.setAttribute("diameterPeerProfileForm",diameterPeerProfileForm);
			request.setAttribute("responseUrl", "/viewDiameterPeerProfile.do?peerProfileId="+diameterPeerProfileData.getPeerProfileId());
			ActionMessage message = new ActionMessage("diameter.peerprofile.update");
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
	        ActionMessage message = new ActionMessage("diameter.peerprofile.duplicate");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information",message);
	        saveErrors(request,messages);
	   }catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.peerprofile.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void convertFormToBean(DiameterPeerProfileForm form,DiameterPeerProfileData data) {
		data.setPeerProfileId(form.getPeerProfileId());
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
		}else{
			data.setSocketSendBufferSize(-1);
		}
		data.setTcpNagleAlgorithm(form.getTcpNagleAlgorithm().toString());
		data.setRedirectHostAvpFormat(form.getRedirectHostAvpFormat().toString());
		data.setDwrDuration(form.getDwrDuration());
		data.setInitConnectionDuration(form.getInitConnectionDuration());
		data.setRetryCount(form.getRetryCount());
		data.setSessionCleanUpCER(form.getSessionCleanUpCER().toString());
		data.setSessionCleanUpDPR(form.getSessionCleanUpDPR().toString());
		data.setCerAvps(form.getCerAvps());
		data.setDprAvps(form.getDprAvps());
		data.setDwrAvps(form.getDwrAvps());
		data.setSendDPRCloseEvent(form.getSendDPRCloseEvent());
		data.setLastModifiedDate(getCurrentTimeStemp());
		data.setFollowRedirection(form.getFollowRedirection().toString());
		data.setHotlinePolicy(form.getHotlinePolicy().trim());
		data.setMinTlsVersion(form.getMinTlsVersion());
		data.setMaxTlsVersion(form.getMaxTlsVersion());
		data.setServerCertificateId(form.getServerCertificateId());
		data.setClientCertificateRequest(form.getClientCertificateRequest());
		data.setValidateCertificateExpiry((form.getValidateCertificateExpiry()) != null ? form.getValidateCertificateExpiry(): "false");
		data.setValidateCertificateRevocation(form.getValidateCertificateRevocation() != null ? form.getValidateCertificateRevocation():"false");
		data.setAllowCertificateCA((form.getAllowCertificateCA()) != null ? form.getAllowCertificateCA(): "false");
		data.setValidateHost((form.getValidateHost()) !=null ? form.getValidateHost():"false");
		if(Strings.isNullOrBlank(form.getServerCertificateId()) == false && ("0".equals(form.getServerCertificateId()) == false)){
			data.setServerCertificateId(form.getServerCertificateId());
		}else{
			data.setServerCertificateId(null);
		}
		data.setAuditUId(form.getAuditUId());
		data.setHaIPAddress(form.getHaIPAddress());
		data.setDhcpIPAddress(form.getDhcpIPAddress());
		
		StringBuffer strCipherSuiteList = new StringBuffer();
		
		String[] strCiphersuit=form.getListCipherSuites();
		String[] strcipherSuitArray =new String[4000];
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		
		for(int i=0;i<strCiphersuit.length;i++){
			if(strCiphersuit[i] != null || strCiphersuit[i] != " "){
				for(CipherSuites cipherSuites2:cipherSuites){
					CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
					if(strCipherSuitesName.name().trim().equals(strCiphersuit[i].trim())){
						strcipherSuitArray[i]=String.valueOf(cipherSuites2.code);
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
		data.setSecurityStandard(form.getSecurityStandard());
		data.setServerCertificateName(form.getServerCertificateName());
    }
}
