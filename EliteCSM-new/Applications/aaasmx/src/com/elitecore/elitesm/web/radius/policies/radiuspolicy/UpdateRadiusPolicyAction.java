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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm;

public class UpdateRadiusPolicyAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String UPDATE_FORWARD = "updateRadiusPolicy";
	private static final String VIEW_FORWARD = "viewRadiusPolicyDetail";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String strUpdateBasicDetails = "updateRadiusPolicy";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_RADIUS_POLICY_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
			

			try{
				RadiusPolicyForm radiusPolicyForm=(RadiusPolicyForm)form;
				String radiusPolicyId ="";
				if(Strings.isNullOrBlank(radiusPolicyForm.getRadiusPolicyId()) == false)
					radiusPolicyId = radiusPolicyForm.getRadiusPolicyId();
					
				if(radiusPolicyForm.getAction() == null){
					if(Strings.isNullOrBlank(radiusPolicyId) == false){
						IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
						radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);
						radiusPolicyForm = convertBeantoForm(radiusPolicyData);

						request.setAttribute("radiusPolicyData",radiusPolicyData);
						request.setAttribute("action",strUpdateBasicDetails);
						request.setAttribute("radiusPolicyForm",radiusPolicyForm);
					}

					return mapping.findForward(UPDATE_FORWARD);
				}else if(radiusPolicyForm.getAction().equalsIgnoreCase("update")){

					RadiusPolicyData radiusPolicyData = new RadiusPolicyData();
					radiusPolicyData = (RadiusPolicyData) blManager.getRadiusPolicyDataById(radiusPolicyId);
					
					radiusPolicyData = convertFormtoBean(radiusPolicyForm);
					blManager.verifyRadiusPolicyName(radiusPolicyData.getRadiusPolicyId(), radiusPolicyData.getName());

					String defaultStaff = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
					radiusPolicyData.setLastModifiedByStaffId(defaultStaff);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					radiusPolicyData.setRadiusPolicyTimePeriodList(blManager.getRadiusPolicyTimePeriodList(radiusPolicyForm));
					
					blManager.updateBasicDetailsById(radiusPolicyData, staffData);
					return mapping.findForward(VIEW_FORWARD);				
				}

			}catch(DuplicateRadiusPolicyNameException drpException){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + drpException.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(drpException);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("radiuspolicy.update.failure");
				ActionMessage message1 = new ActionMessage("radiuspolicy.update.duplicateradiuspolicyname");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE_FORWARD);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("radiuspolicy.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

				return mapping.findForward(FAILURE_FORWARD);
			}
			return mapping.findForward(SUCCESS_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private RadiusPolicyForm convertBeantoForm(IRadiusPolicyData radiusPolicyData){

		RadiusPolicyForm radiusPolicyForm = null;
		if(radiusPolicyData != null){
			radiusPolicyForm = new RadiusPolicyForm();
			radiusPolicyForm.setRadiusPolicyId(radiusPolicyData.getRadiusPolicyId());
			radiusPolicyForm.setName(radiusPolicyData.getName());
			radiusPolicyForm.setDescription(radiusPolicyData.getDescription());
			radiusPolicyForm.setCreateDate(radiusPolicyData.getCreateDate().getTime());
			radiusPolicyForm.setCommonStatusId(radiusPolicyData.getCommonStatusId());
			radiusPolicyForm.setSystemGenerated(radiusPolicyData.getSystemGenerated());
			radiusPolicyForm.setCreatedByStaffId(radiusPolicyData.getCreatedByStaffId());
			radiusPolicyForm.setStatusChangeDate(radiusPolicyData.getStatusChangeDate().getTime());
			radiusPolicyForm.setCheckItem(radiusPolicyData.getCheckItem());
			radiusPolicyForm.setAddItem(radiusPolicyData.getAddItem());
			radiusPolicyForm.setRejectItem(radiusPolicyData.getRejectItem());
			radiusPolicyForm.setReplyItem(radiusPolicyData.getReplyItem());
			radiusPolicyForm.setEditable(radiusPolicyData.getEditable());
			if(BaseConstant.SHOW_STATUS_ID.equals(radiusPolicyData.getCommonStatusId())){
				radiusPolicyForm.setStrStatus("1");
			} else {
				radiusPolicyForm.setStrStatus("0");		
			}
			radiusPolicyForm.setAuditUId(radiusPolicyData.getAuditUId());
		}
		return radiusPolicyForm;
	}

	private RadiusPolicyData convertFormtoBean(RadiusPolicyForm radiusPolicyForm){

		RadiusPolicyData radiusPolicyData = null;

		if(radiusPolicyForm != null){
			Date currentDate = new Date();
			radiusPolicyData = new RadiusPolicyData();
			radiusPolicyData.setRadiusPolicyId(radiusPolicyForm.getRadiusPolicyId());
			radiusPolicyData.setName(radiusPolicyForm.getName());
			radiusPolicyData.setDescription(radiusPolicyForm.getDescription());
			radiusPolicyData.setCreateDate(new Timestamp(radiusPolicyForm.getCreateDate()));
			radiusPolicyData.setSystemGenerated(radiusPolicyForm.getSystemGenerated());
			radiusPolicyData.setSystemGenerated(radiusPolicyForm.getSystemGenerated());
			radiusPolicyData.setCreatedByStaffId(radiusPolicyForm.getCreatedByStaffId());
			radiusPolicyData.setEditable(radiusPolicyForm.getEditable());
			radiusPolicyData.setLastUpdated(new Timestamp(currentDate.getTime()));
			radiusPolicyData.setCheckItem(radiusPolicyForm.getCheckItem());
			radiusPolicyData.setAddItem(radiusPolicyForm.getAddItem());
			radiusPolicyData.setRejectItem(radiusPolicyForm.getRejectItem());
			radiusPolicyData.setReplyItem(radiusPolicyForm.getReplyItem());
			radiusPolicyData.setAuditUId(radiusPolicyForm.getAuditUId());
			
			
			if(radiusPolicyForm.getStrStatus() != null &&  BaseConstant.HIDE_STATUS_ID.equals(radiusPolicyForm.getCommonStatusId())) {
				radiusPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
				radiusPolicyData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
			} else if(radiusPolicyForm.getStrStatus() == null && BaseConstant.SHOW_STATUS_ID.equals( radiusPolicyForm.getCommonStatusId()))  {
				radiusPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
				radiusPolicyData.setStatusChangeDate(new Timestamp(currentDate.getTime()));
			} else {
				radiusPolicyData.setCommonStatusId(radiusPolicyForm.getCommonStatusId());
				radiusPolicyData.setStatusChangeDate(new Timestamp(radiusPolicyForm.getStatusChangeDate()));
			}
		}

		return radiusPolicyData;
	}
}
