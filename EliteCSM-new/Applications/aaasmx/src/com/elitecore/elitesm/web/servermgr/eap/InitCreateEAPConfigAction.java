package com.elitecore.elitesm.web.servermgr.eap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.CreateEAPConfigForm;


public class InitCreateEAPConfigAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "createEAPConfig";
	private static final String FAILURE_FORWARD = "failure";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		request.getSession().removeAttribute("createEAPConfigDetailForm");
		request.getSession().removeAttribute("vendorSpecificCertificateMapList");
		request.getSession().removeAttribute("createEAPConfigForm");
		request.getSession().removeAttribute("enalbedMethodList");
		CreateEAPConfigForm createEAPConfigForm = (CreateEAPConfigForm) form;
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		createEAPConfigForm.setDescription(getDefaultDescription(userName));
		//createEAPConfigForm.setName("");
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		createEAPConfigForm.setDescription(getDefaultDescription(staffData.getUsername()));
        /*createEAPConfigForm.setDefaultNegiotationMethod(4);
        createEAPConfigForm.setTreatInvalidPacketAsFatal("true");
        createEAPConfigForm.setNotificationSuccess("false");
        createEAPConfigForm.setNotificationFailure("false");
        createEAPConfigForm.setMaxEapPacketSize(1024);
        createEAPConfigForm.setSessionCleanupInterval(300);
        createEAPConfigForm.setSessionDurationForCleanup(300);
        createEAPConfigForm.setSessionTimeout(3000);
        createEAPConfigForm.setEapTtlsCertificateRequest("false");*/
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		return mapping.findForward(SUCCESS_FORWARD);


	}

}
