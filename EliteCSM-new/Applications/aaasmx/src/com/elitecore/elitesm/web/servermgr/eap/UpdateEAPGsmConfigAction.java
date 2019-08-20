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
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.EAPGsmConfigForm;

public class UpdateEAPGsmConfigAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EAP_CONFIGURATION;
	private static  final String UPDATE_FORWARD = "updateEapGsmConfigData";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkAccess(request, ACTION_ALIAS);
			EAPGsmConfigForm eapGsmConfigForm = (EAPGsmConfigForm) form;
			EAPConfigBLManager eapconConfigBLManager = new EAPConfigBLManager();
			EAPConfigData eapConfigData = new EAPConfigData();
			
			if("update".equals(eapGsmConfigForm.getAction())){
				setGsmDataToBean(eapGsmConfigForm,eapConfigData);
				eapConfigData.setEapId(eapGsmConfigForm.getEapId());
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditName(eapConfigData.getName());
				staffData.setAuditId(eapConfigData.getAuditUId());
				
				eapconConfigBLManager.updateEapGsmDetails(eapConfigData,staffData,ACTION_ALIAS);
				
				request.setAttribute("eapConfigData",eapConfigData);
				request.setAttribute("responseUrl","/viewEAPConfig.do?viewType=basic&eapId="+eapGsmConfigForm.getEapId());
				ActionMessage message = new ActionMessage("servermgr.eap.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}else{
				if(eapGsmConfigForm.getEapId() != null){
					eapConfigData = eapconConfigBLManager.getEapConfigurationDataById(eapGsmConfigForm.getEapId());
					eapGsmConfigForm.setDefaultGenMethodMap();
					String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
					request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("labelDefaultNegotiationMethod", labelDefaultNegotiationMethod);
					convertBeanToForm(eapGsmConfigForm, eapConfigData);
					return mapping.findForward(UPDATE_FORWARD);
				}
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
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
	
	private void convertBeanToForm(EAPGsmConfigForm form, EAPConfigData eapConfigData){
		// set Sim data to form
		if(eapConfigData.getSimConfigData() != null){
			EAPSimAkaConfigData simConfigData = eapConfigData.getSimConfigData();
			form.setSimPseudonymGenMethod(simConfigData.getPseudonymGenMethod());
			form.setSimPseudonymHexenCoding(simConfigData.getPseudonymHexenCoding());
			form.setSimPseudonymPrefix(simConfigData.getPseudonymPrefix());
			form.setSimPseudonymRootNAI(simConfigData.getPseudonymRootNAI());
			form.setSimPseudonymAAAIndentityInRootNAI(simConfigData.getPseudonymAAAIdentityInRootNAI());
			
			form.setSimFastReauthGenMethod(simConfigData.getFastReAuthGenMethod());
			form.setSimFastReauthHexenCoding(simConfigData.getFastReAuthHexenCoding());
			form.setSimFastReauthPrefix(simConfigData.getFastReAuthPrefix());
			form.setSimFastReauthRootNAI(simConfigData.getFastReAuthRootNAI());
			form.setSimFastReauthAAAIndentityInRootNAI(simConfigData.getFastReAuthAAAIdentityInRootNAI());
			
			form.setSimConfigId(simConfigData.getConfigId());
			form.setAuditUId(eapConfigData.getAuditUId());
			form.setName(eapConfigData.getName());
		}
		// set AKA data to form
		if(eapConfigData.getAkaConfigData() != null){
			EAPSimAkaConfigData akaConfigData = eapConfigData.getAkaConfigData();
			form.setAkaPseudonymGenMethod(akaConfigData.getPseudonymGenMethod());
			form.setAkaPseudonymHexenCoding(akaConfigData.getPseudonymHexenCoding());
			form.setAkaPseudonymPrefix(akaConfigData.getPseudonymPrefix());
			form.setAkaPseudonymRootNAI(akaConfigData.getPseudonymRootNAI());
			form.setAkaPseudonymAAAIndentityInRootNAI(akaConfigData.getPseudonymAAAIdentityInRootNAI());
			
			form.setAkaFastReauthGenMethod(akaConfigData.getFastReAuthGenMethod());
			form.setAkaFastReauthHexenCoding(akaConfigData.getFastReAuthHexenCoding());
			form.setAkaFastReauthPrefix(akaConfigData.getFastReAuthPrefix());
			form.setAkaFastReauthRootNAI(akaConfigData.getFastReAuthRootNAI());
			form.setAkaFastReauthAAAIndentityInRootNAI(akaConfigData.getFastReAuthAAAIdentityInRootNAI());
			
			form.setAkaConfigId(akaConfigData.getConfigId());
			form.setAuditUId(eapConfigData.getAuditUId());
			form.setName(eapConfigData.getName());
		}
		
		// set AKA' data to form
		if(eapConfigData.getAkaPrimeConfigData() != null){
			EAPSimAkaConfigData akaPrimeConfigData = eapConfigData.getAkaPrimeConfigData();
			form.setAkaPrimePseudonymGenMethod(akaPrimeConfigData.getPseudonymGenMethod());
			form.setAkaPrimePseudonymHexenCoding(akaPrimeConfigData.getPseudonymHexenCoding());
			form.setAkaPrimePseudonymPrefix(akaPrimeConfigData.getPseudonymPrefix());
			form.setAkaPrimePseudonymRootNAI(akaPrimeConfigData.getPseudonymRootNAI());
			form.setAkaPrimePseudonymAAAIndentityInRootNAI(akaPrimeConfigData.getPseudonymAAAIdentityInRootNAI());
			
			form.setAkaPrimeFastReauthGenMethod(akaPrimeConfigData.getFastReAuthGenMethod());
			form.setAkaPrimeFastReauthHexenCoding(akaPrimeConfigData.getFastReAuthHexenCoding());
			form.setAkaPrimeFastReauthPrefix(akaPrimeConfigData.getFastReAuthPrefix());
			form.setAkaPrimeFastReauthRootNAI(akaPrimeConfigData.getFastReAuthRootNAI());
			form.setAkaPrimeFastReauthAAAIndentityInRootNAI(akaPrimeConfigData.getFastReAuthAAAIdentityInRootNAI());
			
			form.setAkaPrimeConfigId(akaPrimeConfigData.getConfigId());
			form.setAuditUId(eapConfigData.getAuditUId());
			form.setName(eapConfigData.getName());
		}
	}
	
	private void setGsmDataToBean(EAPGsmConfigForm eapGsmConfigForm, EAPConfigData eapConfigData){
		// set EAP sim Configuration
		if(eapGsmConfigForm.getSimFastReauthPrefix() != null){
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
			eapSimConfigData.setConfigId(eapGsmConfigForm.getSimConfigId());
			eapConfigData.setSimConfigData(eapSimConfigData);
			eapConfigData.setAuditUId(eapGsmConfigForm.getAuditUId());
			eapConfigData.setName(eapGsmConfigForm.getName());
		}
		
		// Set EAP AKA Configuration
		if(eapGsmConfigForm.getAkaFastReauthPrefix() != null){
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
			eapAKAConfigData.setConfigId(eapGsmConfigForm.getAkaConfigId());
			eapConfigData.setAkaConfigData(eapAKAConfigData);
			eapConfigData.setAuditUId(eapGsmConfigForm.getAuditUId());
			eapConfigData.setName(eapGsmConfigForm.getName());
		}
		
		// Set EAP AKA' Configuration
		if(eapGsmConfigForm.getAkaPrimeFastReauthPrefix() != null){
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
			eapAKAPrimeConfigData.setConfigId(eapGsmConfigForm.getAkaPrimeConfigId());
			eapConfigData.setAkaPrimeConfigData(eapAKAPrimeConfigData);
			eapConfigData.setAuditUId(eapGsmConfigForm.getAuditUId());
			eapConfigData.setName(eapGsmConfigForm.getName());
		}
	}
	
}
