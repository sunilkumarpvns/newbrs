package com.elitecore.elitesm.web.radius.clientprofile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.SearchClientProfileForm;


public class InitSearchClientProfileAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "searchClientProfile";
	private static final String MODULE    = "Search Client Profile";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CLIENT_PROFILE;
		
	public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) {
		Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
		ActionMessages messages = new ActionMessages();
		try{
            SearchClientProfileForm searchClientProfileForm =(SearchClientProfileForm)form;
        	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
            ClientProfileBLManager profileBLManager = new ClientProfileBLManager();
			List clientTypeListInCombo = profileBLManager.getClientTypeList();
			List vendorListInCombo = profileBLManager.getVendorList();
			
			searchClientProfileForm.setClientTypeList(clientTypeListInCombo);
			searchClientProfileForm.setVendorList(vendorListInCombo);
			doAuditing(staffData, ACTION_ALIAS);
		}catch(DataManagerException dme){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + dme.getMessage());
			Logger.logTrace(MODULE, dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
		}
		return mapping.findForward(SUCCESS_FORWARD);
	}

}
