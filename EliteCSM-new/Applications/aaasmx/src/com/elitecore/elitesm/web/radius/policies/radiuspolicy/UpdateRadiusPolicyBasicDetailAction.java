package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

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
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.ViewRadiusPolicyForm;

public class UpdateRadiusPolicyBasicDetailAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String UPDATE_FORWARD = "updateRadiusPolicyDetail";
	private static final String VIEW_FORWARD = "viewRadiusPolicyDetail";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String strUpdateBasicDetails = "updateBasicDetails";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_RADIUS_POLICY_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
			String strRadiusPolicyId = request.getParameter("radiusPolicyId");
			String radiusPolicyId="";

			try{
				if(Strings.isNullOrBlank(strRadiusPolicyId) == false){
					radiusPolicyId = strRadiusPolicyId;
				}
				ViewRadiusPolicyForm viewRadiusPolicyForm=(ViewRadiusPolicyForm)form;

				if(viewRadiusPolicyForm.getAction() == null){
					if(Strings.isNullOrBlank(radiusPolicyId) == false){
						IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
						radiusPolicyData.setRadiusPolicyId(radiusPolicyId);
						radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);
						request.setAttribute("radiusPolicyData",radiusPolicyData);
						viewRadiusPolicyForm = convertBeantoForm(radiusPolicyData);

						request.setAttribute("action",strUpdateBasicDetails);
						request.setAttribute("viewRadiusPolicyForm",viewRadiusPolicyForm);
					}

					return mapping.findForward(UPDATE_FORWARD);
				}else if(viewRadiusPolicyForm.getAction().equalsIgnoreCase("update")){

					RadiusPolicyData radiusPolicyData = convertFormtoBean(viewRadiusPolicyForm);
					blManager.verifyRadiusPolicyName(radiusPolicyData.getRadiusPolicyId(), radiusPolicyData.getName());

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

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

	private ViewRadiusPolicyForm convertBeantoForm(IRadiusPolicyData radiusPolicyData){

		ViewRadiusPolicyForm radiusPolicyForm = null;

		if(radiusPolicyData != null){
			radiusPolicyForm = new ViewRadiusPolicyForm();
			radiusPolicyForm.setRadiusPolicyId(radiusPolicyData.getRadiusPolicyId());
			radiusPolicyForm.setName(radiusPolicyData.getName());
			radiusPolicyForm.setDescription(radiusPolicyData.getDescription());
			radiusPolicyForm.setCreateDate(radiusPolicyData.getCreateDate());
			radiusPolicyForm.setStatusChangeDate(radiusPolicyData.getStatusChangeDate());			
			radiusPolicyForm.setCommonStatusId(radiusPolicyData.getCommonStatusId());
			radiusPolicyForm.setLastModifiedByStaffId(radiusPolicyData.getLastModifiedByStaffId());
			radiusPolicyForm.setLastUpdated(radiusPolicyData.getLastUpdated());
			radiusPolicyForm.setSystemGenerated(radiusPolicyData.getSystemGenerated());			
		}

		return radiusPolicyForm;
	}

	private RadiusPolicyData convertFormtoBean(ViewRadiusPolicyForm radiusPolicyForm){

		RadiusPolicyData radiusPolicyData = null;

		if(radiusPolicyForm != null){
			radiusPolicyData = new RadiusPolicyData();
			radiusPolicyData.setRadiusPolicyId(radiusPolicyForm.getRadiusPolicyId());
			radiusPolicyData.setName(radiusPolicyForm.getName());
			radiusPolicyData.setDescription(radiusPolicyForm.getDescription());
			radiusPolicyData.setCreateDate(radiusPolicyForm.getCreateDate());
			radiusPolicyData.setStatusChangeDate(radiusPolicyForm.getStatusChangeDate());			
			radiusPolicyData.setCommonStatusId(radiusPolicyForm.getCommonStatusId());
			radiusPolicyData.setLastModifiedByStaffId(radiusPolicyForm.getLastModifiedByStaffId());
			radiusPolicyData.setSystemGenerated(radiusPolicyForm.getSystemGenerated());
			radiusPolicyData.setLastUpdated(radiusPolicyForm.getLastUpdated());
		}

		return radiusPolicyData;
	}
}
