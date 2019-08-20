package com.elitecore.elitesm.web.radius.clientprofile;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ProfileSuppVendorRelData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.CreateClientProfileForm;

public class CreateClientProfileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_CLIENT_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			CreateClientProfileForm createClientProfileForm = (CreateClientProfileForm)form;			

			try{
				
				ClientProfileBLManager clientProfileBLManager=new ClientProfileBLManager();
				
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				
				RadiusClientProfileData radiusClientProfileData =new RadiusClientProfileData();

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				radiusClientProfileData.setCreatedByStaffId(staffData.getStaffId());
				String strSelectedSupportedVendorIds[] = request.getParameterValues("selectedSupportedVendorIds");
				if(strSelectedSupportedVendorIds != null && strSelectedSupportedVendorIds.length>0){
					
					String[] selectedSupportedVendorIds =new String[strSelectedSupportedVendorIds.length];
					for (int i = 0; i < strSelectedSupportedVendorIds.length; i++) {
						selectedSupportedVendorIds[i]=strSelectedSupportedVendorIds[i];
					}
					createClientProfileForm.setSelectedSupportedVendorIdList(selectedSupportedVendorIds);
				}
				convertFormToBean(createClientProfileForm,radiusClientProfileData);
				clientProfileBLManager.create(radiusClientProfileData,staffData);
				
				request.setAttribute("responseUrl","/initSearchClientProfile.do"); 
				ActionMessage message = new ActionMessage("clientprofile.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
				}catch (DuplicateInstanceNameFoundException dpfExp) {
			        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			        Logger.logTrace(MODULE,dpfExp);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
					request.setAttribute("errorDetails", errorElements);
			        ActionMessage message = new ActionMessage("clientprofile.create.duplicate.failure",createClientProfileForm.getProfileName());
			        ActionMessages messages = new ActionMessages();
			        messages.add("information",message);
			        saveErrors(request,messages);
			   }catch(DataManagerException e){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("clientprofile.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
				}catch(Exception e){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("clientprofile.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
				}
     			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void convertFormToBean(CreateClientProfileForm createClientProfileForm,	RadiusClientProfileData radiusClientProfileData) {
	
		
		radiusClientProfileData.setProfileName(createClientProfileForm.getProfileName());
		radiusClientProfileData.setDescription(createClientProfileForm.getDescription());
		radiusClientProfileData.setClientTypeId(createClientProfileForm.getClientTypeId());
		radiusClientProfileData.setVendorInstanceId(createClientProfileForm.getVendorInstanceId());
		radiusClientProfileData.setDnsList(createClientProfileForm.getDnsList());
		radiusClientProfileData.setUserIdentities(createClientProfileForm.getUserIdentities());
		radiusClientProfileData.setPrepaidStandard(createClientProfileForm.getPrepaidStandard());
		radiusClientProfileData.setClientPolicy(createClientProfileForm.getClientPolicy());
		radiusClientProfileData.setHotlinePolicy(createClientProfileForm.getHotlinePolicy());
		radiusClientProfileData.setFilterUnsupportedVsa(new Boolean(createClientProfileForm.isFilterUnsupportedVsa()).toString());
		//radiusClientProfileData.setFramedPool(createClientProfileForm.getFramedPool());
		radiusClientProfileData.setDhcpAddress(createClientProfileForm.getDhcpAddress());
		radiusClientProfileData.setHaAddress(createClientProfileForm.getHaAddress());
		radiusClientProfileData.setMultipleClassAttribute(createClientProfileForm.getMultipleClassAttribute());
		radiusClientProfileData.setDynAuthPort(createClientProfileForm.getDynaAuthPort());
		radiusClientProfileData.setCoaSupportedAttributes(createClientProfileForm.getCoaSupportedAttributes());
		radiusClientProfileData.setCoaUnsupportedAttributes(createClientProfileForm.getCoaUnsupportedAttributes());
		radiusClientProfileData.setDmSupportedAttributes(createClientProfileForm.getDmSupportedAttributes());
		radiusClientProfileData.setDmUnsupportedAttributes(createClientProfileForm.getDmUnsupportedAttributes());
		radiusClientProfileData.setSystemgenerated("N");
		radiusClientProfileData.setCreateDate(getCurrentTimeStemp());
		
		List<ProfileSuppVendorRelData> supportedVendorList = new ArrayList<ProfileSuppVendorRelData>();
		String[] resultArray=createClientProfileForm.getSelectedSupportedVendorIdList();
		if(resultArray != null && resultArray.length>0){
		for(int i=0;i<resultArray.length;i++){
			ProfileSuppVendorRelData data=new ProfileSuppVendorRelData();
			Logger.logDebug(MODULE,"selectedsupportedvendorid:"+resultArray[i]);
			data.setVendorInstanceId(resultArray[i]);
			supportedVendorList.add(data);
		}
		
		radiusClientProfileData.setSupportedVendorList(supportedVendorList);
		}
		
		
	}
	
}

