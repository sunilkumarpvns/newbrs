package com.elitecore.elitesm.web.radius.radiuspolicygroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.radiuspolicygroup.RadiusPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm;

public class UpdateRadiusPolicyGroupAction extends BaseWebAction{
	protected static final String MODULE = "CreateRadiusPolicyGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_RADIUS_POLICY_GROUP;
	private static final String INIT_UPDATE_FORWARD="initUpdateRadiusPolicyGroup";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			RadiusPolicyGroupBLManager blManager = new RadiusPolicyGroupBLManager();

			try{
				RadiusPolicyGroupForm radiusPolicyGroupForm = (RadiusPolicyGroupForm)form;
			
				String policyId = "";
				if(Strings.isNullOrBlank(radiusPolicyGroupForm.getPolicyId()) == false)
					policyId =radiusPolicyGroupForm.getPolicyId();
					
				if(radiusPolicyGroupForm.getAction() == null){
					
					if(Strings.isNullOrBlank(policyId) == false){
						
						RadiusPolicyGroup radiusPolicyGroup;
						
						radiusPolicyGroup = blManager.getRadiusPolicyGroupById(policyId);
						convertBeantoForm(radiusPolicyGroup, radiusPolicyGroupForm);

						request.setAttribute("radiusPolicyGroup",radiusPolicyGroup);
						request.setAttribute("radiusPolicyGroupForm",radiusPolicyGroupForm);
					}

					return mapping.findForward(INIT_UPDATE_FORWARD);
					
				}else if(radiusPolicyGroupForm.getAction().equalsIgnoreCase("update")){

					RadiusPolicyGroup radiusPolicyGroup;
					
					radiusPolicyGroup = blManager.getRadiusPolicyGroupById(policyId);
					
					convertFormtoBean(radiusPolicyGroupForm, radiusPolicyGroup);
					blManager.verifyRadiusPolicyName(radiusPolicyGroup.getPolicyId(), radiusPolicyGroup.getPolicyName());

					String defaultStaff = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					blManager.updateRadiusPolicyGroupById(radiusPolicyGroup, staffData);
					
					request.setAttribute("responseUrl","/viewRadiusPolicyGroup.do?policyId="+radiusPolicyGroup.getPolicyId());
		            ActionMessage message = new ActionMessage("radiuspolicygroup.update.success");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveMessages(request, messages);
		            return mapping.findForward(SUCCESS);
				}
			}catch(DuplicateRadiusPolicyNameException drpException){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + drpException.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(drpException);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiuspolicygroup.update.failure");
				ActionMessage message1 = new ActionMessage("radiuspolicygroup.update.duplicateradiuspolicygroupname");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiuspolicygroup.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		return mapping.findForward(FAILURE);
	}

	private void convertFormtoBean(RadiusPolicyGroupForm radiusPolicyGroupForm, RadiusPolicyGroup radiusPolicyGroup) {
		radiusPolicyGroup.setPolicyId(radiusPolicyGroupForm.getPolicyId());
		radiusPolicyGroup.setPolicyName(radiusPolicyGroupForm.getPolicyname());
		radiusPolicyGroup.setExpression(radiusPolicyGroupForm.getExpression());
		radiusPolicyGroup.setAuditUId(radiusPolicyGroupForm.getAuditUId());
	}

	private void convertBeantoForm(RadiusPolicyGroup radiusPolicyGroup, RadiusPolicyGroupForm radiusPolicyGroupForm) {
		radiusPolicyGroupForm.setPolicyId(String.valueOf(radiusPolicyGroup.getPolicyId()));
		radiusPolicyGroupForm.setPolicyname(radiusPolicyGroup.getPolicyName());
		radiusPolicyGroupForm.setExpression(radiusPolicyGroup.getExpression());
		radiusPolicyGroupForm.setAuditUId(radiusPolicyGroup.getAuditUId());
	}
}
