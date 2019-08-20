package com.elitecore.elitesm.web.servermgr.eap;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.UpdateSupportedMethodsForm;

public class UpdateSupportedMethodsAction extends BaseEAPConfigAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updatesupportedmethoddata";
	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EAP_CONFIGURATION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			EAPConfigBLManager eapconfigConfigBLManager = new EAPConfigBLManager();
			
			String strEapId = request.getParameter("eapId");
		 	
			
		 	UpdateSupportedMethodsForm updateSupportedMethodsForm=(UpdateSupportedMethodsForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			
			String eapId = "";
			if(Strings.isNullOrBlank(strEapId) == false){
				eapId=strEapId;
			}else{
				eapId=updateSupportedMethodsForm.getEapId();
			}
			 

			try {
				
				if(updateSupportedMethodsForm.getAction() == null){
					if(Strings.isNullOrBlank(eapId) == false){
						EAPConfigData eapConfigData = new EAPConfigData();
						 
						eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
						
						updateSupportedMethodsForm = convertBeanToForm(eapConfigData);
						
						String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
						request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
                        request.setAttribute("eapConfigData",eapConfigData);
						request.setAttribute("updateSupportedMethodsForm",updateSupportedMethodsForm);
					}	
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateSupportedMethodsForm.getAction().equalsIgnoreCase("update")){
					EAPConfigData eapConfigData = new EAPConfigData();
					 
					eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
			
					
					eapConfigData = convertFormToBean(updateSupportedMethodsForm,eapConfigData);
                	actionAlias = ACTION_ALIAS;
                	
                	staffData.setAuditName(eapConfigData.getName());
					staffData.setAuditId(eapConfigData.getAuditUId());
                	
                	eapconfigConfigBLManager.updateSupportedAuthMethods(eapConfigData,staffData,actionAlias);
					
                	request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("responseUrl","/viewEAPConfig.do?viewType=basic&eapId="+eapId); 
					ActionMessage message = new ActionMessage("servermgr.eap.supportedmethods.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);
					
				}
			}catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.supportedmethods.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.supportedmethods.update.failure");
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

	private EAPConfigData convertFormToBean(UpdateSupportedMethodsForm updateSupportedMethodsForm,EAPConfigData configData) {

		List<String> checkedEnabledMethodsArray = new ArrayList<String>();
		List<String> unCheckedEnabledMethodsArray= new ArrayList<String>();

		configData.setEapId(updateSupportedMethodsForm.getEapId());


		StringBuffer enabledMethod=new StringBuffer();
		String[] enableAuthMethodarry=new String[9];

		if(updateSupportedMethodsForm.isTlsBool()){
			enableAuthMethodarry[0]=EAPConfigConstant.TLS_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[0]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.TLS_STR);
		}

		if(updateSupportedMethodsForm.isTtlsBool()){
			enableAuthMethodarry[1]=EAPConfigConstant.TTLS_STR;
	
			checkedEnabledMethodsArray.add(enableAuthMethodarry[1]);

		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.TTLS_STR);
	
		}

		if(updateSupportedMethodsForm.isMd5Bool()){
			enableAuthMethodarry[2]=EAPConfigConstant.MD5_CHALLENGE_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[2]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.MD5_CHALLENGE_STR);
		}	

		if(updateSupportedMethodsForm.isPeapBool()){
			enableAuthMethodarry[3]=EAPConfigConstant.PEAP_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[3]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.PEAP_STR);
		}

		if(updateSupportedMethodsForm.isSimBool()){
			enableAuthMethodarry[4]=EAPConfigConstant.SIM_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[4]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.SIM_STR);
		}
		
		if(updateSupportedMethodsForm.isAkaBool()){
			enableAuthMethodarry[5]=EAPConfigConstant.AKA_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[5]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.AKA_STR);
		}

		if(updateSupportedMethodsForm.isGtcBool()){
			enableAuthMethodarry[6]=EAPConfigConstant.GTC_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[6]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.GTC_STR);
		}	

		if(updateSupportedMethodsForm.isMschapv2Bool()){
			enableAuthMethodarry[7]=EAPConfigConstant.MSCHAPv2_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[7]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.MSCHAPv2_STR);
		}	
		
		if(updateSupportedMethodsForm.isAkaPrimeBool()){
			enableAuthMethodarry[8]=EAPConfigConstant.AKA_PRIME_STR;
			checkedEnabledMethodsArray.add(enableAuthMethodarry[8]);
		}else{
			unCheckedEnabledMethodsArray.add(EAPConfigConstant.AKA_PRIME_STR);
		}	


		int len=enableAuthMethodarry.length;
		for (int i = 0; i <len ; i++) {

			if(enableAuthMethodarry[i]!=null && enableAuthMethodarry[i].length()>0){

				if(enabledMethod.length()>0){
					enabledMethod.append(",");
					enabledMethod.append(enableAuthMethodarry[i]);
				}else{
					enabledMethod.append(enableAuthMethodarry[i]);
				}

			}

		}
        configData.setCheckedEnabledMethodsArray(checkedEnabledMethodsArray);
        configData.setUnCkeckedEnabledMethodsArray(unCheckedEnabledMethodsArray);
		configData.setEnabledAuthMethods(enabledMethod.toString());
		configData.setDefaultNegiotationMethod(updateSupportedMethodsForm.getDefaultNegiotationMethod());
		configData.setLastModifiedDate(getCurrentTimeStemp());
		configData.setAuditUId(updateSupportedMethodsForm.getAuditUId());
		return configData;

	}

	private UpdateSupportedMethodsForm convertBeanToForm(EAPConfigData eapConfigData) 
	{
		
		
        UpdateSupportedMethodsForm updateSupportedMethodsForm = new UpdateSupportedMethodsForm();
		updateSupportedMethodsForm.setEapId(eapConfigData.getEapId());
		
		String[] labelEnableAuthMethods = EAPConfigUtils.convertEnableAuthMethodToLabel(eapConfigData.getEnabledAuthMethods());
		updateSupportedMethodsForm.setEnabledAuthServiceMethod(labelEnableAuthMethods[0]);
		
		if(labelEnableAuthMethods[1]!=null){
			updateSupportedMethodsForm.setTlsBool(true);	
		}
		if(labelEnableAuthMethods[2]!=null){
			updateSupportedMethodsForm.setTtlsBool(true);	
		}
		if(labelEnableAuthMethods[3]!=null){
			updateSupportedMethodsForm.setPeapBool(true);	
		}
		if(labelEnableAuthMethods[4]!=null){
			updateSupportedMethodsForm.setSimBool(true);	
		}
		if(labelEnableAuthMethods[5]!=null){
			updateSupportedMethodsForm.setAkaBool(true);	
		}
		if(labelEnableAuthMethods[6]!=null){
			updateSupportedMethodsForm.setGtcBool(true);	
		}
		if(labelEnableAuthMethods[7]!=null){
			updateSupportedMethodsForm.setMschapv2Bool(true);	
		}
		if(labelEnableAuthMethods[8]!=null){
			updateSupportedMethodsForm.setMd5Bool(true);	
		}
		if(labelEnableAuthMethods[9]!=null){
			updateSupportedMethodsForm.setAkaPrimeBool(true);	
		}
		
		
		
		/// Set Default Negotiation Methods....
		updateSupportedMethodsForm.setDefaultNegiotationMethod(eapConfigData.getDefaultNegiotationMethod());
		updateSupportedMethodsForm.setAuditUId(eapConfigData.getAuditUId());
		
		return updateSupportedMethodsForm;
		
	}
	

	
   
   
}
