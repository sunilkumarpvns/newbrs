package com.elitecore.elitesm.web.externalsystem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.UpdateESIInstanceForm;

public class UpdateESIInstanceAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "UpdateESIInstance";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "UPDATE ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_EXTERNAL_SYSTEM;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			UpdateESIInstanceForm esiform = (UpdateESIInstanceForm)form;
			ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
			ExternalSystemInterfaceInstanceData esiTypeAndInstanceData = esiBLManager.getExternalSystemInterfaceInstanceDataById(esiform.getEsiInstanceId());
		
			String esiInstanceId = ((ESITypeAndInstanceData)request.getSession().getAttribute("esiTypeInstance")).getEsiInstanceId();

			ActionForward actionForward = new ActionForward();

			convertFormToBean(esiform,esiTypeAndInstanceData);
			
			esiBLManager.updateByInstanceId(esiTypeAndInstanceData,staffData);
			
			request.setAttribute("esiInstanceId", esiInstanceId);
			long esiTypeId = esiTypeAndInstanceData.getEsiTypeId();
			actionForward.setPath(mapping.findForward(SUCCESS_FORWARD).getPath() + "?esiInstanceId=" + esiInstanceId);
			return actionForward;

			//return mapping.findForward(SUCCESS_FORWARD);

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

		}catch (DataManagerException e) {

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("esi.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);            
			return mapping.findForward(FAILURE_FORWARD);

		}


	}
	public void convertFormToBean(UpdateESIInstanceForm formData , ExternalSystemInterfaceInstanceData esiInstanceData){

		esiInstanceData.setName(formData.getName());
		esiInstanceData.setRealmNames(formData.getRealmNames());
		esiInstanceData.setDescription(formData.getDescription());
		esiInstanceData.setAddress(formData.getAddress());
		esiInstanceData.setSharedSecret(formData.getSharedSecret());
		esiInstanceData.setExpiredRequestLimitCount(formData.getExpiredRequestLimitCount());
		esiInstanceData.setMinLocalPort(formData.getMinLocalPort());
		esiInstanceData.setTimeout(formData.getTimeout());
		esiInstanceData.setCreateDate(getCurrentTimeStemp());
		esiInstanceData.setRetryLimit(formData.getRetryLimit());
		esiInstanceData.setStatusCheckDuration(formData.getStatusCheckDuration());
		esiInstanceData.setSupportedAttribute(formData.getSupportedAttribute());
		esiInstanceData.setUnSupportedAttribute(formData.getUnSupportedAttribute());
		esiInstanceData.setStatusCheckMethod(formData.getStatusCheckMethod());
		esiInstanceData.setPacketBytes(formData.getPacketBytes());
		esiInstanceData.setAuditUId(formData.getAuditUId());
	}

}
