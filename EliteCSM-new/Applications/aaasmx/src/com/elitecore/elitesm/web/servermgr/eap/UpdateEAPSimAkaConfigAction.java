package com.elitecore.elitesm.web.servermgr.eap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
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
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.UpdateEAPBasicDetailsForm;

public class UpdateEAPSimAkaConfigAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateEapConfigData";
	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EAP_CONFIGURATION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
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
					if(Strings.isNullOrBlank(eapId) == false){
						EAPConfigData eapConfigData = new EAPConfigData();
						eapConfigData=eapconfigConfigBLManager.getEapConfigurationDataById(eapId);
						
						updateEAPBasicDetailsForm = convertBeanToForm(eapConfigData);
						
						String labelDefaultNegotiationMethod = convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
						eapConfigData.setEnabledAuthMethods(updateEAPBasicDetailsForm.getEnabledAuthServiceMethod());
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
		
		configData.setDefaultNegiotationMethod(updateEAPBasicDetailsForm.getDefaultNegiotationMethod());
		configData.setDescription(updateEAPBasicDetailsForm.getDescription());
		configData.setEapId(updateEAPBasicDetailsForm.getEapId());
		
    	
    	/*StringBuffer enabledMethod=new StringBuffer();
    	String[] enableAuthMethodarry=new String[8];
    	
    	if(updateEAPBasicDetailsForm.isTlsBool())
    	enableAuthMethodarry[0]="013";
    	
    	if(updateEAPBasicDetailsForm.isTtlsBool())
    	enableAuthMethodarry[1]="021";
    	
    	if(updateEAPBasicDetailsForm.isMd5Bool())
    	enableAuthMethodarry[2]="004";
    	
    	if(updateEAPBasicDetailsForm.isPeapBool())
    	enableAuthMethodarry[3]="025";
    	
    	if(updateEAPBasicDetailsForm.isSimBool())
    	enableAuthMethodarry[4]="018";
    	
    	if(updateEAPBasicDetailsForm.isAkaBool())
    	enableAuthMethodarry[5]="023";
    	
    	if(updateEAPBasicDetailsForm.isGtcBool())
    	enableAuthMethodarry[6]="006";
    	
    	if(updateEAPBasicDetailsForm.isMschapv2Bool())
    	enableAuthMethodarry[7]="026";
    	
    	
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
		
		
		
    	
     	configData.setEnabledAuthMethods(enabledMethod.toString());
*/     	
		configData.setLastModifiedDate(getCurrentTimeStemp());
		configData.setMaxEapPacketSize(updateEAPBasicDetailsForm.getMaxEapPacketSize());
		configData.setName(updateEAPBasicDetailsForm.getName());
		configData.setNotificationFailure(updateEAPBasicDetailsForm.getNotificationFailure());
		configData.setNotificationSuccess(updateEAPBasicDetailsForm.getNotificationSuccess());
		configData.setSessionCleanupInterval(updateEAPBasicDetailsForm.getSessionCleanupInterval());
		configData.setSessionDurationForCleanup(updateEAPBasicDetailsForm.getSessionDurationForCleanup());
		configData.setSessionTimeout(updateEAPBasicDetailsForm.getSessionTimeout());
		configData.setTreatInvalidPacketAsFatal(updateEAPBasicDetailsForm.getTreatInvalidPacketAsFatal());
		
		
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
		updateEAPBasicDetailsForm.setTreatInvalidPacketAsFatal(eapConfigData.getTreatInvalidPacketAsFatal());
		updateEAPBasicDetailsForm.setDefaultNegiotationMethod(eapConfigData.getDefaultNegiotationMethod());
		updateEAPBasicDetailsForm.setNotificationSuccess(eapConfigData.getNotificationSuccess());
		updateEAPBasicDetailsForm.setNotificationFailure(eapConfigData.getNotificationFailure());
		updateEAPBasicDetailsForm.setMaxEapPacketSize(eapConfigData.getMaxEapPacketSize());
		
		
		String[] labelEnableAuthMethods = convertEnableAuthMethodToLabel(eapConfigData.getEnabledAuthMethods());
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
	
	private String convertDefaultNegotiationMethodToLabel(Long defaultNegotiationMethod) {
		
		   String strdefaultNegotiationMethod="";
		    
		   
		   if(defaultNegotiationMethod == EAPConfigConstant.TLS){
			   strdefaultNegotiationMethod="TLS";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.TTLS){
			   strdefaultNegotiationMethod="TTLS";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.SIM){
			   strdefaultNegotiationMethod="SIM";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.AKA){
			   strdefaultNegotiationMethod="AKA";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.MD5_CHALLENGE){
			   strdefaultNegotiationMethod="MD5-CHALLENGE";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.MSCHAPv2){
			   strdefaultNegotiationMethod="MS-CHAPV2";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.PEAP){
			   strdefaultNegotiationMethod="PEAP";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.GTC){
			   strdefaultNegotiationMethod="GTC";
		   }else if(defaultNegotiationMethod == EAPConfigConstant.AKA_PRIME){
			   strdefaultNegotiationMethod="AKA'";
		   }
		   
		  return strdefaultNegotiationMethod; 
		   
	}





	private String[] convertEnableAuthMethodToLabel(String enabledAuthMethods) {
		
		 //Enabled Auth Methods..
		   String strEnabledAuthMethods = enabledAuthMethods;
		   String strLabelEnabledAuthMethods="";
		   String[] resultArray = new String[10];
		   boolean flag=true;
		   if(strEnabledAuthMethods != null && strEnabledAuthMethods.length()>0){
			   String[] enabledAuthMethodsArray = strEnabledAuthMethods.split(",");
			  
			   
			   for(int i=0;i<enabledAuthMethodsArray.length;i++){
				
				   if(strLabelEnabledAuthMethods.length()>0){
					
					     if("013".equals(enabledAuthMethodsArray[i])){ 
						   strLabelEnabledAuthMethods +=",TLS";
						   resultArray[1]=EAPConfigConstant.TLS_STR;
					     }else if(flag && (("021".equals(enabledAuthMethodsArray[i]) || ("025".equals(enabledAuthMethodsArray[i]))))){
					       strLabelEnabledAuthMethods +=",TTLS/PEAP";
					       resultArray[2]=EAPConfigConstant.TTLS_STR;
					       //resultArray[3]="025";
					       flag=false;
					     }else if("018".equals(enabledAuthMethodsArray[i])){
						     strLabelEnabledAuthMethods +=",SIM";
						     resultArray[4]=EAPConfigConstant.SIM_STR;
					     }else if("023".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +=",AKA";
					    	 resultArray[5]=EAPConfigConstant.AKA_STR;
					     }else if("006".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +=",GTC";
					    	 resultArray[6]=EAPConfigConstant.GTC_STR;
					     }else if("026".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +=",MS-CHAPV2";
					    	 resultArray[7]=EAPConfigConstant.MSCHAPv2_STR;
					     }else if("004".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +=",MD5";
					    	 resultArray[8]=EAPConfigConstant.MD5_CHALLENGE_STR;
					     }else if("050".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +=",AKA'";
					    	 resultArray[9]=EAPConfigConstant.AKA_PRIME_STR;
					     }
					     
						   
				   }else{
					   
					  
					     if("013".equals(enabledAuthMethodsArray[i])){
						     strLabelEnabledAuthMethods +="TLS";
						     resultArray[1]=EAPConfigConstant.TLS_STR;
					     }else if(flag && (("021".equals(enabledAuthMethodsArray[i]) || ("025".equals(enabledAuthMethodsArray[i]))))){
					    	 strLabelEnabledAuthMethods += "TTLS/PEAP";
					    	 resultArray[2]=EAPConfigConstant.TTLS_STR;
						     //resultArray[3]="025";
					    	 flag=false;
					     }else if("018".equals(enabledAuthMethodsArray[i])){
					    	strLabelEnabledAuthMethods +="SIM";
					    	resultArray[4]=EAPConfigConstant.SIM_STR;
					     }else if("023".equals(enabledAuthMethodsArray[i])){
					    	strLabelEnabledAuthMethods+="AKA";
					    	resultArray[5]=EAPConfigConstant.AKA_STR;
					     }else if("006".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +="GTC";
					    	 resultArray[6]=EAPConfigConstant.GTC_STR;
					     }else if("026".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +="MS-CHAPV2";
					    	 resultArray[7]=EAPConfigConstant.MSCHAPv2_STR;
					     }else if("004".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +="MD5";
					    	 resultArray[8]=EAPConfigConstant.MD5_CHALLENGE_STR;
					     }else if("050".equals(enabledAuthMethodsArray[i])){
					    	 strLabelEnabledAuthMethods +="AKA'";
					    	 resultArray[9]=EAPConfigConstant.AKA_PRIME_STR;
					     }
					   
							   
				   }
				   
			   }
			   
		   }
		   
		   System.out.println("Enabled Auth Method :"+strLabelEnabledAuthMethods.toString());
		   resultArray[0]=strLabelEnabledAuthMethods.toString();
		
		return resultArray;
	}
	
   
   
}
