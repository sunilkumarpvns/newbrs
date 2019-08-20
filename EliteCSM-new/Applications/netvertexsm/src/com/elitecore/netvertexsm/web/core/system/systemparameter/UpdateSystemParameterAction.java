package com.elitecore.netvertexsm.web.core.system.systemparameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.systemparameter.forms.UpdateSystemParameterForm;

public class UpdateSystemParameterAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "updateSystemParameter";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SYSTEM_PARAMETER_ACTION;
	private static final String SHORT_DATE_FORMAT = "Short Date Format";
	private static final String DATE_FORMAT = "Date Format";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter the execute method of "+getClass().getName());
			ActionErrors errors = new ActionErrors();
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			PasswordSelectionPolicyBLManager passwordBLManager = new PasswordSelectionPolicyBLManager();
			
			try{
				ISystemParameterData systemParameterData = new SystemParameterData();
				UpdateSystemParameterForm updateSystemParameterForm = (UpdateSystemParameterForm)form;
				PasswordPolicyConfigData passwordPolicySelectionData = new PasswordPolicyConfigData();
				String action = updateSystemParameterForm.getAction();
				boolean criteriaNotRequired=false;
				
				if(action != null && action.equalsIgnoreCase("update")){
					List lstparameterDetail = (List)updateSystemParameterForm.getLstParameterValue();

					passwordPolicySelectionData.setPasswordRange((updateSystemParameterForm.getPasswordRange() == "" ? null : updateSystemParameterForm.getPasswordRange().trim()));
					if(updateSystemParameterForm.getAplhabetsRange()!=null && updateSystemParameterForm.getAplhabetsRange().trim().length()>0){
						passwordPolicySelectionData.setAlphabetRange(Integer.parseInt(updateSystemParameterForm.getAplhabetsRange().trim()));
					}
					
					if(updateSystemParameterForm.getDigitRange() != null && updateSystemParameterForm.getDigitRange().trim().length()>0){						 
						passwordPolicySelectionData.setDigitsRange(Integer.parseInt(updateSystemParameterForm.getDigitRange().trim()));
					}
					
					if(updateSystemParameterForm.getSpeCharRange() != null  && updateSystemParameterForm.getSpeCharRange().trim().length()>0){						 
						passwordPolicySelectionData.setSpecialCharRange(Integer.parseInt(updateSystemParameterForm.getSpeCharRange().trim()));
					}
					passwordPolicySelectionData.setProhibitedChars((updateSystemParameterForm.getProChar() == "" ? null :updateSystemParameterForm.getProChar()));
					passwordPolicySelectionData.setPasswordPolicyId(Long.parseLong(updateSystemParameterForm.getPasswordPolicyId().trim()));
					passwordPolicySelectionData.setPasswordValidity((updateSystemParameterForm.getPwdValidity() == "" ? 0 : Integer.parseInt(updateSystemParameterForm.getPwdValidity().trim())));
					passwordPolicySelectionData.setChangePwdOnFirstLogin(String.valueOf(updateSystemParameterForm.isChangePwdOnFirstLogin()));
					passwordPolicySelectionData.setTotalHistoricalPasswords(updateSystemParameterForm.getTotalHistoricalPasswords()==""? 0 : Integer.parseInt(updateSystemParameterForm.getTotalHistoricalPasswords().trim()));
					
					if(passwordPolicySelectionData.getPasswordRange() == null && passwordPolicySelectionData.getAlphabetRange() == null
								&& passwordPolicySelectionData.getDigitsRange() == null && passwordPolicySelectionData.getSpecialCharRange() == null && passwordPolicySelectionData.getPasswordValidity() == 0){
						criteriaNotRequired=true;
					}
					
					boolean isValidParam=isValidParameters(passwordPolicySelectionData);
					if(isValidParam || criteriaNotRequired ==true){
        					for(int i = 0; i<lstparameterDetail.size() ; i++){
        						ISystemParameterData systemParameterValueData = (ISystemParameterData)lstparameterDetail.get(i);
        						String paramName = systemParameterValueData.getName();
        						String paramValue = systemParameterValueData.getValue();
        						if(DATE_FORMAT.equals(paramName)){
        							try{
        								validateDateFormat(paramValue);
        							}catch(Exception e){
        								getErrorMessage(request, e,DATE_FORMAT);
        								return mapping.findForward(FAILURE_FORWARD);
        							}
        						}
        						if(SHORT_DATE_FORMAT.equals(paramName)){
        							try{
        								validateDateFormat(paramValue);
        							}catch(Exception e){
        								getErrorMessage(request, e,SHORT_DATE_FORMAT);
        								return mapping.findForward(FAILURE_FORWARD);
        							}
        						}
        					}
        
        					Set tempSet = new HashSet();
        					tempSet.clear();
        					tempSet.addAll(lstparameterDetail);
        					systemParameterData.setParameterDetail(tempSet);
        
        
        					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        					String actionAlias = ACTION_ALIAS;
        					blManager.updateBasicDetail(lstparameterDetail,staffData,actionAlias);
        					passwordBLManager.updatePolicyDetail(passwordPolicySelectionData);
        					
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
					}
 
				}else{
					List<ISystemParameterData> lstParameterValue = blManager.getList();
					int i = 1;
					for(ISystemParameterData data : lstParameterValue){
						List<SystemParameterValuePoolData> valuePool = blManager.getSystemParameterValuePoolForParameter(data.getParameterId());
						Set<SystemParameterValuePoolData> valuePoolSet = new HashSet<SystemParameterValuePoolData>(valuePool);
						if(Collectionz.isNullOrEmpty(valuePool) == false){
							updateSystemParameterForm.setSystemParameterValuePoolData(i, valuePoolSet);
						}
						i++;
					}
					request.setAttribute("lstParameterValue",lstParameterValue);
					updateSystemParameterForm.setLstParameterValue(lstParameterValue);
					PasswordPolicyConfigData passwordPolicyData=passwordBLManager.viewPasswordSelectionPolicy();
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
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("systemparameter.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(FAILURE_FORWARD);
		}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("systemparameter.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
 

	private void getErrorMessage(HttpServletRequest request, Exception e,String parameter) {
		Logger.logError(MODULE, "Error during parsing Date Format. Reason: ");
		Logger.logTrace(MODULE,e);
		ActionMessage message = new ActionMessage("systemparameter.dateformat.failure",parameter);
		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
		request.setAttribute("errorDetails", errorElements);
		ActionMessages messages = new ActionMessages();
		messages.add("information", message);
		saveErrors(request, messages);
		
		ActionMessages errorHeadingMessage = new ActionMessages();
		message = new ActionMessage("systemparameter.error.heading","updating");
		errorHeadingMessage.add("errorHeading",message);
		saveMessages(request,errorHeadingMessage);
	}
	
	private void validateDateFormat(String paramValue) throws ParseException,IllegalArgumentException {
		if(Strings.isNullOrBlank(paramValue)){
			throw new IllegalArgumentException("Can not configure empty string with date parameter");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(paramValue);
		String dateAsStr = sdf.format(new Date());
		sdf.setLenient(false);
		sdf.parse(dateAsStr);
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
	    		if(passwordPolicyConfigData.getAlphabetRange() > passwordPolicyConfigData.getMaxPasswordLength())
					checkValid=false;
	    	}
	    	
	    	if(passwordPolicyConfigData.getDigitsRange() != null){
	    		if(passwordPolicyConfigData.getDigitsRange() > passwordPolicyConfigData.getMaxPasswordLength())
	    			checkValid=false;
	    	}
	    	
	    	if(passwordPolicyConfigData.getSpecialCharRange() != null){
	    		if(passwordPolicyConfigData.getSpecialCharRange() > passwordPolicyConfigData.getMaxPasswordLength())
	    			checkValid=false;
	    	}
	    }else{
	    	checkValid=true;
	    }
	   return checkValid;
	}	
 
}



