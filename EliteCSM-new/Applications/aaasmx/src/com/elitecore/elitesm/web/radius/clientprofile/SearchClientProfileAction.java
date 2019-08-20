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
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.SearchClientProfileForm;

public class SearchClientProfileAction extends BaseWebAction{

	

	private static final String SUCCESS_FORWARD = "listclientprofiles";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CLIENT_PROFILE;
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SEARCH CLIENT PROFILE ACTION";
	private static final String LIST_FORWARD = "searchClientProfileList";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			try{
				ClientProfileBLManager blManager = new ClientProfileBLManager();
				SearchClientProfileForm searchClientProfileForm = (SearchClientProfileForm)form;
			    
				ClientProfileBLManager profileBLManager = new ClientProfileBLManager();
				List clientTypeListInCombo = profileBLManager.getClientTypeList();
				List vendorListInCombo = profileBLManager.getVendorList();
				
				searchClientProfileForm.setClientTypeList(clientTypeListInCombo);
				searchClientProfileForm.setVendorList(vendorListInCombo);
				
				int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchClientProfileForm.getPageNumber()).intValue();
	    		}
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				
                RadiusClientProfileData radiusClientProfileData = new RadiusClientProfileData();
                 
                String strClientProfileName =request.getParameter("profileName"); 
                if(strClientProfileName != null){
                	radiusClientProfileData.setProfileName(strClientProfileName);
                }else if(searchClientProfileForm.getProfileName() != null){
                	radiusClientProfileData.setProfileName(searchClientProfileForm.getProfileName());
                }else{
                	radiusClientProfileData.setProfileName("");
                	
                }
                strClientProfileName = radiusClientProfileData.getProfileName();
            
                String clientTypeId=request.getParameter("clientTypeId");
                if(clientTypeId != null){
                	radiusClientProfileData.setClientTypeId(Long.parseLong(clientTypeId));
                }else{
                	if(searchClientProfileForm.getClientTypeId() == null) {
                		radiusClientProfileData.setClientTypeId(0L);
                	} else {
                		radiusClientProfileData.setClientTypeId(Long.parseLong(searchClientProfileForm.getClientTypeId()));
                	}	
                }
              			
                String vendorInstanceId=request.getParameter("vendorInstanceId");
                if(vendorInstanceId != null){
                	radiusClientProfileData.setVendorInstanceId(vendorInstanceId);
                }else{
                	if(searchClientProfileForm.getVendorInstanceId() == null) {
                		radiusClientProfileData.setVendorInstanceId(null); 
                	} else {
                		radiusClientProfileData.setVendorInstanceId(searchClientProfileForm.getVendorInstanceId());
                	}
                	
                }
                
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
				PageList pageList = blManager.search(radiusClientProfileData,staffData,requiredPageNo,pageSize);
				List<RadiusClientProfileData> clientProfileList=pageList.getListData();
										
				for(int i=0;i<clientProfileList.size();i++){
					RadiusClientProfileData radiusClientProfileData2 = (RadiusClientProfileData) clientProfileList.get(i);
					VendorData vendorData=blManager.getVendorData(radiusClientProfileData2.getVendorInstanceId());
					ClientTypeData clientTypeData=blManager.getClientTypeData(radiusClientProfileData2.getClientTypeId());
					clientProfileList.get(i).setClientTypeData(clientTypeData);
					clientProfileList.get(i).setVendorData(vendorData);
					
				}
				
				searchClientProfileForm.setProfileName(radiusClientProfileData.getProfileName());
				searchClientProfileForm.setClientTypeId(String.valueOf(radiusClientProfileData.getClientTypeId()));
				searchClientProfileForm.setVendorInstanceId(String.valueOf(radiusClientProfileData.getVendorInstanceId()));
				searchClientProfileForm.setPageNumber(pageList.getCurrentPage());
				searchClientProfileForm.setTotalPages(pageList.getTotalPages());
				searchClientProfileForm.setTotalRecords(pageList.getTotalItems());
				searchClientProfileForm.setClientProfileList(clientProfileList);
				
				
				
				searchClientProfileForm.setAction(BaseConstant.LISTACTION);
				
				return mapping.findForward(LIST_FORWARD);
				
				
			}catch(DataManagerException dme){
				if(dme.getMessage().equals("Action failed :could not execute query")){
        			Logger.logTrace(MODULE,dme);
	        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
	        		request.setAttribute("errorDetails", errorElements);
	        		ActionMessages messages = new ActionMessages();
	        		ActionMessage message1 = new ActionMessage("clientprofile.dbc.failure");
	        		messages.add("information",message1);
	        		saveErrors(request,messages);
	
	        		return mapping.findForward(FAILURE_FORWARD);
				}
				
			}catch(Exception e){
				Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
			
			Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
			ActionMessage message = new ActionMessage("clientprofile.list.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
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
	
	
	
}
