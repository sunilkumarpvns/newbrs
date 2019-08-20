package com.elitecore.elitesm.web.externalsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.CreateESIInstanceForm;

public class CreateESIInstanceAction extends BaseWebAction{
	
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_EXTERNAL_SYSTEM;
	private static final String MODULE = "CREATE_ESI_INSTANCE_ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
	
			CreateESIInstanceForm esiform = (CreateESIInstanceForm)form;
			
			try{
				checkActionPermission(request, ACTION_ALIAS);
				ExternalSystemInterfaceInstanceData esiInstanceData = new ExternalSystemInterfaceInstanceData();
				ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				convertFormToBean(esiform,esiInstanceData);
				
				esiInstanceData.setCreatedByStaffId(currentUser);        	
				esiInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				esiInstanceData.setLastModifiedByStaffId(currentUser);
				
				esiBLManager.createESIInstance(esiInstanceData, staffData);
				
				List esiTypeList = esiBLManager.getListOfESIType();
				esiform.setEsiTypeList(esiTypeList);
				request.setAttribute("responseUrl", "/initSearchESIInstance");
				ActionMessage message = new ActionMessage("esi.create.success");
    	        ActionMessages messages = new ActionMessages();
    	        messages.add("information",message);
    	        saveMessages(request, messages);

			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	

			}catch(DuplicateInstanceNameFoundException dpf){
    			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,dpf);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("esi.duplicate.name.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
    		}catch(DuplicateParameterFoundExcpetion dpf){
    			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,dpf);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("esi.duplicate.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
    		}catch(Exception e){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("esi.create.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
    			return mapping.findForward(FAILURE_FORWARD);
			}

			return mapping.findForward(SUCCESS_FORWARD);
	
	}
	
	public void convertFormToBean(CreateESIInstanceForm formData , ExternalSystemInterfaceInstanceData esiInstanceData){
		
		esiInstanceData.setName(formData.getName());
		esiInstanceData.setDescription(formData.getDesc());
		esiInstanceData.setAddress(formData.getAddress());
		esiInstanceData.setSharedSecret(formData.getSharedSecret());
		esiInstanceData.setEsiTypeId(formData.getEsiTypeId());
		esiInstanceData.setRealmNames(formData.getRealmNames());
		esiInstanceData.setCreateDate(getCurrentTimeStemp());
		esiInstanceData.setTimeout(formData.getTimeout());
		esiInstanceData.setExpiredRequestLimitCount(formData.getExpiredRequestLimitCount());
		esiInstanceData.setMinLocalPort(formData.getMinLocalPort());
		esiInstanceData.setRetryLimit(formData.getRetryLimit());
		esiInstanceData.setStatusCheckDuration(formData.getStatusCheckDuration());
		esiInstanceData.setStatus("Y");
		esiInstanceData.setSupportedAttribute(formData.getSupportedAttribute());
		esiInstanceData.setUnSupportedAttribute(formData.getUnSupportedAttribute());
		esiInstanceData.setPacketBytes(formData.getPacketBytes());
		esiInstanceData.setStatusCheckMethod(formData.getStatusCheckMethod());
	}

}
