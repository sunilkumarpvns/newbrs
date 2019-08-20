/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UpdateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy; 

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyTimePeriod;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm;

public class UpdateDiameterPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_AUTHORIZATION_POLICY_BASIC_DETAIL;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			Date currentDate = new Date();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterPolicyBLManager blManager = new DiameterPolicyBLManager();			
			
			UpdateDiameterPolicyForm updateDiameterPolicyForm = (UpdateDiameterPolicyForm)form;				

			DiameterPolicyData diameterPolicyData = blManager.getDiameterPolicyDataById(updateDiameterPolicyForm.getDiameterPolicyId());

			diameterPolicyData = convertFormToBean(updateDiameterPolicyForm,diameterPolicyData);
			
			diameterPolicyData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
			diameterPolicyData.setLastModifiedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			diameterPolicyData.setDiameterPolicyTimePeriodList(getDiameterPolicyTimePeriodList(updateDiameterPolicyForm));
			blManager.updateById(diameterPolicyData,staffData);

			request.setAttribute("diameterPolicyData",diameterPolicyData);
			request.setAttribute("policyForm",updateDiameterPolicyForm);
			request.setAttribute("responseUrl", "/initSearchDiameterPolicy"); 
			ActionMessage message = new ActionMessage("diameter.diameterpolicy.update");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);

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
	        ActionMessage message = new ActionMessage("diameter.eappolicy.duplicate");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information",message);
	        saveErrors(request,messages);
	   }catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.diameterpolicy.updateerror");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private List<DiameterPolicyTimePeriod> getDiameterPolicyTimePeriodList( UpdateDiameterPolicyForm diameterPolicyForm ) {
		String[] monthOfYear = diameterPolicyForm.getMonthOfYear();
		String[] dayOfMonth = diameterPolicyForm.getDayOfMonth();
		String[] dayOfWeek = diameterPolicyForm.getDayOfWeek();
		String[] timePeriod = diameterPolicyForm.getTimePeriod();
		
		if(monthOfYear == null || dayOfMonth == null || dayOfWeek == null || timePeriod == null) {
			return null;
		} else {
			int length = monthOfYear.length;
			if(length != dayOfMonth.length || length != dayOfWeek.length || length != timePeriod.length)
				return null;
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList = new ArrayList<DiameterPolicyTimePeriod>(length);
			for(int i =0 ; i<length ; i++) {
				DiameterPolicyTimePeriod diameterPolicyTimePeriod = new DiameterPolicyTimePeriod();
				if(monthOfYear[i].trim().length() > 0){
					diameterPolicyTimePeriod.setMonthOfYear(monthOfYear[i]);
				}
				if(dayOfMonth[i].trim().length() > 0){
					diameterPolicyTimePeriod.setDayOfMonth(dayOfMonth[i]);
				}
				if(dayOfWeek[i].trim().length() > 0){
					diameterPolicyTimePeriod.setDayOfWeek(dayOfWeek[i]);
				}
				if(timePeriod[i].trim().length() > 0){
					diameterPolicyTimePeriod.setTimePeriod(timePeriod[i]);
				}
				diameterPolicyTimePeriodList.add(diameterPolicyTimePeriod);
			}
			return diameterPolicyTimePeriodList;
		}
	}

	private DiameterPolicyData convertFormToBean(UpdateDiameterPolicyForm updateDiameterPolicyForm,DiameterPolicyData diameterPolicyData) {
		diameterPolicyData.setDiameterPolicyId(updateDiameterPolicyForm.getDiameterPolicyId());
		diameterPolicyData.setCheckItem(updateDiameterPolicyForm.getCheckItem());
		diameterPolicyData.setRejectItem(updateDiameterPolicyForm.getRejectItem());
		diameterPolicyData.setReplyItem(updateDiameterPolicyForm.getReplyItem());
		if(updateDiameterPolicyForm.getStatus() != null ) {
			diameterPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
		} else {
			diameterPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);  
		}
		diameterPolicyData.setName(updateDiameterPolicyForm.getName());
		diameterPolicyData.setDescription(updateDiameterPolicyForm.getDescription());
		diameterPolicyData.setLastModifiedDate(getCurrentTimeStemp());
		diameterPolicyData.setAuditUId(updateDiameterPolicyForm.getAuditUId());
		
		return diameterPolicyData;
	}
}
