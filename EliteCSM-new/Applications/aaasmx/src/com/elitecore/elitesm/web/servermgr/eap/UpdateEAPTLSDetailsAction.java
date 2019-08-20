package com.elitecore.elitesm.web.servermgr.eap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.TLSVersion;
import  com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.VendorSpecificCertificateData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.UpdateEAPTLSDetailsForm;

public class UpdateEAPTLSDetailsAction extends BaseEAPConfigAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateEapTlsConfigData";

	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EAP_CONFIGURATION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());

			EAPConfigBLManager eapconfigConfigBLManager = new EAPConfigBLManager();

			String strEapId = request.getParameter("eapId");
			UpdateEAPTLSDetailsForm updateEAPTLSDetailsForm =(UpdateEAPTLSDetailsForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			String eapId;
			if(Strings.isNullOrBlank(strEapId) == false){
				eapId=strEapId;
			}else{
				eapId=updateEAPTLSDetailsForm.getEapId();
			}
			String type="";

			Logger.logDebug(MODULE,request.getParameter("typeVal"));

			if(request.getParameter("typeVal") != null){


				if("ttls".equals(request.getParameter("typeVal"))){
					type="TTLS";


				}else if("tls".equals(request.getParameter("typeVal"))){
					type="TLS";

				}else if("peap".equals(request.getParameter("typeVal"))){
					type="PEAP";

				}


			}



			Logger.logDebug(MODULE, "TYpe is:"+type);
			try {

				if(updateEAPTLSDetailsForm.getAction() == null){
					if(eapId != null){
						EAPConfigData eapConfigData = new EAPConfigData();

						eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);

						String[] vendoridentifiers = null;
						String[] serverCerticateIdForVSC = null;

						if(eapConfigData.getEaptlsConfigData() != null){

							/*
							 * set VendorSpecific Certificate List
							 */
							List<VendorSpecificCertificateData> vendorSpecificCertList=eapConfigData.getEaptlsConfigData().getVendorSpecificList();

							
							for(VendorSpecificCertificateData vendorSpecificCertificateData:vendorSpecificCertList){
								if(vendorSpecificCertificateData.getServerCertificateIdForVSC() == null){
									vendorSpecificCertificateData.setServerCertificateIdForVSC("");
								}
							}
							request.setAttribute("vendorSpecificCertList", vendorSpecificCertList);
							
							List<ServerCertificateData> serverCertificateList = eapconfigConfigBLManager.getListOfServerCertificate();
							List<String> collectionList=new ArrayList<String>();
							
				 			Collection<TLSVersion> tlsVersionList=Arrays.asList(TLSVersion.values());
				 			List<String> lstTlsVersionList=new ArrayList<String>();
				 			
				 			if(serverCertificateList != null && serverCertificateList.size() > 0){
								for(int i=0;i<serverCertificateList.size();i++){
									ServerCertificateData tempServerCertificateData = (ServerCertificateData) serverCertificateList.get(i);
									if(tempServerCertificateData.getServerCertificateId().equals(eapConfigData.getEaptlsConfigData().getServerCertificateId())){
										updateEAPTLSDetailsForm.setServerCertificateName(tempServerCertificateData.getServerCertificateName());
									}
								}
							}
				 			
				 			if(tlsVersionList !=null && tlsVersionList.size() > 0){
					 			for(TLSVersion tlsVersion:tlsVersionList){
									lstTlsVersionList.add(tlsVersion.version);
								}
					 			
					 			Collections.sort(lstTlsVersionList);
								
					 			if(lstTlsVersionList !=null){
					 				updateEAPTLSDetailsForm.setLstTLSVersion(lstTlsVersionList);
								}
				 			}
				 			
				 			CipherSuites[] cipherSuites= CipherSuites.values();
							List<String> cipherList=new ArrayList<String>();
						
							if(cipherSuites != null && cipherSuites.length > 0){
								for(CipherSuites cipherSuites2:cipherSuites){
									CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
									cipherList.add(strCipherSuitesName.name());
								}
							}
							
							updateEAPTLSDetailsForm.setServerCertificateDataList(serverCertificateList);
							request.setAttribute("serverCertificateDataList", serverCertificateList);
							
							if(eapConfigData.getEaptlsConfigData().getCiphersuiteList() !=null){
								List<String> cipherSuiteList = convertcipherSuiteListToString(eapConfigData.getEaptlsConfigData().getCiphersuiteList());
								
								request.setAttribute("cipherSuiteList",cipherSuiteList);
								updateEAPTLSDetailsForm.setLstCipherSuitesList(cipherSuiteList);
								
								if(cipherList != null){
									updateEAPTLSDetailsForm.setCipherSuitList(cipherList);
								}
								
								cipherList.removeAll(cipherSuiteList);
								updateEAPTLSDetailsForm.setRemainingCipherList(cipherList);
								
							}else{
								updateEAPTLSDetailsForm.setCipherSuitList(cipherList);
							}
						}
						updateEAPTLSDetailsForm = convertBeanToForm(eapConfigData);
						updateEAPTLSDetailsForm.setTypeVal(type);
						String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
						request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
						request.setAttribute("eapConfigData",eapConfigData);
						request.setAttribute("vendoridentifiers",vendoridentifiers);
						request.setAttribute("serverCerticateIdForVSC",serverCerticateIdForVSC);
						request.setAttribute("updateEAPTLSDetailsForm",updateEAPTLSDetailsForm);


					}	
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateEAPTLSDetailsForm.getAction().equalsIgnoreCase("update")){

					String stringsArray=updateEAPTLSDetailsForm.getCipherSuites();
					String [] paramArray=stringsArray.split(",");
					updateEAPTLSDetailsForm.setListCipherSuites(paramArray);
					
					EAPConfigData eapConfigData = new EAPConfigData();

					eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
					
					eapConfigData=convertFormToBean(updateEAPTLSDetailsForm,eapConfigData);

					// set vendor specific certificate
					String [] oui = request.getParameterValues("oui");
					String [] serverCertificateIdForVSC = request.getParameterValues("serverCerticateIdForVSC");
					List<VendorSpecificCertificateData> vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateData>();
					Set<VendorSpecificCertificateData> vendorSpecificCertificateSet = new HashSet<VendorSpecificCertificateData>();
					if(oui != null && oui.length>0)
					{
						int len = serverCertificateIdForVSC.length;
						Logger.logDebug("Module", "certificate length is:"+len);

						for(int i=0;i<len;i++){
							VendorSpecificCertificateData vendorSpecificCertificateData = new VendorSpecificCertificateData();

							vendorSpecificCertificateData.setOui(oui[i]);
							
							if(serverCertificateIdForVSC[i].equalsIgnoreCase("0")){
	    						vendorSpecificCertificateData.setServerCertificateIdForVSC(null);
	    					}else{
	    						vendorSpecificCertificateData.setServerCertificateIdForVSC(serverCertificateIdForVSC[i]);
	    					}
							vendorSpecificCertificateData.setEaptlsId(updateEAPTLSDetailsForm.getEaptlsId());
							vendorSpecificCertificateList.add(vendorSpecificCertificateData);
							vendorSpecificCertificateSet.add(vendorSpecificCertificateData);
						}
					}

					eapConfigData.getEaptlsConfigData().setVendorSpecificList(vendorSpecificCertificateList);
					
					staffData.setAuditName(eapConfigData.getName());
					staffData.setAuditId(eapConfigData.getAuditUId());
					
					eapconfigConfigBLManager.updateTLSDetails(eapConfigData,staffData,ACTION_ALIAS);
					
					request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("responseUrl","/viewEAPConfig.do?viewType=basic&eapId="+eapId); 
					ActionMessage message=null;
					if("TLS".equals(updateEAPTLSDetailsForm.getTypeVal())){
						message = new ActionMessage("servermgr.eap.tls.update.success");	
					}else if ("TTLS".equals(updateEAPTLSDetailsForm.getTypeVal())){
						message = new ActionMessage("servermgr.eap.ttls.update.success");
					}else{
						message = new ActionMessage("servermgr.eap.peap.update.success");
					}

					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);


				}
			}catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message=null;
				if("TLS".equals(type)){
					message = new ActionMessage("servermgr.eap.tls.update.failure");	
				}else if("TTLS".equals(type)){
					message = new ActionMessage("servermgr.eap.ttls.update.failure");	
				}else{
					message = new ActionMessage("servermgr.eap.peap.update.failure");
				}
				//ActionMessage message = new ActionMessage("servermgr.eap.tls.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message=null;
				if("TLS".equals(type)){
					message = new ActionMessage("servermgr.eap.tls.update.failure");	
				}else if("TTLS".equals(type)){
					message = new ActionMessage("servermgr.eap.ttls.update.failure");	
				}else{
					message = new ActionMessage("servermgr.eap.peap.update.failure");
				}
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private EAPConfigData convertFormToBean(UpdateEAPTLSDetailsForm updateEAPTLSDetailsForm,EAPConfigData configData) {

		EAPTLSConfigData tlsConfigData = new EAPTLSConfigData();

		//set config data
		configData.setEapId(updateEAPTLSDetailsForm.getEapId());
		configData.setLastModifiedDate(getCurrentTimeStemp());
		configData.setEapTtlsCertificateRequest(updateEAPTLSDetailsForm.getTtlscertificateRequest());
		configData.setPeapVersion(updateEAPTLSDetailsForm.getPeapVersion());
		configData.setEapPeapCertificateRequest(updateEAPTLSDetailsForm.getPeapCertificateRequest());
		configData.setPeapNegotiationMethod(updateEAPTLSDetailsForm.getPeapNegotiationMethod());
		configData.setTtlsNegotiationMethod(updateEAPTLSDetailsForm.getTtlsNegotiationMethod());
		configData.setAuditUId(updateEAPTLSDetailsForm.getAuditUId());
		configData.setName(updateEAPTLSDetailsForm.getName());
		
		configData.setPeapVersion(updateEAPTLSDetailsForm.getPeapVersion());
		tlsConfigData.setEapId(updateEAPTLSDetailsForm.getEapId());
		tlsConfigData.setEaptlsId(updateEAPTLSDetailsForm.getEaptlsId());
		tlsConfigData.setMinTlsVersion(updateEAPTLSDetailsForm.getMinTlsVersion());
		tlsConfigData.setMaxTlsVersion(updateEAPTLSDetailsForm.getMaxTlsVersion());
		if(Strings.isNullOrBlank(updateEAPTLSDetailsForm.getServerCertificateId()) == false && ("0".equals(updateEAPTLSDetailsForm.getServerCertificateId()) == false)){
			tlsConfigData.setServerCertificateId(updateEAPTLSDetailsForm.getServerCertificateId());
		} else {
			tlsConfigData.setServerCertificateId(null);
		}
		tlsConfigData.setCertificateRequest(updateEAPTLSDetailsForm.getCertificateRequest());
		tlsConfigData.setSessionResumptionLimit(updateEAPTLSDetailsForm.getSessionResumptionLimit());
		tlsConfigData.setSessionResumptionDuration(updateEAPTLSDetailsForm.getSessionResumptionDuration());
		tlsConfigData.setDefaultCompressionMethod(updateEAPTLSDetailsForm.getDefaultCompressionMethod());
		
		//set certificate type list...
		StringBuffer certificateString = new StringBuffer();

		if(updateEAPTLSDetailsForm.isDss()){
			if(certificateString.length()>0){
				certificateString.append(",");	
				certificateString.append("2");
			}else{
				certificateString.append("2");
			}
		}
		if(updateEAPTLSDetailsForm.isDss_dh()){

			if(certificateString.length()>0){
				certificateString.append(",");	
				certificateString.append("4");
			}else{
				certificateString.append("4");
			}
		}
		if(updateEAPTLSDetailsForm.isRsa()){
			if(certificateString.length()>0){
				certificateString.append(",");	
				certificateString.append("1");
			}else{
				certificateString.append("1");
			}
		}
		if(updateEAPTLSDetailsForm.isRsa()){
			if(certificateString.length()>0){
				certificateString.append(",");	
				certificateString.append("3");
			}else{
				certificateString.append("3");
			}
		}



		tlsConfigData.setCertificateTypesList(certificateString.toString());

		//set cipher suite list

		StringBuffer strCipherSuiteList = new StringBuffer();
		
		String[] strCiphersuit=updateEAPTLSDetailsForm.getListCipherSuites();
		String[] strcipherSuitArray =new String[4000];
		CipherSuites[] cipherSuites= CipherSuites.values();
		
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
		tlsConfigData.setCiphersuiteList(strCipherSuiteList.toString());
		tlsConfigData.setServerCertificateProfileName(updateEAPTLSDetailsForm.getServerCertificateName());
		tlsConfigData.setExpiryDate((updateEAPTLSDetailsForm.getExpiryDate() == null)?"false":updateEAPTLSDetailsForm.getExpiryDate());
		tlsConfigData.setRevokedCertificate((updateEAPTLSDetailsForm.getRevokedCertificate() == null)?"false":updateEAPTLSDetailsForm.getRevokedCertificate());
		tlsConfigData.setMissingClientCertificate((updateEAPTLSDetailsForm.getMissingClientCertificate() == null)?"false":updateEAPTLSDetailsForm.getMissingClientCertificate());
		tlsConfigData.setMacValidation((updateEAPTLSDetailsForm.getMacValidation() == null)?"false":updateEAPTLSDetailsForm.getMacValidation());
		configData.setEaptlsConfigData(tlsConfigData);
		return configData;

	}

	private UpdateEAPTLSDetailsForm convertBeanToForm(EAPConfigData eapConfigData) 
	{
		UpdateEAPTLSDetailsForm updateEAPTLSDetailsForm = new UpdateEAPTLSDetailsForm();
		updateEAPTLSDetailsForm.setEapId(eapConfigData.getEapId());

		String[] labelEnableAuthMethods = EAPConfigUtils.convertEnableAuthMethodToLabel(eapConfigData.getEnabledAuthMethods());
		updateEAPTLSDetailsForm.setEnabledAuthMethods(labelEnableAuthMethods[0]);
		updateEAPTLSDetailsForm.setTtlscertificateRequest(eapConfigData.getEapTtlsCertificateRequest());
		updateEAPTLSDetailsForm.setPeapCertificateRequest(eapConfigData.getEapPeapCertificateRequest());
		updateEAPTLSDetailsForm.setPeapVersion(eapConfigData.getPeapVersion());
		updateEAPTLSDetailsForm.setPeapNegotiationMethod(eapConfigData.getPeapNegotiationMethod());
		updateEAPTLSDetailsForm.setTtlsNegotiationMethod(eapConfigData.getTtlsNegotiationMethod());
		updateEAPTLSDetailsForm.setAuditUId(eapConfigData.getAuditUId());
		updateEAPTLSDetailsForm.setName(eapConfigData.getName());
		
		EAPTLSConfigData tlsConfigData=eapConfigData.getEaptlsConfigData();
		/*
		 * set TLS/TTLS configuration
		 */

		if(tlsConfigData != null){
			updateEAPTLSDetailsForm.setEaptlsId(tlsConfigData.getEaptlsId());
			updateEAPTLSDetailsForm.setMinTlsVersion(tlsConfigData.getMinTlsVersion());
			updateEAPTLSDetailsForm.setMaxTlsVersion(tlsConfigData.getMaxTlsVersion());
			updateEAPTLSDetailsForm.setCertificateRequest(tlsConfigData.getCertificateRequest());
			updateEAPTLSDetailsForm.setSessionResumptionLimit(tlsConfigData.getSessionResumptionLimit());
			updateEAPTLSDetailsForm.setSessionResumptionDuration(tlsConfigData.getSessionResumptionDuration());
			updateEAPTLSDetailsForm.setDefaultCompressionMethod(tlsConfigData.getDefaultCompressionMethod());
			updateEAPTLSDetailsForm.setServerCertificateId(((tlsConfigData.getServerCertificateId()) !=null) ? tlsConfigData.getServerCertificateId() : "");
			updateEAPTLSDetailsForm.setExpiryDate(tlsConfigData.getExpiryDate());
			updateEAPTLSDetailsForm.setRevokedCertificate(tlsConfigData.getRevokedCertificate());
			updateEAPTLSDetailsForm.setMissingClientCertificate(tlsConfigData.getMissingClientCertificate());
			updateEAPTLSDetailsForm.setMacValidation(tlsConfigData.getMacValidation());
			//set certificate type
			String certificateTypeList=tlsConfigData.getCertificateTypesList();

			String[] certificateTypesArray=certificateTypeList.split(",");
			for (int i = 0; i < certificateTypesArray.length; i++) {

				if("2".equals(certificateTypesArray[i]))
					updateEAPTLSDetailsForm.setDss(true);	
				else if("4".equals(certificateTypesArray[i]))
					updateEAPTLSDetailsForm.setDss_dh(true);
				else if("1".equals(certificateTypesArray[i]))
					updateEAPTLSDetailsForm.setRsa(true);
				else if("3".equals(certificateTypesArray[i]))
					updateEAPTLSDetailsForm.setRsa_dh(true);

			}


			/*
			 * set cipher suite list
			 *
			 * TLS_RSA_WITH_3DES_EDE_CBC_SHA=10
			 * TLS_RSA_WITH_AES_128_CBC_SHA=47
			 * TLS_RSA_WITH_DES_CBC_SHA=9 
			 */

			if(tlsConfigData.getCiphersuiteList() !=null){
				String cipherSuitString = tlsConfigData.getCiphersuiteList();
				String[] cipherSuiteArray=cipherSuitString.split(",");
				List<String> cipherSuitValueStrings =new ArrayList<String>() ;
				CipherSuites[] cipherSuites= CipherSuites.values();
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
				updateEAPTLSDetailsForm.setLstCipherSuitesList(cipherSuitValueStrings);
				updateEAPTLSDetailsForm.setCipherSuites(strCipherSuit);
			}
			
			
			
		}	



		return updateEAPTLSDetailsForm;

	}


	private List<String> convertcipherSuiteListToString(String ciphersuiteList) {
		String[] cipherSuiteValueArray = ciphersuiteList.split(",");
		List<String> cipherSuiteLabelArray = new ArrayList<String>();
		CipherSuites[] cipherSuites= CipherSuites.values();
		
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
