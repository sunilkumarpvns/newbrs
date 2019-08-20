package com.elitecore.elitesm.web.systemstartup.defaultsetup.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAACaseSelectionForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupUtility;

public class CaseSelection extends BaseWebAction{

	private static final String DO_CASE_SELECTION = "DoCaseSelection";
	private static final String DEFAULT_SUBSCRIBER_CASE_SENSITIVITY = "1";
	private static final String DEFAULT_POLICY_CASE_SENSITIVITY = "2";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		EliteAAACaseSelectionForm caseSelectionForm = (EliteAAACaseSelectionForm)form;

		if((Strings.isNullOrEmpty(caseSelectionForm.getCaseSensitivityForPolicy()) == false && 
				Strings.isNullOrEmpty(caseSelectionForm.getCaseSensitivityForSubscriber()) == false)){
			
			StaffBLManager staffBLManager = new StaffBLManager();
			IStaffData staffData = staffBLManager.getStaffDataByUserName(DefaultSetupUtility.ADMIN);
			
			ConfigManager.policyCaseSensitivity = caseSelectionForm.getCaseSensitivityForPolicy();
			ConfigManager.subscriberCaseSensitivity = caseSelectionForm.getCaseSensitivityForSubscriber();
			
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			blManager.updateCaseSensitivity(caseSelectionForm.getCaseSensitivityForPolicy(),
					caseSelectionForm.getCaseSensitivityForSubscriber(), staffData);

			clearFormData(caseSelectionForm);
			return mapping.findForward("defaultEliteAAASetup");
		}
		
		String strReturnToStarupPage = "loginFailedInitFailed";
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		
		caseSelectionForm.setCaseSensitivityForPolicy(DEFAULT_POLICY_CASE_SENSITIVITY);
		caseSelectionForm.setCaseSensitivityForSubscriber(DEFAULT_SUBSCRIBER_CASE_SENSITIVITY);
		
		request.setAttribute("eliteAAACaseSelectionForm", caseSelectionForm);
		return mapping.findForward(DO_CASE_SELECTION);
	}

	private void clearFormData(EliteAAACaseSelectionForm caseSelectionForm) {
		caseSelectionForm.setCaseSensitivityForSubscriber(null);
		caseSelectionForm.setCaseSensitivityForPolicy(null);
	}
}
