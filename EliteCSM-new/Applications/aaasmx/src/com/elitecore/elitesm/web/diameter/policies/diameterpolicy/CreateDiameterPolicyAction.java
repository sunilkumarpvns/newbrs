/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateDiameterpolicyAction.java                 		
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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyTimePeriod;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.CreateDiameterPolicyForm;

public class CreateDiameterPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_AUTHORIZATION_POLICY; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			CreateDiameterPolicyForm diameterPolicyForm = (CreateDiameterPolicyForm)form; 
			DiameterPolicyBLManager diameterpolicyBLManager = new DiameterPolicyBLManager();
			
			DiameterPolicyData diameterPolicyData = new DiameterPolicyData();
			Date currentDate = new Date();
			
			convertFromToBean(diameterPolicyForm,diameterPolicyData);
			diameterPolicyData.setDiameterPolicyTimePeriodList(getDiameterPolicyTimePeriodList(diameterPolicyForm));
			diameterPolicyData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
			diameterPolicyData.setLastModifiedByStaffId(currentUser);
			diameterPolicyData.setCreatedByStaffId(currentUser);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			diameterpolicyBLManager.create(diameterPolicyData, staffData, false);
			doAuditing(staffData, ACTION_ALIAS);
			request.setAttribute("diameterPolicyData", "diameterPolicyData");
			request.setAttribute("diameterPolicyForm", "diameterPolicyForm");
			
			request.setAttribute("responseUrl", "/initSearchDiameterPolicy");    
			ActionMessage message = new ActionMessage("diameter.policy.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);             
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.policy.create.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private List<DiameterPolicyTimePeriod> getDiameterPolicyTimePeriodList( CreateDiameterPolicyForm diameterPolicyForm ) {
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

	private void convertFromToBean(CreateDiameterPolicyForm form,DiameterPolicyData data) {
		data.setCheckItem(form.getCheckItem());
		data.setRejectItem(form.getRejectItem());
		data.setReplyItem(form.getReplyItem());
		if(form.getStatus() != null ) {
			data.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
		} else {
			data.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);  
		}
		
		data.setName(form.getName());
		data.setDescription(form.getDescription());
		data.setCreateDate(getCurrentTimeStemp());
		data.setLastModifiedDate(getCurrentTimeStemp());
		data.setSystemGenerated("N");
		data.setEditable("Y");
	}           
}
