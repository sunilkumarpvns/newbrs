package com.elitecore.elitesm.web.externalsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.SearchESIInstanceForm;

public class InitSearchESIInstanceAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "SearchESIInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_EXTERNAL_SYSTEM;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		// for obtaining esiType list ...
		ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
		
		List<ExternalSystemInterfaceTypeData> esiTypeList = esiBLManager.getListOfESIType();
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
		SearchESIInstanceForm searchESIInstanceForm = (SearchESIInstanceForm)form;		
		request.setAttribute("searchESIInstance", searchESIInstanceForm);
		searchESIInstanceForm.setEsiTypeList(esiTypeList);
		searchESIInstanceForm.setAction(" ");
		doAuditing(staffData, ACTION_ALIAS);

		///
		return mapping.findForward(SUCCESS_FORWARD);
	}
		
}
