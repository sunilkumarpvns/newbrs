package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class MiscProfileAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String DELETE_FAIL_FORWARD = "deleteFailure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_GATEWAY_PROFILE;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
	    	try{
	    		GatewayBLManager gatewayBLManager = new GatewayBLManager();
	    		List<GatewayData> gatewayList = gatewayBLManager.getGatewayList();
				List<Long> profileIdList = new ArrayList<Long>();
				String[] strProfileIds = request.getParameterValues("select"); 
				long [] profileids=new long[strProfileIds.length];
				List<Long> deleteProfileids=new ArrayList<Long>();
				

				for(int i=0;i<strProfileIds.length;i++){
					profileids[i]=Long.parseLong(strProfileIds[i]);
				}
				
				if(gatewayList!=null && strProfileIds.length>0){
					for(int i=0;i<profileids.length;i++){
						for(int j=0;j<gatewayList.size();j++){
							if(profileids[i]==gatewayList.get(j).getProfileId()){
								profileids[i]=0;
								deleteProfileids.add(profileids[i]);
							}
						}
					}
				}
				
				for(int i=0;i<profileids.length;i++){
					if(profileids[i]!=0){
						profileIdList.add(profileids[i]);
					}
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				gatewayBLManager.deleteProfile(profileIdList,staffData, ACTION_ALIAS);
				if(deleteProfileids.size()>0){
		            ActionMessages messages = new ActionMessages();		            		
		            ActionMessage message = new ActionMessage("gateway.profile.delete.failure");
		            messages.add("information", message);
		            ActionMessage reasonMessage = new ActionMessage("gateway.profile.configuredwith.gateway");
		            messages.add("reason", reasonMessage);
		            
		            ActionMessages errorHeadingMessage = new ActionMessages();
		            message = new ActionMessage("error.heading.gateway.profile.delete.failure");
		            errorHeadingMessage.add("errorHeading",message);
		            saveErrors(request, messages);
		            saveMessages(request,errorHeadingMessage);
		       
					request.setAttribute("responseUrl","/initSearchProfile.do");
					return mapping.findForward(DELETE_FAIL_FORWARD);
				}
				ActionMessage message = new ActionMessage("gateway.profile.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
	            request.setAttribute("responseUrl","/initSearchProfile.do");
				return mapping.findForward(SUCCESS_FORWARD);
	    	}catch(ActionNotPermitedException e){
	             Logger.logError(MODULE,"Error :-" + e.getMessage());
	             printPermitedActionAlias(request);
	             ActionMessages messages = new ActionMessages();
	             messages.add("information", new ActionMessage("general.user.restricted"));
	             saveErrors(request, messages);
	             ActionMessages errorHeadingMessage = new ActionMessages();
	             ActionMessage message = new ActionMessage("gateway.profile.error.heading","deleting");
	             errorHeadingMessage.add("errorHeading",message);
	             saveMessages(request,errorHeadingMessage); 	
	             return mapping.findForward(INVALID_ACCESS_FORWARD);
	        }catch (DataManagerException managerExp) {
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("gateway.profile.delete.failure");
		            messages.add("information", message);
		            saveErrors(request, messages);
				}else{
	               ActionMessage message = new ActionMessage("general.error");
	               messages.add("information", message);
	               saveErrors(request, messages);
				}
	        }catch(Exception managerExp){
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
	    	}
	    	
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("gateway.profile.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	
	    	return mapping.findForward(FAILURE_FORWARD);
	    }else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("gateway.profile.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}
    }
}