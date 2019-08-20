package com.elitecore.elitesm.web.radius.clientprofile;
import java.util.ArrayList;
import java.util.Collections;
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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ProfileSuppVendorRelData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.UpdateClientProfileForm;


public class UpdateClientProfileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateClientProfile";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_CLIENT_PROFILE;
	private static final String MODULE = "UpdateClientProfileAction";
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
			UpdateClientProfileForm updateClientProfileForm=(UpdateClientProfileForm)form;
			try{
				RadiusClientProfileData radiusClientProfileData = new RadiusClientProfileData();
				String strprofileId = request.getParameter("profileId");
				String profileId;
				if(strprofileId == null){
					profileId = updateClientProfileForm.getProfileId();
				}else{
					profileId = strprofileId;
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				radiusClientProfileData = clientProfileBLManager.getClientProfileDataById(profileId);
				String viewType=request.getParameter("viewType");
				
				
				if(updateClientProfileForm.getAction() == null){
					if(profileId!=null ){
						
						String vendorInstanceId=radiusClientProfileData.getVendorInstanceId();
						Long clientTypeId=radiusClientProfileData.getClientTypeId();
						
						List<ClientTypeData> lstClientType=clientProfileBLManager.getClientTypeList();
						List<VendorData> lstVendorData=clientProfileBLManager.getVendorList();
						Collections.sort(lstVendorData);
						VendorData vendorData=clientProfileBLManager.getVendorData(vendorInstanceId);
						ClientTypeData clientTypeData=clientProfileBLManager.getClientTypeData(clientTypeId);	
						
						radiusClientProfileData.setVendorData(vendorData);
						radiusClientProfileData.setClientTypeData(clientTypeData);
						updateClientProfileForm = convertBeanToForm(radiusClientProfileData,lstClientType,lstVendorData);
						updateClientProfileForm.setViewType(viewType);
						request.setAttribute("supportedVendorLstBean",radiusClientProfileData.getSupportedVendorLst());
						request.setAttribute("radiusClientProfileData",radiusClientProfileData);
						request.setAttribute("updateClientProfileForm",updateClientProfileForm);
						
						
					}
					return mapping.findForward(UPDATE_FORWARD);
				 }else if(updateClientProfileForm.getAction().equalsIgnoreCase("update")){
					 
					 String[] strSelectedSupportedVendorIds = request.getParameterValues("selectedSupportedVendorIds");
					 if(strSelectedSupportedVendorIds != null && strSelectedSupportedVendorIds.length>0){
						 
					 String[] selectedSupportedVendorIds =new String[strSelectedSupportedVendorIds.length];
					 for (int i = 0; i < strSelectedSupportedVendorIds.length; i++) {
						 selectedSupportedVendorIds[i]=strSelectedSupportedVendorIds[i];
					 }
					 updateClientProfileForm.setSelectedSupportedVendorIdList(selectedSupportedVendorIds);
					 }
					 RadiusClientProfileData clientProfileData=new RadiusClientProfileData();
					 clientProfileData = radiusClientProfileData;
						
					 if("basic".equals(updateClientProfileForm.getViewType())){
						 clientProfileData =convertFormToBeanBasicDetails(updateClientProfileForm,clientProfileData);
						 staffData.setAuditName(clientProfileData.getProfileName());
						 staffData.setAuditId(clientProfileData.getAuditUId());
						 
						 clientProfileBLManager.updateBasicDetails(clientProfileData,staffData,actionAlias);
					 }else{
						 
						 clientProfileData =convertFormToBeanAdvancedDetails(updateClientProfileForm,clientProfileData);
						 staffData.setAuditName(clientProfileData.getProfileName());
						 staffData.setAuditId(clientProfileData.getAuditUId());
						 clientProfileBLManager.updateAdvanceDetails(clientProfileData,staffData,actionAlias);
					 }

					 request.setAttribute("radiusClientProfileData",radiusClientProfileData);
					 request.setAttribute("responseUrl","/viewClientProfile.do?profileId="+profileId+"&viewType=basic"); 
					 ActionMessage message = new ActionMessage("clientprofile.update.success");
					 ActionMessages messages = new ActionMessages();
					 messages.add("information",message);
					 saveMessages(request,messages);
					 return mapping.findForward(SUCCESS_FORWARD);
					
					
				}
			}catch (DuplicateInstanceNameFoundException dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		        Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("clientprofile.update.duplicate.failure",updateClientProfileForm.getProfileName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(DataManagerException e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("clientprofile.update.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			    
		}catch(Exception e){
			   Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("clientprofile.update.failure");
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
	
	private UpdateClientProfileForm convertBeanToForm(RadiusClientProfileData radiusClientProfileData, List<ClientTypeData> lstClientType, List<VendorData> lstVendorData){
		UpdateClientProfileForm updateClientProfilelForm = null;
		if(radiusClientProfileData != null){
			
			updateClientProfilelForm = new UpdateClientProfileForm();
			updateClientProfilelForm.setClientPolicy(radiusClientProfileData.getClientPolicy());
			updateClientProfilelForm.setClientTypeId(radiusClientProfileData.getClientTypeId());
			updateClientProfilelForm.setDescription(radiusClientProfileData.getDescription());
			updateClientProfilelForm.setDhcpAddress(radiusClientProfileData.getDhcpAddress());
			updateClientProfilelForm.setDnsList(radiusClientProfileData.getDnsList());
			//updateClientProfilelForm.setFramedPool(radiusClientProfileData.getFramedPool());
			updateClientProfilelForm.setHaAddress(radiusClientProfileData.getHaAddress());
			updateClientProfilelForm.setHotlinePolicy(radiusClientProfileData.getHotlinePolicy());
			updateClientProfilelForm.setPrepaidStandard(radiusClientProfileData.getPrepaidStandard());
			updateClientProfilelForm.setProfileId(radiusClientProfileData.getProfileId());
			updateClientProfilelForm.setProfileName(radiusClientProfileData.getProfileName());
			updateClientProfilelForm.setSupportedVendorList(radiusClientProfileData.getSupportedVendorList());
			updateClientProfilelForm.setMultipleClassAttribute(radiusClientProfileData.getMultipleClassAttribute());
			updateClientProfilelForm.setFilterUnsupportedVsa(Boolean.parseBoolean(radiusClientProfileData.getFilterUnsupportedVsa()));
			updateClientProfilelForm.setDynaAuthPort(radiusClientProfileData.getDynAuthPort());
			updateClientProfilelForm.setCoaSupportedAttributes(radiusClientProfileData.getCoaSupportedAttributes());
			updateClientProfilelForm.setCoaUnsupportedAttributes(radiusClientProfileData.getCoaUnsupportedAttributes());
			updateClientProfilelForm.setDmSupportedAttributes(radiusClientProfileData.getDmSupportedAttributes());
			updateClientProfilelForm.setDmUnsupportedAttributes(radiusClientProfileData.getDmUnsupportedAttributes());
			List<VendorData> supportedVendorList = radiusClientProfileData.getSupportedVendorLst();
			if(supportedVendorList != null && !supportedVendorList.isEmpty()){
			
	        String[] selectedSupportedVendorIdList =new String[supportedVendorList.size()];
		    for(int i=0;i<supportedVendorList.size();i++){

		    	VendorData suppVendorData = supportedVendorList.get(i);
				selectedSupportedVendorIdList[i] = suppVendorData.getVendorInstanceId();
			}
		    updateClientProfilelForm.setSelectedSupportedVendorIdList(selectedSupportedVendorIdList);
		    
			}
			updateClientProfilelForm.setUserIdentities(radiusClientProfileData.getUserIdentities());
			updateClientProfilelForm.setVendorInstanceId(radiusClientProfileData.getVendorInstanceId());
			updateClientProfilelForm.setLstClientType(lstClientType);
			updateClientProfilelForm.setLstVendorData(lstVendorData);
			updateClientProfilelForm.setAuditUId(radiusClientProfileData.getAuditUId());
			
		}
		
		return updateClientProfilelForm;
	}
	
	private RadiusClientProfileData convertFormToBeanBasicDetails(UpdateClientProfileForm updateClientProfileForm,RadiusClientProfileData radiusClientProfileData){
			radiusClientProfileData.setProfileId(updateClientProfileForm.getProfileId());
			radiusClientProfileData.setProfileName(updateClientProfileForm.getProfileName());
			radiusClientProfileData.setDescription(updateClientProfileForm.getDescription());
			radiusClientProfileData.setClientTypeId(updateClientProfileForm.getClientTypeId());
			radiusClientProfileData.setVendorInstanceId(updateClientProfileForm.getVendorInstanceId());
			
			radiusClientProfileData.setAuditUId(updateClientProfileForm.getAuditUId());
			List<ProfileSuppVendorRelData> supportedVendorList = new ArrayList<ProfileSuppVendorRelData>();
			String[] resultArray=updateClientProfileForm.getSelectedSupportedVendorIdList();
			if (resultArray != null && resultArray.length > 0) {
	
				for (int i = 0; i < resultArray.length; i++) {
					ProfileSuppVendorRelData data = new ProfileSuppVendorRelData();
					Logger.logDebug(MODULE, "selectedsupportedvendorid:"
							+ resultArray[i]);
					data.setVendorInstanceId(resultArray[i]);
					supportedVendorList.add(data);
				}
	
				radiusClientProfileData.setSupportedVendorList(supportedVendorList);
			}else{
				radiusClientProfileData.setSupportedVendorList(null);
			}
			radiusClientProfileData.setLastModifiedDate(getCurrentTimeStemp());

		return radiusClientProfileData;
	}
	
	private RadiusClientProfileData convertFormToBeanAdvancedDetails(UpdateClientProfileForm updateClientProfileForm,RadiusClientProfileData radiusClientProfileData){
		radiusClientProfileData.setProfileId(updateClientProfileForm.getProfileId());
		radiusClientProfileData.setDnsList(updateClientProfileForm.getDnsList());
		radiusClientProfileData.setUserIdentities(updateClientProfileForm.getUserIdentities());
		radiusClientProfileData.setPrepaidStandard(updateClientProfileForm.getPrepaidStandard());
		radiusClientProfileData.setClientPolicy(updateClientProfileForm.getClientPolicy());
		radiusClientProfileData.setHotlinePolicy(updateClientProfileForm.getHotlinePolicy());
		//radiusClientProfileData.setFramedPool(updateClientProfilelForm.getFramedPool());
		radiusClientProfileData.setDhcpAddress(updateClientProfileForm.getDhcpAddress());
		radiusClientProfileData.setHaAddress(updateClientProfileForm.getHaAddress());
		radiusClientProfileData.setFilterUnsupportedVsa(new Boolean(updateClientProfileForm.isFilterUnsupportedVsa()).toString());
		radiusClientProfileData.setMultipleClassAttribute(updateClientProfileForm.getMultipleClassAttribute());
		radiusClientProfileData.setDynAuthPort(updateClientProfileForm.getDynaAuthPort());
		radiusClientProfileData.setCoaSupportedAttributes(updateClientProfileForm.getCoaSupportedAttributes());
		radiusClientProfileData.setCoaUnsupportedAttributes(updateClientProfileForm.getCoaUnsupportedAttributes());
		radiusClientProfileData.setDmSupportedAttributes(updateClientProfileForm.getDmSupportedAttributes());
		radiusClientProfileData.setDmUnsupportedAttributes(updateClientProfileForm.getDmUnsupportedAttributes());
		radiusClientProfileData.setAuditUId(updateClientProfileForm.getAuditUId());
		radiusClientProfileData.setLastModifiedDate(getCurrentTimeStemp());
		radiusClientProfileData.setDynAuthPort(updateClientProfileForm.getDynaAuthPort());
		radiusClientProfileData.setCoaSupportedAttributes(updateClientProfileForm.getCoaSupportedAttributes());
		radiusClientProfileData.setCoaUnsupportedAttributes(updateClientProfileForm.getCoaUnsupportedAttributes());
		radiusClientProfileData.setDmSupportedAttributes(updateClientProfileForm.getDmSupportedAttributes());
		radiusClientProfileData.setDmUnsupportedAttributes(updateClientProfileForm.getDmUnsupportedAttributes());

	return radiusClientProfileData;
}
	
}
