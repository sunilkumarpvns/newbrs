/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitUpdateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm;

public class InitUpdateDiameterPeerProfileAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER-PROFILE";
	private static final String INITUPDATEDIAMETERPEERPROFILE = "initUpdateDiameterPeerProfile"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_PEER_PROFILE; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm)form;

				DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();			 
				
				DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(diameterPeerProfileForm.getPeerProfileId());
				
				setFormData(diameterPeerProfileForm,diameterPeerProfileData);
		 		request.setAttribute("diameterPeerProfileData",diameterPeerProfileData);
				request.setAttribute("diameterPeerProfileForm",diameterPeerProfileForm);
				
				List<ServerCertificateData> serverCertificateList = blManager.getListOfServerCertificate();
				List<String> collectionList=new ArrayList<String>();
				
	 			Collection<TLSVersion> tlsVersionList= Arrays.asList(TLSVersion.values());
	 			List<String> lstTlsVersionList=new ArrayList<String>();
	 			
	 			if(serverCertificateList != null && serverCertificateList.size() > 0){
					for(int i=0;i<serverCertificateList.size();i++){
						ServerCertificateData tempServerCertificateData = (ServerCertificateData) serverCertificateList.get(i);
						if(tempServerCertificateData.getServerCertificateId() == diameterPeerProfileData.getServerCertificateId()){
							diameterPeerProfileForm.setServerCertificateName(tempServerCertificateData.getServerCertificateName());
						}
					}
				}
	 			
	 			if(tlsVersionList !=null && tlsVersionList.size() > 0){
		 			for(TLSVersion tlsVersion:tlsVersionList){
						lstTlsVersionList.add(tlsVersion.version);
					}
		 			
		 			Collections.sort(lstTlsVersionList);
					
		 			if(lstTlsVersionList !=null){
						diameterPeerProfileForm.setLstTLSVersion(lstTlsVersionList);
					}
	 			}
				
				SecurityStandard[] securityStandardList=SecurityStandard.values();
				
				if(securityStandardList !=null && securityStandardList.length > 0){
					for(SecurityStandard securityStandard:securityStandardList){
						collectionList.add(securityStandard.val);
					}
					Collections.sort(collectionList);
					diameterPeerProfileForm.setLstConnectionStandard(collectionList);
				}
				
				Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
				List<String> cipherList=new ArrayList<String>();
			
				if(cipherSuites != null && cipherSuites.size() > 0){
					for(CipherSuites cipherSuites2:cipherSuites){
						CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
						cipherList.add(strCipherSuitesName.name());
					}
				}
				
				diameterPeerProfileForm.setServerCertificateDataList(serverCertificateList);
				
				if(diameterPeerProfileData.getCiphersuiteList() !=null){
					List<String> cipherSuiteList = convertcipherSuiteListToLabel(diameterPeerProfileData.getCiphersuiteList());
					
					request.setAttribute("cipherSuiteList",cipherSuiteList);
					diameterPeerProfileForm.setLstCipherSuitesList(cipherSuiteList);
					
					if(cipherList != null){
						diameterPeerProfileForm.setCipherSuitList(cipherList);
					}
					
					cipherList.removeAll(cipherSuiteList);
					diameterPeerProfileForm.setRemainingCipherList(cipherList);
					
				}else{
					diameterPeerProfileForm.setCipherSuitList(cipherList);
				}
				
				request.setAttribute("diameterPeerProfileForm", diameterPeerProfileForm);
				return mapping.findForward(INITUPDATEDIAMETERPEERPROFILE);       

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
	
	private void setFormData(DiameterPeerProfileForm form,DiameterPeerProfileData data) {
		form.setPeerProfileId(data.getPeerProfileId());
		form.setProfileName(data.getProfileName());
		form.setDescription(data.getDescription());
		form.setExclusiveAuthAppIds(data.getExclusiveAuthAppIds());
		form.setExclusiveAcctAppIds(data.getExclusiveAcctAppIds());
		form.setTransportProtocol(data.getTransportProtocol());
		form.setSocketReceiveBufferSize(data.getSocketReceiveBufferSize());
		form.setSocketSendBufferSize(data.getSocketSendBufferSize());
		form.setTcpNagleAlgorithm(data.getTcpNagleAlgorithm());
		form.setDwrDuration(data.getDwrDuration());
		form.setInitConnectionDuration(data.getInitConnectionDuration());
		form.setSessionCleanUpCER(Boolean.valueOf(data.getSessionCleanUpCER()));
		form.setSessionCleanUpDPR(Boolean.valueOf(data.getSessionCleanUpDPR()));
		form.setRedirectHostAvpFormat(data.getRedirectHostAvpFormat());
		form.setRetryCount(data.getRetryCount());
		form.setCerAvps(data.getCerAvps());
		form.setDprAvps(data.getDprAvps());
		form.setDwrAvps(data.getDwrAvps());
		form.setSendDPRCloseEvent(data.getSendDPRCloseEvent());
		form.setFollowRedirection(data.getFollowRedirection());
		form.setHotlinePolicy(data.getHotlinePolicy());
		form.setMinTlsVersion(data.getMinTlsVersion());
		form.setMaxTlsVersion(data.getMaxTlsVersion());
		form.setServerCertificateId(data.getServerCertificateId());
		form.setClientCertificateRequest(data.getClientCertificateRequest());
		form.setValidateCertificateExpiry(data.getValidateCertificateExpiry());
		form.setAllowCertificateCA(data.getAllowCertificateCA());
		form.setValidateCertificateRevocation(data.getValidateCertificateRevocation());
		form.setValidateHost(data.getValidateHost());
		form.setSecurityStandard(data.getSecurityStandard());
		form.setAuditUId(data.getAuditUId());
		form.setHaIPAddress(data.getHaIPAddress());
		form.setDhcpIPAddress(data.getDhcpIPAddress());
		
		/*
		 * set cipher suite list
		 *
		 * TLS_RSA_WITH_3DES_EDE_CBC_SHA=10
		 * TLS_RSA_WITH_AES_128_CBC_SHA=47
		 * TLS_RSA_WITH_DES_CBC_SHA=9 
		 */

		if(data.getCiphersuiteList() !=null){
			String cipherSuitString = data.getCiphersuiteList();
			String[] cipherSuiteArray=cipherSuitString.split(",");
			List<String> cipherSuitValueStrings =new ArrayList<String>() ;
			Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
			String strCipherSuit="";
			for (int i = 0; i < cipherSuiteArray.length; i++) {
					if(cipherSuiteArray[i] != null || cipherSuiteArray[i] != ""){
						for(CipherSuites cipherSuites2:cipherSuites){
							CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
							String code=String.valueOf(strCipherSuitesName.code);
							if(code.equals(cipherSuiteArray[i])){
								cipherSuitValueStrings.add(strCipherSuitesName.name());
								if(strCipherSuit == ""){
									strCipherSuit=strCipherSuitesName.name();
								}else{
									strCipherSuit=strCipherSuit+","+strCipherSuitesName.name();
								}
							}
						}
					}
				}
			form.setLstCipherSuitesList(cipherSuitValueStrings);
			form.setCipherSuites(strCipherSuit);
		}
		
		
		
	} 
	protected List<String> convertcipherSuiteListToLabel(String ciphersuiteList) {
		String[] cipherSuiteValueArray = ciphersuiteList.split(",");
		List<String> cipherSuiteLabelArray = new ArrayList<String>();
		Collection<CipherSuites> cipherSuites= Arrays.asList(CipherSuites.values());
		
		for(int i=0;i<cipherSuiteValueArray.length;i++){
				for(CipherSuites cipherSuites2:cipherSuites){
					CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
					String strCode=String.valueOf(cipherSuites2.code);
					if(strCode.equals(cipherSuiteValueArray[i])){
						cipherSuiteLabelArray.add(strCipherSuitesName.name());
					}
				}
			}
		return cipherSuiteLabelArray;
	}
}
