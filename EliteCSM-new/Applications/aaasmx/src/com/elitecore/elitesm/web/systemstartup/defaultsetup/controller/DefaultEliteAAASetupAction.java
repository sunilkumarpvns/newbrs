package com.elitecore.elitesm.web.systemstartup.defaultsetup.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupUtility;

public class DefaultEliteAAASetupAction extends BaseWebAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String strReturnToStarupPage = "loginFailedInitFailed";
		
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		
		EliteAAASetupForm eliteAAASetupForm = (EliteAAASetupForm) form;

		if (DefaultSetupUtility.checkForFreshInstance()) {
			return mapping.findForward(DefaultSetupUtility.SYSTEM_STARTUP);
		} else if (Collectionz.isNullOrEmpty(DefaultSetupUtility.verifyAndCollectDuplicateModule()) == false) {
			eliteAAASetupForm.setDuplicateInstances(DefaultSetupUtility.verifyAndCollectDuplicateModule());
			request.setAttribute("eliteAAASetupForm", eliteAAASetupForm);
			return mapping.findForward(DefaultSetupUtility.FAILURE_FORWARD);
		} else if (Collectionz.isNullOrEmpty(DefaultSetupUtility.checkForXmlFiles()) == false) { 
			return mapping.findForward(DefaultSetupUtility.SYSTEM_STARTUP);
		} else {
			return mapping.findForward(DefaultSetupUtility.DEFAULT_SETUP);
		}
	}
}
