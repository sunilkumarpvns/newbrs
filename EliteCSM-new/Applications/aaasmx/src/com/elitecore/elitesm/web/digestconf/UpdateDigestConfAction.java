package com.elitecore.elitesm.web.digestconf;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.digestconf.forms.UpdateDigestConfForm;

public class UpdateDigestConfAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updatedigestconf";
	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIGEST_CONFIGURATION;
	private static final String MODULE = "UPDATE_DIGEST_CONFIG_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
			DigestConfBLManager digestConfBLManager = new DigestConfBLManager();
			
			UpdateDigestConfForm updateDigestConfForm = (UpdateDigestConfForm)form;
			try{

				String strdigestConfId = request.getParameter("digestConfId");
				String digestConfId;
				if(strdigestConfId == null){
					digestConfId = updateDigestConfForm.getDigestConfId();
				}else{
					digestConfId = strdigestConfId;
				}
				
				if(updateDigestConfForm.getAction() == null){
					if(Strings.isNullOrBlank(digestConfId) == false){
						DigestConfigInstanceData digestConfigInstanceData = digestConfBLManager.getDigestConfigDataById(digestConfId);
						
						updateDigestConfForm = convertBeanToForm(digestConfigInstanceData);
						request.setAttribute("digestConfData",digestConfigInstanceData);
						request.setAttribute("updateDigestConfForm", updateDigestConfForm);
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateDigestConfForm.getAction().equalsIgnoreCase("update")){
										
					DigestConfigInstanceData digestConfigInstanceData = convertFormToBean(updateDigestConfForm);
					digestConfigInstanceData.setLastModifiedDate(new Timestamp((new Date()).getTime()));
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
					digestConfBLManager.updateDigestConfigurationById(digestConfigInstanceData,staffData);
					request.setAttribute("digestConfigInstanceData",digestConfigInstanceData);
					request.setAttribute("responseUrl","/viewDigestConf.do?digestConfId="+digestConfId); 
					ActionMessage message = new ActionMessage("digestconf.update.success");
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
		        ActionMessage message = new ActionMessage("digestconf.update.duplicate.failure",updateDigestConfForm.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(Exception e){
			   Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("databaseds.update.failure");
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
	
	private UpdateDigestConfForm convertBeanToForm(DigestConfigInstanceData digestConfigInstanceData){
		
		UpdateDigestConfForm updateDigestConfForm = null;
		if(digestConfigInstanceData!=null){
			updateDigestConfForm = new UpdateDigestConfForm();

			updateDigestConfForm.setDigestConfId(digestConfigInstanceData.getDigestConfId());
			updateDigestConfForm.setName(digestConfigInstanceData.getName());
			updateDigestConfForm.setDescription(digestConfigInstanceData.getDescription());
			updateDigestConfForm.setRealm(digestConfigInstanceData.getRealm());
			updateDigestConfForm.setDefaultQoP(digestConfigInstanceData.getDefaultQoP());
			updateDigestConfForm.setDefaultAlgo(digestConfigInstanceData.getDefaultAlgo());
			updateDigestConfForm.setOpaque(digestConfigInstanceData.getOpaque());
			updateDigestConfForm.setDefaultNonce(digestConfigInstanceData.getDefaultNonce());
			updateDigestConfForm.setDefaultNonceLength(digestConfigInstanceData.getDefaultNonceLength());
			updateDigestConfForm.setDraftAAASipEnable(digestConfigInstanceData.getDraftAAASipEnable());
			updateDigestConfForm.setAuditUId(digestConfigInstanceData.getAuditUId());
		}
	
		return updateDigestConfForm;
	}
	

  private DigestConfigInstanceData convertFormToBean(UpdateDigestConfForm updateDigestConfForm){
	  
	  DigestConfigInstanceData configInstanceData = null;
	  if(updateDigestConfForm != null){

		  configInstanceData = new DigestConfigInstanceData();
		  
		  configInstanceData.setDigestConfId(updateDigestConfForm.getDigestConfId());
		  configInstanceData.setName(updateDigestConfForm.getName());
		  configInstanceData.setDescription(updateDigestConfForm.getDescription());
		  configInstanceData.setRealm(updateDigestConfForm.getRealm());
		  configInstanceData.setDefaultQoP(updateDigestConfForm.getDefaultQoP());
		  configInstanceData.setDefaultAlgo(updateDigestConfForm.getDefaultAlgo());
		  configInstanceData.setOpaque(updateDigestConfForm.getOpaque());
		  configInstanceData.setDefaultNonce(updateDigestConfForm.getDefaultNonce());
		  configInstanceData.setDefaultNonceLength(updateDigestConfForm.getDefaultNonceLength());
		  configInstanceData.setDraftAAASipEnable(updateDigestConfForm.getDraftAAASipEnable());
		  configInstanceData.setAuditUId(updateDigestConfForm.getAuditUId());
		  
	  }
	  
	  return configInstanceData;
	  
	  
  }
	
	
	
		
}
