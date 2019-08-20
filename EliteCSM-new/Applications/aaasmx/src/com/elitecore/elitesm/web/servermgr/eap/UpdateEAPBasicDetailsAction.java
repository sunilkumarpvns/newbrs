package com.elitecore.elitesm.web.servermgr.eap;

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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.UpdateEAPBasicDetailsForm;

public class UpdateEAPBasicDetailsAction extends BaseEAPConfigAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateEapConfigData";
	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EAP_CONFIGURATION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());

			EAPConfigBLManager eapconfigConfigBLManager = new EAPConfigBLManager();
			
			String strEapId = request.getParameter("eapId");
		 	UpdateEAPBasicDetailsForm updateEAPBasicDetailsForm = (UpdateEAPBasicDetailsForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			
			String eapId = "";
			if(Strings.isNullOrBlank(strEapId) == false){
				eapId=strEapId;
			}else{
				eapId=updateEAPBasicDetailsForm.getEapId();
			}
			 

			try {
				
				if(updateEAPBasicDetailsForm.getAction() == null){
					if(eapId != null){
						EAPConfigData eapConfigData = new EAPConfigData();
						 
						eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
						
						updateEAPBasicDetailsForm = convertBeanToForm(eapConfigData);
						
						String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
						request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
                        request.setAttribute("eapConfigData",eapConfigData);
						request.setAttribute("updateEAPBasicDetailsForm",updateEAPBasicDetailsForm);
					}	
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateEAPBasicDetailsForm.getAction().equalsIgnoreCase("update")){
					EAPConfigData eapConfigData = new EAPConfigData();
					 
					eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
					eapConfigData = convertFormToBean(updateEAPBasicDetailsForm,eapConfigData);
                	actionAlias = ACTION_ALIAS;
                	
                	staffData.setAuditName(eapConfigData.getName());
					staffData.setAuditId(eapConfigData.getAuditUId());
                	
                	eapconfigConfigBLManager.updateEapBasicDetails(eapConfigData,staffData,actionAlias);
					
                	request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("responseUrl","/viewEAPConfig.do?viewType=basic&eapId="+eapId); 
					ActionMessage message = new ActionMessage("servermgr.eap.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);
					
				}
			}catch (DuplicateInstanceNameFoundException dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		        Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("servermgr.eap.update.duplicate.failure",updateEAPBasicDetailsForm.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.eap.update.failure");
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

	private EAPConfigData convertFormToBean(UpdateEAPBasicDetailsForm updateEAPBasicDetailsForm,EAPConfigData configData) {
		
		//configData.setDefaultNegiotationMethod(updateEAPBasicDetailsForm.getDefaultNegiotationMethod());
		configData.setDescription(updateEAPBasicDetailsForm.getDescription());
		configData.setEapId(updateEAPBasicDetailsForm.getEapId());
		configData.setLastModifiedDate(getCurrentTimeStemp());
		configData.setMaxEapPacketSize(updateEAPBasicDetailsForm.getMaxEapPacketSize());
		configData.setName(updateEAPBasicDetailsForm.getName());
		configData.setNotificationFailure(updateEAPBasicDetailsForm.getNotificationFailure());
		configData.setNotificationSuccess(updateEAPBasicDetailsForm.getNotificationSuccess());
		configData.setSessionCleanupInterval(updateEAPBasicDetailsForm.getSessionCleanupInterval());
		configData.setSessionDurationForCleanup(updateEAPBasicDetailsForm.getSessionDurationForCleanup());
		configData.setSessionTimeout(updateEAPBasicDetailsForm.getSessionTimeout());
		
		if( updateEAPBasicDetailsForm.getMskRevalidationTime() == null || updateEAPBasicDetailsForm.getMskRevalidationTime().length() <= 0 ){
			configData.setMskRevalidationTime(null);
		}else{
			configData.setMskRevalidationTime(Long.parseLong(updateEAPBasicDetailsForm.getMskRevalidationTime()));
		}
		
		configData.setTreatInvalidPacketAsFatal(updateEAPBasicDetailsForm.getTreatInvalidPacketAsFatal());
		configData.setAuditUId(updateEAPBasicDetailsForm.getAuditUId());
		
		return configData;
		
	}

	private UpdateEAPBasicDetailsForm convertBeanToForm(EAPConfigData eapConfigData) 
	{
		UpdateEAPBasicDetailsForm updateEAPBasicDetailsForm = new UpdateEAPBasicDetailsForm();
        updateEAPBasicDetailsForm.setEapId(eapConfigData.getEapId());
		updateEAPBasicDetailsForm.setName(eapConfigData.getName());
		updateEAPBasicDetailsForm.setDescription(eapConfigData.getDescription());
		updateEAPBasicDetailsForm.setSessionCleanupInterval(eapConfigData.getSessionCleanupInterval());
		updateEAPBasicDetailsForm.setSessionDurationForCleanup(eapConfigData.getSessionDurationForCleanup());
		updateEAPBasicDetailsForm.setSessionTimeout(eapConfigData.getSessionTimeout());
		
		if( eapConfigData.getMskRevalidationTime() == null){
			updateEAPBasicDetailsForm.setMskRevalidationTime(null);
		}else{
			updateEAPBasicDetailsForm.setMskRevalidationTime(eapConfigData.getMskRevalidationTime().toString());
		}
		
		updateEAPBasicDetailsForm.setTreatInvalidPacketAsFatal(eapConfigData.getTreatInvalidPacketAsFatal());
		updateEAPBasicDetailsForm.setDefaultNegiotationMethod(eapConfigData.getDefaultNegiotationMethod());
		updateEAPBasicDetailsForm.setNotificationSuccess(eapConfigData.getNotificationSuccess());
		updateEAPBasicDetailsForm.setNotificationFailure(eapConfigData.getNotificationFailure());
		updateEAPBasicDetailsForm.setMaxEapPacketSize(eapConfigData.getMaxEapPacketSize());
		updateEAPBasicDetailsForm.setAuditUId(eapConfigData.getAuditUId());
		
		String[] labelEnableAuthMethods = EAPConfigUtils.convertEnableAuthMethodToLabel(eapConfigData.getEnabledAuthMethods());
		updateEAPBasicDetailsForm.setEnabledAuthServiceMethod(labelEnableAuthMethods[0]);
		if(labelEnableAuthMethods[1]!=null){
			updateEAPBasicDetailsForm.setTlsBool(true);	
		}
		if(labelEnableAuthMethods[2]!=null){
			updateEAPBasicDetailsForm.setTtlsBool(true);	
		}
		if(labelEnableAuthMethods[3]!=null){
			updateEAPBasicDetailsForm.setPeapBool(true);	
		}
		if(labelEnableAuthMethods[4]!=null){
			updateEAPBasicDetailsForm.setSimBool(true);	
		}
		if(labelEnableAuthMethods[5]!=null){
			updateEAPBasicDetailsForm.setAkaBool(true);	
		}
		if(labelEnableAuthMethods[6]!=null){
			updateEAPBasicDetailsForm.setGtcBool(true);	
		}
		if(labelEnableAuthMethods[7]!=null){
			updateEAPBasicDetailsForm.setMschapv2Bool(true);	
		}
		if(labelEnableAuthMethods[8]!=null){
			updateEAPBasicDetailsForm.setMd5Bool(true);	
		}
		if(labelEnableAuthMethods[9]!=null){
			updateEAPBasicDetailsForm.setAkaPrimeBool(true);	
		}
		
		
		
		/*
		 * check for configuration exist or not
		 *  tls/ttls configuration 
		 * 
		 */
		 
		  boolean isTtlsConfigurationExist=false;
		  if(eapConfigData.getEaptlsConfigData() != null){
			  isTtlsConfigurationExist=true;
		  }
		  updateEAPBasicDetailsForm.setTtlsConfigurationExist(isTtlsConfigurationExist);
		
		return updateEAPBasicDetailsForm;
		
	}
}
