package com.elitecore.elitesm.web.servermgr.eap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPSimAkaConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.EAPGsmConfigForm;

public class CreateEAPGsmConfigAction extends BaseWebAction {

	private static String ACTION_ALIAS = ConfigConstant.CREATE_EAP_CONFIGURATION;
	private static final String GSM_FORWARD = "createEAPGsmConfig";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response){
		try{
			Logger.logInfo(MODULE, "Enter execute method of"+getClass().getName());
			checkAccess(request, ACTION_ALIAS);
			EAPGsmConfigForm eapGsmConfigForm = (EAPGsmConfigForm) form;
			if("create".equals(eapGsmConfigForm.getAction())){
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				EAPConfigBLManager blManager = new EAPConfigBLManager();
				EAPConfigData eapConfigData=(EAPConfigData)request.getSession().getAttribute("eapConfigData");
				
				setGsmDataToBean(eapGsmConfigForm, eapConfigData);
				
				blManager.create(eapConfigData, staffData);
				
				request.getSession().removeAttribute("createEAPConfigDetailForm");
				request.getSession().removeAttribute("dbFieldMapList");
				request.getSession().removeAttribute("createEAPConfigForm");
				request.getSession().removeAttribute("enabledMethodList");

				request.setAttribute("responseUrl","/initSearchEAPConfig.do?");
				ActionMessage message = new ActionMessage("servermgr.eap.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			} else{
				eapGsmConfigForm.setDefaultValueToForm();
				return mapping.findForward(GSM_FORWARD);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch (Exception e) {
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} 
		return mapping.findForward(FAILURE);
	}
	
	private void setGsmDataToBean(EAPGsmConfigForm eapGsmConfigForm, EAPConfigData eapConfigData){
		// set EAP sim Configuration
		if(eapConfigData.getEnabledAuthMethods().contains(EAPConfigConstant.SIM_STR)){
		EAPSimAkaConfigData eapSimConfigData = new EAPSimAkaConfigData();
		if(!"0".equals(eapGsmConfigForm.getSimPseudonymGenMethod())){
			eapSimConfigData.setPseudonymGenMethod(eapGsmConfigForm.getSimPseudonymGenMethod());
		}
		eapSimConfigData.setPseudonymHexenCoding(eapGsmConfigForm.getSimPseudonymHexenCoding());
		eapSimConfigData.setPseudonymPrefix(eapGsmConfigForm.getSimPseudonymPrefix());
		eapSimConfigData.setPseudonymRootNAI(eapGsmConfigForm.getSimPseudonymRootNAI());
		eapSimConfigData.setPseudonymAAAIdentityInRootNAI(eapGsmConfigForm.getSimPseudonymAAAIndentityInRootNAI());

		if(!"0".equals(eapGsmConfigForm.getSimFastReauthGenMethod())){
			eapSimConfigData.setFastReAuthGenMethod(eapGsmConfigForm.getSimFastReauthGenMethod());
		}
		eapSimConfigData.setFastReAuthHexenCoding(eapGsmConfigForm.getSimFastReauthHexenCoding());
		eapSimConfigData.setFastReAuthPrefix(eapGsmConfigForm.getSimFastReauthPrefix());
		eapSimConfigData.setFastReAuthRootNAI(eapGsmConfigForm.getSimFastReauthRootNAI());
		eapSimConfigData.setFastReAuthAAAIdentityInRootNAI(eapGsmConfigForm.getSimFastReauthAAAIndentityInRootNAI());
		eapSimConfigData.setEapAuthType(EAPConfigConstant.SIM);
		eapConfigData.setSimConfigData(eapSimConfigData);
		}


		// Set EAP AKA Configuration
		if(eapConfigData.getEnabledAuthMethods().contains(EAPConfigConstant.AKA_STR)){
		EAPSimAkaConfigData eapAKAConfigData = new EAPSimAkaConfigData();
		if(!"0".equals(eapGsmConfigForm.getAkaPseudonymGenMethod())){
			eapAKAConfigData.setPseudonymGenMethod(eapGsmConfigForm.getAkaPseudonymGenMethod());
		}
		eapAKAConfigData.setPseudonymHexenCoding(eapGsmConfigForm.getAkaPseudonymHexenCoding());
		eapAKAConfigData.setPseudonymPrefix(eapGsmConfigForm.getAkaPseudonymPrefix());
		eapAKAConfigData.setPseudonymRootNAI(eapGsmConfigForm.getAkaPseudonymRootNAI());
		eapAKAConfigData.setPseudonymAAAIdentityInRootNAI(eapGsmConfigForm.getAkaPseudonymAAAIndentityInRootNAI());
		if(!"0".equals(eapGsmConfigForm.getAkaFastReauthGenMethod())){
			eapAKAConfigData.setFastReAuthGenMethod(eapGsmConfigForm.getAkaFastReauthGenMethod());
		}
		eapAKAConfigData.setFastReAuthHexenCoding(eapGsmConfigForm.getAkaFastReauthHexenCoding());
		eapAKAConfigData.setFastReAuthPrefix(eapGsmConfigForm.getAkaFastReauthPrefix());
		eapAKAConfigData.setFastReAuthRootNAI(eapGsmConfigForm.getAkaFastReauthRootNAI());
		eapAKAConfigData.setFastReAuthAAAIdentityInRootNAI(eapGsmConfigForm.getAkaFastReauthAAAIndentityInRootNAI());
		eapAKAConfigData.setEapAuthType(EAPConfigConstant.AKA);
		eapConfigData.setAkaConfigData(eapAKAConfigData);
		}

		// Set EAP AKA' Configuration
		if(eapConfigData.getEnabledAuthMethods().contains(EAPConfigConstant.AKA_PRIME_STR)){
		EAPSimAkaConfigData eapAKAPrimeConfigData = new EAPSimAkaConfigData();
		if(!"0".equals(eapGsmConfigForm.getAkaPrimePseudonymGenMethod())){
			eapAKAPrimeConfigData.setPseudonymGenMethod(eapGsmConfigForm.getAkaPrimePseudonymGenMethod());
		}
		eapAKAPrimeConfigData.setPseudonymHexenCoding(eapGsmConfigForm.getAkaPrimePseudonymHexenCoding());
		eapAKAPrimeConfigData.setPseudonymPrefix(eapGsmConfigForm.getAkaPrimePseudonymPrefix());
		eapAKAPrimeConfigData.setPseudonymRootNAI(eapGsmConfigForm.getAkaPrimePseudonymRootNAI());
		eapAKAPrimeConfigData.setPseudonymAAAIdentityInRootNAI(eapGsmConfigForm.getAkaPrimePseudonymAAAIndentityInRootNAI());
		if(!"0".equals(eapGsmConfigForm.getAkaPrimeFastReauthGenMethod())){
			eapAKAPrimeConfigData.setFastReAuthGenMethod(eapGsmConfigForm.getAkaPrimeFastReauthGenMethod());
		}
		eapAKAPrimeConfigData.setFastReAuthHexenCoding(eapGsmConfigForm.getAkaPrimeFastReauthHexenCoding());
		eapAKAPrimeConfigData.setFastReAuthPrefix(eapGsmConfigForm.getAkaPrimeFastReauthPrefix());
		eapAKAPrimeConfigData.setFastReAuthRootNAI(eapGsmConfigForm.getAkaPrimeFastReauthRootNAI());
		eapAKAPrimeConfigData.setFastReAuthAAAIdentityInRootNAI(eapGsmConfigForm.getAkaPrimeFastReauthAAAIndentityInRootNAI());
		eapAKAPrimeConfigData.setEapAuthType(EAPConfigConstant.AKA_PRIME);
		eapConfigData.setAkaPrimeConfigData(eapAKAPrimeConfigData);
		}
	}
	
	
}
