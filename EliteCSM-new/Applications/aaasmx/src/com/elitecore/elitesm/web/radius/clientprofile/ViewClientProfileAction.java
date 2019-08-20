package com.elitecore.elitesm.web.radius.clientprofile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.clientprofile.forms.ViewClientProfileForm;




public class ViewClientProfileAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewClientProfile";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_CLIENT_PROFILE;
	private static final String MODULE = "ViewClientProfileAction";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewClientProfileForm viewClientProfileForm = (ViewClientProfileForm)form;
				ClientProfileBLManager blManager = new ClientProfileBLManager();
				RadiusClientProfileData radiusClientProfileData = new RadiusClientProfileData();
				String viewType=request.getParameter("viewType");
				Logger.logDebug(MODULE,"viewType is:"+viewType);
				viewClientProfileForm.setViewType(viewType);
				String strclientprofileId = request.getParameter("profileId");
				String clientProfileId = strclientprofileId;
				if(clientProfileId == null){
					clientProfileId = viewClientProfileForm.getProfileId();
				}

				if(clientProfileId!=null ){
					radiusClientProfileData = blManager.getClientProfileDataById(clientProfileId);

					String vendorInstanceId=radiusClientProfileData.getVendorInstanceId();
					Long clientTypeId=radiusClientProfileData.getClientTypeId();

					VendorData vendorData=blManager.getVendorData(vendorInstanceId);
					ClientTypeData clientTypeData=blManager.getClientTypeData(clientTypeId);	

					radiusClientProfileData.setVendorData(vendorData);
					radiusClientProfileData.setClientTypeData(clientTypeData);
					request.setAttribute("supportedVendorLstBean",radiusClientProfileData.getSupportedVendorLst());
					request.setAttribute("radiusClientProfileData",radiusClientProfileData);
				}

				return mapping.findForward(VIEW_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("clientprofile.viewclientprofile.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}


}
