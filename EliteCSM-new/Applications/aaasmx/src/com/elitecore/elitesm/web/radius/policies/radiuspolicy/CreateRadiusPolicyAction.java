package com.elitecore.elitesm.web.radius.policies.radiuspolicy;


import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm;


public class CreateRadiusPolicyAction extends BaseDictionaryAction {
	protected static final String MODULE = "RADIUSPOLICY ACTION";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_RADIUS_POLICY_ACTION;

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{

		if(checkAccess(request, ACTION_ALIAS)){  
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			RadiusPolicyForm radiusPolicyForm = (RadiusPolicyForm) form;	
			try{
				String defaultStaff = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				if(radiusPolicyForm != null) {
					RadiusPolicyData radiusPolicyData = convertFormToBean(radiusPolicyForm);
					radiusPolicyData.setLastModifiedByStaffId(defaultStaff);
					radiusPolicyData.setCreatedByStaffId(defaultStaff);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();	
					radiusPolicyData.setRadiusPolicyTimePeriodList(blManager.getRadiusPolicyTimePeriodList(radiusPolicyForm));
					blManager.create(radiusPolicyData, staffData, false);
					request.setAttribute("responseUrl","/initSearchRadiusPolicy.do");
					ActionMessage message = new ActionMessage("radiuspolicy.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);
				} 
				return mapping.findForward("CREATE_FORWARD");
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiuspolicy.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");

			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		//TODO : Baiju - set error message and then forward to error page.

	}
	
	public void print(String[] strArray ) {
		for(String str : strArray){
			System.out.print(str+",");
		}
	}
	
	
	public RadiusPolicyData convertFormToBean(RadiusPolicyForm radiusPolicyForm){
		RadiusPolicyData radiusPolicyData = new RadiusPolicyData();
		radiusPolicyData.setName(radiusPolicyForm.getName());
		radiusPolicyData.setDescription(radiusPolicyForm.getDescription());
		if(radiusPolicyForm.getStrStatus() != null ) {
			radiusPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
		} else {
			radiusPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
		}
		Date currentDate = new Date();
		radiusPolicyData.setCreateDate(new Timestamp(currentDate.getTime()));
		radiusPolicyData.setLastUpdated(new Timestamp(currentDate.getTime()));
		radiusPolicyData.setSystemGenerated("N");
		radiusPolicyData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
		radiusPolicyData.setEditable("Y");
		radiusPolicyData.setCheckItem(radiusPolicyForm.getCheckItem());
		radiusPolicyData.setAddItem(radiusPolicyForm.getAddItem());
		radiusPolicyData.setRejectItem(radiusPolicyForm.getRejectItem());
		radiusPolicyData.setReplyItem(radiusPolicyForm.getReplyItem());
		return radiusPolicyData;
	}

}
