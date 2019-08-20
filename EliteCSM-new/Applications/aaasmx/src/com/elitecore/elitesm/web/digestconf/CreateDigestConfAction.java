package com.elitecore.elitesm.web.digestconf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.digestconf.forms.CreateDigestConfForm;

public class CreateDigestConfAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIGEST_CONFIGURATION;
	private static final String MODULE="CreateDigestConfAction";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			CreateDigestConfForm createDigestConfForm = (CreateDigestConfForm)form;
			try{
				
				DigestConfBLManager digestConfBLManager = new DigestConfBLManager();
				
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				
				DigestConfigInstanceData digestConfigInstanceData = new DigestConfigInstanceData();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

				digestConfigInstanceData.setName(createDigestConfForm.getName());
				digestConfigInstanceData.setDescription(createDigestConfForm.getDescription());
				digestConfigInstanceData.setCreatedbyStaffid(currentUser);
				digestConfigInstanceData.setCreateDate(getCurrentTimeStemp());
				convertFormtoBean(createDigestConfForm,digestConfigInstanceData);
				digestConfBLManager.create(digestConfigInstanceData,staffData);
				request.setAttribute("responseUrl","/initSearchDigestConf"); 
				ActionMessage message = new ActionMessage("digestconf.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
				
			}catch (DuplicateInstanceNameFoundException dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		        Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("digestconf.create.duplicate.failure",createDigestConfForm.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("digestconf.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("digestconf.create.failure");
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

	private void convertFormtoBean(CreateDigestConfForm createDigestConfForm,DigestConfigInstanceData digestConfigInstanceData) {
		
		   
		    digestConfigInstanceData.setName(createDigestConfForm.getName());
		    digestConfigInstanceData.setDescription(createDigestConfForm.getDescription());
		    digestConfigInstanceData.setRealm(createDigestConfForm.getRealm());
		    digestConfigInstanceData.setDefaultQoP(createDigestConfForm.getDefaultQoP());
		    digestConfigInstanceData.setDefaultAlgo(createDigestConfForm.getDefaultAlgo());
		    digestConfigInstanceData.setOpaque(createDigestConfForm.getOpaque());
		    digestConfigInstanceData.setDefaultNonceLength(createDigestConfForm.getDefaultNonceLength());
		    digestConfigInstanceData.setDefaultNonce(createDigestConfForm.getDefaultNonce());
		    digestConfigInstanceData.setDraftAAASipEnable(createDigestConfForm.getDraftAAASipEnable());
		    
		   
		
	}
}

