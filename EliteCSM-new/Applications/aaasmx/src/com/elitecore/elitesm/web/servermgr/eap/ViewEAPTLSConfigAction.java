package com.elitecore.elitesm.web.servermgr.eap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
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
import com.elitecore.elitesm.web.servermgr.eap.forms.ViewEAPTLSConfigForm;


public class ViewEAPTLSConfigAction extends BaseEAPConfigAction {
	private static final String VIEW_FORWARD = "vieweaptlsdetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_EAP_CONFIGURATION;
	private static final String MODULE = "VIEW_EAP_TLS_CONFIG";
	private static final String VIEW_TLS_CONFIG_NOT_EXIST = "tlsconfignotexist";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{


				EAPConfigBLManager eapconConfigBLManager = new EAPConfigBLManager();
				ViewEAPTLSConfigForm viewEAPTLSConfigForm = (ViewEAPTLSConfigForm)form;

				if("ttls".equals(request.getParameter("type"))){
					viewEAPTLSConfigForm.setType("ttls");
				}else if("peap".equals(request.getParameter("type"))){
					viewEAPTLSConfigForm.setType("peap");
				}else{  
					viewEAPTLSConfigForm.setType("tls");
				}


				EAPConfigData eapConfigData = new EAPConfigData();
				String strEapId = request.getParameter("eapId");
				String eapId = strEapId;

				if(Strings.isNullOrBlank(eapId) == false){

					EAPTLSConfigData eaptlsConfigData = new EAPTLSConfigData();
					eapConfigData = eapconConfigBLManager.getEapConfigurationDataById(eapId);
					eaptlsConfigData = eapConfigData.getEaptlsConfigData();
					String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

					if(eaptlsConfigData != null){

						List<ServerCertificateData> serverCertificateList = eapconConfigBLManager.getListOfServerCertificate();
						
						if(serverCertificateList != null){
							for(int i=0;i<serverCertificateList.size();i++){
								ServerCertificateData tempServerCertificateData = (ServerCertificateData) serverCertificateList.get(i);
								if(tempServerCertificateData.getServerCertificateId().equals(eaptlsConfigData.getServerCertificateId())){
									eaptlsConfigData.setServerCertificateProfileName(tempServerCertificateData.getServerCertificateName());
								}
							}
						}
						
						if(eaptlsConfigData.getCiphersuiteList() != null){
							List<String> cipherSuiteList = convertcipherSuiteListToString(eaptlsConfigData.getCiphersuiteList());
							request.setAttribute("cipherSuiteList",cipherSuiteList);
						}
						
						String  labelCertificatetypeList = convertCertificatetypeListToLabel(eapConfigData.getEaptlsConfigData().getCertificateTypesList());
						eapConfigData.getEaptlsConfigData().setCertificateTypesList(labelCertificatetypeList);

						 
						List<VendorSpecificCertificateData> vendorSpecificCertificateDataList=eaptlsConfigData.getVendorSpecificList();
						Map<String,VendorSpecificServerCertificateInfo> vendorSpecoficServerInformation =  new LinkedHashMap<String, VendorSpecificServerCertificateInfo>();
						for(VendorSpecificCertificateData vendorData:vendorSpecificCertificateDataList){
							for(ServerCertificateData serverCertificateData:serverCertificateList){
								if(serverCertificateData.getServerCertificateId().equals(vendorData.getServerCertificateIdForVSC())){
									
									VendorSpecificServerCertificateInfo vInfo = new VendorSpecificServerCertificateInfo();
									vInfo.setServerCertificateId(serverCertificateData.getServerCertificateId());
									vInfo.setServerCertificateName(serverCertificateData.getServerCertificateName());
									vendorSpecoficServerInformation.put(vendorData.getOui(), vInfo);
								}else if(vendorData.getServerCertificateIdForVSC() == null){
									VendorSpecificServerCertificateInfo vInfo = new VendorSpecificServerCertificateInfo();
									vInfo.setServerCertificateName("NONE");
									vendorSpecoficServerInformation.put(vendorData.getOui(), vInfo);
								}
							}
						}
						
						request.setAttribute("eapConfigData",eapConfigData);
						request.setAttribute("eapTlsConfigData",eapConfigData.getEaptlsConfigData());
						request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
						request.setAttribute("serverCertificateList", serverCertificateList);
						request.setAttribute("vendorSpecoficServerInformation", vendorSpecoficServerInformation);
						return mapping.findForward(VIEW_FORWARD);

					}else{
						request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
						request.setAttribute("eapConfigData",eapConfigData);
						return mapping.findForward(VIEW_TLS_CONFIG_NOT_EXIST);

					}
				}

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("servermgr.eap.view.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	protected List<String> convertcipherSuiteListToString(String ciphersuiteList) {
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
