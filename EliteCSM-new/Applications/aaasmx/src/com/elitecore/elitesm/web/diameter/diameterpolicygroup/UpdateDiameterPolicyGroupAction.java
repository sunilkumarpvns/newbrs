package com.elitecore.elitesm.web.diameter.diameterpolicygroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpolicygroup.DiameterPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterpolicygroup.forms.DiameterPolicyGroupForm;

public class UpdateDiameterPolicyGroupAction extends BaseWebAction{
	protected static final String MODULE = "UpdateDiameterPolicyGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_POLICY_GROUP;
	private static final String INIT_UPDATE_FORWARD="initUpdateDiameterPolicyGroup";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			DiameterPolicyGroupBLManager blManager = new DiameterPolicyGroupBLManager();
			try{
				DiameterPolicyGroupForm diameterPolicyGroupForm =(DiameterPolicyGroupForm)form;
			
				String policyId = "";
				String strPolicyId = diameterPolicyGroupForm.getPolicyId();
				if(Strings.isNullOrBlank(strPolicyId) == false)
					policyId = strPolicyId;
					
				if(diameterPolicyGroupForm.getAction() == null){
					
					if(Strings.isNullOrBlank(policyId) == false){
						
						DiameterPolicyGroup diameterPolicyGroup;

						diameterPolicyGroup = blManager.getDiameterPolicyGroupDataById(policyId);

						convertBeantoForm(diameterPolicyGroup, diameterPolicyGroupForm);

						request.setAttribute("diameterPolicyGroup",diameterPolicyGroup);
						request.setAttribute("diameterPolicyGroupForm",diameterPolicyGroupForm);
					}

					return mapping.findForward(INIT_UPDATE_FORWARD);
					
				}else if(diameterPolicyGroupForm.getAction().equalsIgnoreCase("update")){
					
					DiameterPolicyGroup diameterPolicyGroup;
					
					diameterPolicyGroup = blManager.getDiameterPolicyGroupDataById(policyId);
					
					convertFormtoBean(diameterPolicyGroupForm, diameterPolicyGroup);
					blManager.verifyDiameterPolicyName(diameterPolicyGroup.getPolicyId(), diameterPolicyGroup.getPolicyName());

					String defaultStaff = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					blManager.updateDiameterPolicyGroupById(diameterPolicyGroup,staffData);
					
					request.setAttribute("responseUrl","/viewDiameterPolicyGroup.do?policyId="+diameterPolicyGroup.getPolicyId());
		            ActionMessage message = new ActionMessage("diameterpolicygroup.update.success");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveMessages(request, messages);
		            return mapping.findForward(SUCCESS);
				}
			}catch(DuplicateRadiusPolicyNameException drpException){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + drpException.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(drpException);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("diameterpolicygroup.update.failure");
				ActionMessage message1 = new ActionMessage("diameterpolicygroup.update.duplicateradiuspolicygroupname");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("diameterpolicygroup.update.failure");
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

	private void convertFormtoBean(DiameterPolicyGroupForm diameterPolicyGroupForm, DiameterPolicyGroup diameterPolicyGroup) {
		diameterPolicyGroup.setPolicyId(diameterPolicyGroupForm.getPolicyId());
		diameterPolicyGroup.setPolicyName(diameterPolicyGroupForm.getPolicyname());
		diameterPolicyGroup.setExpression(diameterPolicyGroupForm.getExpression());
		diameterPolicyGroup.setAuditUId(diameterPolicyGroupForm.getAuditUId());
	}

	private void convertBeantoForm(DiameterPolicyGroup diameterPolicyGroup, DiameterPolicyGroupForm diameterPolicyGroupForm) {
		diameterPolicyGroupForm.setPolicyId(String.valueOf(diameterPolicyGroup.getPolicyId()));
		diameterPolicyGroupForm.setPolicyname(diameterPolicyGroup.getPolicyName());
		diameterPolicyGroupForm.setExpression(diameterPolicyGroup.getExpression());
		diameterPolicyGroupForm.setAuditUId(diameterPolicyGroup.getAuditUId());
	}
}
