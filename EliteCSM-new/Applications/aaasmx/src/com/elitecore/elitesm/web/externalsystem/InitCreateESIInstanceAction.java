package com.elitecore.elitesm.web.externalsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.CreateESIInstanceForm;

	public class InitCreateESIInstanceAction extends BaseWebAction{
	
		private static final String SUCCESS_FORWARD = "CreateESIInstance";
		private static final String FAILURE_FORWARD = "failure";
		private static final String ACTION_ALIAS = "CREATE_ESI_INSTANCE_ACTION";
		private static final String MODULE = "CREATE_ESI_INSTANCE_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
		List esiTypeList = esiBLManager.getListOfESIType();
		CreateESIInstanceForm esiInstanceForm = (CreateESIInstanceForm)form;
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		esiInstanceForm.setDesc(getDefaultDescription(userName));
		esiInstanceForm.setEsiTypeList(esiTypeList);
		
		return mapping.findForward(SUCCESS_FORWARD);
		
	}
	

}
