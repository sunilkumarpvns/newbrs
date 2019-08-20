package com.elitecore.elitesm.web.servermgr.eap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;



import com.elitecore.commons.base.Strings;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.IEAPTLSConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.VendorSpecificCertificateData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.CreateEAPConfigDetailForm;

public class CreateEAPConfigDetailAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_FORWARD = "viewEAPConfigDetail";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_EAP_CONFIGURATION;
	private static final String GSM_FORWARD = "createEAPGsmConfig";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			EAPConfigData eapConfigData = new EAPConfigData();
			try{
				checkActionPermission(request, ACTION_ALIAS);
				CreateEAPConfigDetailForm createEAPConfigDetailForm = (CreateEAPConfigDetailForm)form;
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				EAPConfigBLManager blmanager = new EAPConfigBLManager();
                
                if(createEAPConfigDetailForm.getCheckAction().equalsIgnoreCase("Save")){
                	
                	
                	EAPTLSConfigData eaptlsConfigData=new EAPTLSConfigData();
                	
                	eapConfigData=(EAPConfigData)request.getSession().getAttribute("eapConfigData");
                	
                	//if ttls is selected 
                	
                	List<String> enabledMethodList=(List<String>)request.getSession().getAttribute("enalbedMethodList");
                	if(enabledMethodList!=null){
                		if (enabledMethodList.contains(EAPConfigConstant.TTLS_STR)){
                			eapConfigData.setEapTtlsCertificateRequest(createEAPConfigDetailForm.getTtlscertificateRequest());
                			eapConfigData.setTtlsNegotiationMethod(createEAPConfigDetailForm.getTtlsNegotiationMethod());
                		}
                		if (enabledMethodList.contains(EAPConfigConstant.PEAP_STR)){
                			eapConfigData.setEapPeapCertificateRequest(createEAPConfigDetailForm.getPeapCertificateRequest());
                			eapConfigData.setPeapVersion(createEAPConfigDetailForm.getPeapVersion());
                			eapConfigData.setPeapNegotiationMethod(createEAPConfigDetailForm.getPeapNegotiationMethod());
                		}
                	}
                	
                	String stringsArray=createEAPConfigDetailForm.getCipherSuites();
        			String [] paramArray=stringsArray.split(",");
        			createEAPConfigDetailForm.setListCipherSuites(paramArray);
                	
                	convertFormToBean(eaptlsConfigData,createEAPConfigDetailForm);
                	
                	// set vendor specific certificate
                	String [] oui = request.getParameterValues("oui");
                	String [] serverCertificateIdForVSC = request.getParameterValues("serverCerticateIdForVSC");
    				List<VendorSpecificCertificateData> vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateData>();
    				
    				if(oui != null && oui.length>0)
    				{
    					int len = oui.length;
	    				Logger.logDebug("Module", "certificate length is:"+len);
	    				
	    				for(int i=0;i<len;i++){
	    					VendorSpecificCertificateData vendorSpecificCertificateData = new VendorSpecificCertificateData();
	    					
	    					vendorSpecificCertificateData.setOui(oui[i]);
	    					
	    					if(serverCertificateIdForVSC[i].equalsIgnoreCase("0")){
	    						vendorSpecificCertificateData.setServerCertificateIdForVSC(null);
	    					}else{
	    						vendorSpecificCertificateData.setServerCertificateIdForVSC(serverCertificateIdForVSC[i]);
	    					}
	    				    
	                        vendorSpecificCertificateList.add(vendorSpecificCertificateData);
	                        
	    				}
    				}
    				
                	eaptlsConfigData.setVendorSpecificList(vendorSpecificCertificateList);
                	eapConfigData.setEaptlsConfigData(eaptlsConfigData);
                	
                	String nextForward = getNextForward(enabledMethodList);
                	if(nextForward==null){
                		blmanager.create(eapConfigData,staffData);
                		request.getSession().removeAttribute("createEAPConfigDetailForm");
                		request.getSession().removeAttribute("dbFieldMapList");
                		request.getSession().removeAttribute("createEAPConfigForm");

                		request.setAttribute("responseUrl","/initSearchEAPConfig.do?");
                		ActionMessage message = new ActionMessage("servermgr.eap.create.success");
                		ActionMessages messages = new ActionMessages();
                		messages.add("information",message);
                		saveMessages(request,messages);
                		return mapping.findForward(SUCCESS_FORWARD);
                	}else{
                		return mapping.findForward(nextForward);
                	}
                }
	
				return mapping.findForward(VIEW_FORWARD);
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
		        ActionMessage message = new ActionMessage("servermgr.eap.create.duplicate.failure",eapConfigData.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
				return mapping.findForward(FAILURE_FORWARD);
		/*}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}*/
	}

	private void convertFormToBean(IEAPTLSConfigData eaptlsConfigData,CreateEAPConfigDetailForm createEAPConfigDetailForm) {
		
        eaptlsConfigData.setMinTlsVersion(createEAPConfigDetailForm.getMinTlsVersion());
        eaptlsConfigData.setMaxTlsVersion(createEAPConfigDetailForm.getMaxTlsVersion());
        if(Strings.isNullOrBlank(createEAPConfigDetailForm.getServerCertificateId()) == false && ("0".equals(createEAPConfigDetailForm.getServerCertificateId()) == false)){
        	eaptlsConfigData.setServerCertificateId(createEAPConfigDetailForm.getServerCertificateId());
        }else{
        	eaptlsConfigData.setServerCertificateId(null);
        }
        	
		eaptlsConfigData.setCertificateRequest(createEAPConfigDetailForm.getCertificateRequest());
		eaptlsConfigData.setSessionResumptionLimit(createEAPConfigDetailForm.getSessionResumptionLimit());
		eaptlsConfigData.setSessionResumptionDuration(createEAPConfigDetailForm.getSessionResumptionDuration());
		eaptlsConfigData.setDefaultCompressionMethod(createEAPConfigDetailForm.getDefaultCompressionMethod());
		
		//build certificateTypesList string
		StringBuffer strCertificateTypesList = new StringBuffer();
		int[] certificateTypesList = new int[4];
		certificateTypesList[0]=createEAPConfigDetailForm.getDss();
		certificateTypesList[1]=createEAPConfigDetailForm.getDss_dh();
		certificateTypesList[2]=createEAPConfigDetailForm.getRsa();
		certificateTypesList[3]=createEAPConfigDetailForm.getRsa_dh();
		for (int i = 0; i < certificateTypesList.length; i++) {
			
			if(certificateTypesList[i] != 0){
			   	if(strCertificateTypesList.length()>0){
			   		strCertificateTypesList.append(",");
			   		strCertificateTypesList.append(certificateTypesList[i]);
			   	}else{
			   		strCertificateTypesList.append(certificateTypesList[i]);
			   	}
				
			}
		}
		eaptlsConfigData.setCertificateTypesList(strCertificateTypesList.toString());
	  	
		StringBuffer strCipherSuiteList = new StringBuffer();
		
		String[] strCiphersuit=createEAPConfigDetailForm.getListCipherSuites();
		String[] strcipherSuitArray =new String[4000];
		CipherSuites[] cipherSuites= CipherSuites.values();
		
		for(int i=0;i<strCiphersuit.length;i++){
			if(strCiphersuit[i] != null || strCiphersuit[i] != " "){
				for(CipherSuites cipherSuit:cipherSuites){
					if(strCiphersuit[i].trim().equals(cipherSuit.name().trim())){
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
		eaptlsConfigData.setCiphersuiteList(strCipherSuiteList.toString());
		eaptlsConfigData.setServerCertificateProfileName(createEAPConfigDetailForm.getServerCertificateProfileName());
		
		//Certificate Validation 
		eaptlsConfigData.setExpiryDate((createEAPConfigDetailForm.getExpiryDate() == null) ?"false":createEAPConfigDetailForm.getExpiryDate());
		eaptlsConfigData.setRevokedCertificate((createEAPConfigDetailForm.getRevokedCertificate() == null)?"false":createEAPConfigDetailForm.getRevokedCertificate());
		eaptlsConfigData.setMissingClientCertificate((createEAPConfigDetailForm.getMissingClientCertificate() == null)?"false":createEAPConfigDetailForm.getMissingClientCertificate());
		eaptlsConfigData.setMacValidation((createEAPConfigDetailForm.getMacValidation() == null)?"false":createEAPConfigDetailForm.getMacValidation());
		
	}
	private String getNextForward(List<String> enalbedMethodList){
		if(enalbedMethodList!=null){
			if(enalbedMethodList.contains(EAPConfigConstant.SIM_STR) || enalbedMethodList.contains(EAPConfigConstant.AKA_STR) || enalbedMethodList.contains(EAPConfigConstant.AKA_PRIME_STR)){
				return GSM_FORWARD;
			}
		}
		return null;
	}


	
	
}	
