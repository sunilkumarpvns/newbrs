package com.elitecore.elitesm.web.radius.clientprofile;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.CreateClientProfileForm;


public class InitCreateClientProfileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "createClientProfile";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="INIT_CREATE_CLIENT_PROFILE_ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		ClientProfileBLManager clientProfileBLManager= new ClientProfileBLManager();
		CreateClientProfileForm createClientProfileForm=(CreateClientProfileForm)form;
		try{
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			List<ClientTypeData> lstClientType=clientProfileBLManager.getClientTypeList();
			Logger.logDebug(MODULE, "IN ACTION CLASS SIZE IS:"+lstClientType.size());
			Logger.logDebug(MODULE,"client profile form:"+createClientProfileForm);
			createClientProfileForm.setLstClientType(lstClientType);
			createClientProfileForm.setDescription(getDefaultDescription(userName));
			List<VendorData> lstVendorData=clientProfileBLManager.getVendorList();
			Collections.sort(lstVendorData);
			createClientProfileForm.setLstVendorData(lstVendorData);
			request.setAttribute("createClientProfileForm",createClientProfileForm);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(Exception exp){

			Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

}
