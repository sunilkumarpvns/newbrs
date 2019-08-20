package com.elitecore.elitesm.web.core.system.systemparameter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.systemparameter.forms.UpdateSystemParameterForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.CaseSensitivity;

public class UpdateSystemParameterAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "updateSystemParameter";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SYSTEM_PARAMETER_ACTION;
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if(checkAccess(request, ACTION_ALIAS)){

			Logger.logTrace(MODULE,"Enter the execute method of "+getClass().getName());
			String parameterId = request.getParameter("parameterid");
			ActionErrors errors = new ActionErrors();
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
			try{
				ISystemParameterData systemParameterData = new SystemParameterData();
				UpdateSystemParameterForm updateSystemParameterForm = (UpdateSystemParameterForm)form;
				PasswordPolicyConfigData passwordPolicySelectionData = new PasswordPolicyConfigData();
				String action = updateSystemParameterForm.getAction();
				boolean criteriaNotRequired=false;
				if(action != null && action.equalsIgnoreCase("update")){
					List lstparameterDetail = (List)updateSystemParameterForm.getLstParameterValue();
					
					passwordPolicySelectionData.setPasswordRange((updateSystemParameterForm.getPasswordRange() == "" ? null : updateSystemParameterForm.getPasswordRange()));
					passwordPolicySelectionData.setAlphabetRange((updateSystemParameterForm.getAplhabetsRange() == "" ? null : updateSystemParameterForm.getAplhabetsRange()));
					passwordPolicySelectionData.setDigitsRange((updateSystemParameterForm.getDigitRange() == ""? null :updateSystemParameterForm.getDigitRange()));
					passwordPolicySelectionData.setSpecialCharRange((updateSystemParameterForm.getSpeCharRange() == "" ? null :updateSystemParameterForm.getSpeCharRange()));
					passwordPolicySelectionData.setProhibitedChars((updateSystemParameterForm.getProChar() == "" ? null :updateSystemParameterForm.getProChar()));
					passwordPolicySelectionData.setPasswordPolicyId(updateSystemParameterForm.getPasswordPolicyId());
					passwordPolicySelectionData.setPasswordValidity((updateSystemParameterForm.getPwdValidity() == "" ? 0 : Integer.parseInt(updateSystemParameterForm.getPwdValidity())));
					passwordPolicySelectionData.setChangePwdOnFirstLogin(String.valueOf(updateSystemParameterForm.isChangePwdOnFirstLogin()));
					
					String maxHistoricalPasswordString = updateSystemParameterForm.getMaxHistoricalPassword();
					
					if(maxHistoricalPasswordString == null || updateSystemParameterForm.getMaxHistoricalPassword().trim().length() == 0){
						passwordPolicySelectionData.setMaxHistoricalPasswords(1);
					} else {
						passwordPolicySelectionData.setMaxHistoricalPasswords(Integer.parseInt(updateSystemParameterForm.getMaxHistoricalPassword()));
					}
					
					if(passwordPolicySelectionData.getPasswordRange() == null && passwordPolicySelectionData.getAlphabetRange() == null
								&& passwordPolicySelectionData.getDigitsRange() == null && passwordPolicySelectionData.getSpecialCharRange() == null && passwordPolicySelectionData.getPasswordValidity() == 0){
						criteriaNotRequired=true;
					}
					
					boolean isValidParam=isValidParameters(passwordPolicySelectionData);
					if(isValidParam || criteriaNotRequired ==true){
					
						
						for(int i = 0; i<lstparameterDetail.size() ; i++){
							ISystemParameterData systemParameterValueData = (ISystemParameterData)lstparameterDetail.get(i);
						}
	
						Set tempSet = new HashSet();
						tempSet.clear();
						tempSet.addAll(lstparameterDetail);
						systemParameterData.setParameterDetail(tempSet);
	
					
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						blManager.updateBasicDetail(lstparameterDetail, staffData);
						passwordBLManager.updatePolicyDetail(passwordPolicySelectionData, staffData);
						doAuditing(staffData, actionAlias);
						request.setAttribute("responseUrl","/viewSystemParameter");
						ActionMessage message = new ActionMessage("systemparameter.update.success");
						ActionMessage message1 = new ActionMessage("systemparameter.refresh.systemparameter");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						messages.add("information",message1);
						saveMessages(request,messages);
						return mapping.findForward(SUCCESS_FORWARD);
					}else{
						Logger.logError(MODULE, "Error during Data Manager operation ");
						ActionMessage message = new ActionMessage("systemparameter.pwdpolicy.range.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information", message);
						saveErrors(request, messages);

						return mapping.findForward(INVALID_ACCESS_FORWARD);
					}
				}else{
					List<ISystemParameterData> lstParameterValue = blManager.getList();

					for (ISystemParameterData parameter:lstParameterValue){
						if(CaseSensitivity.POLICY_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
							parameter.setParameterDetail(addValuePoolSetData());
						}

						if(CaseSensitivity.SUBSCRIBER_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
							parameter.setParameterDetail(addValuePoolSetData());
						}
					}
					PasswordPolicyConfigData passwordPolicyData=passwordBLManager.getPasswordSelectionPolicy();
					request.setAttribute("lstParameterValue",lstParameterValue);
					updateSystemParameterForm.setLstParameterValue(lstParameterValue);
					updateSystemParameterForm.setPasswordSelectionConfigData(passwordPolicyData);
					return mapping.findForward(LIST_FORWARD);
				}
			}
			catch (Exception managerExp) {
				managerExp.printStackTrace();
				Logger.logError(MODULE,"Error during data Manager operation,reason :"+ managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("systemparameter.update.failure")); 
			saveErrors(request,errors);
			return mapping.findForward(FAILURE_FORWARD);
		}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private boolean isValidParameters(PasswordPolicyConfigData passwordPolicyConfigData){
		boolean checkValid=true;
	    if(passwordPolicyConfigData.isAlphabetCheckReq() == true || passwordPolicyConfigData.isDigitCheckReq() == true
	    		|| passwordPolicyConfigData.isLengthCheckReq()== true || passwordPolicyConfigData.isSpecialCharCheckReq() == true){
	    	
	    	if(passwordPolicyConfigData.getPasswordRange() != null){
	    		if(passwordPolicyConfigData.getMinPasswordLength() > passwordPolicyConfigData.getMaxPasswordLength())
					checkValid=false;
	    	}
	    	
	    	if(passwordPolicyConfigData.getAlphabetRange() != null){
	    		if(passwordPolicyConfigData.getMinAlphabates() > passwordPolicyConfigData.getMaxAlphabates())
					checkValid=false;
	    	}
	    	
	    	if(passwordPolicyConfigData.getDigitsRange() != null){
	    		if(passwordPolicyConfigData.getMinDigits() > passwordPolicyConfigData.getMaxDigits())
	    			checkValid=false;
	    	}
	    	
	    	if(passwordPolicyConfigData.getSpecialCharRange() != null){
	    		if(passwordPolicyConfigData.getMinSpecialChars() > passwordPolicyConfigData.getMaxSpecialChars())
	    			checkValid=false;
	    	}
	    }else{
	    	checkValid=true;
	    }
	   return checkValid;
	}

	private Set addValuePoolSetData() {
		Set valuePoolSet = new LinkedHashSet();
		for(CaseSensitivity caseSensitivity: CaseSensitivity.values()){
            SystemParameterValuePoolData valuePoolData = new SystemParameterValuePoolData();
			valuePoolData.setName(caseSensitivity.name);
            valuePoolData.setValue(caseSensitivity.id);
            valuePoolSet.add(valuePoolData);
        }
		return valuePoolSet;
	}
}