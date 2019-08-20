package com.elitecore.elitesm.web.servermgr.transmapconf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.UpdateTranslationMappingConfigForm;

public class UpdateTranslationMappingConfigBasicDetailAction  extends BaseWebAction{
	
	private static final String MODULE = UpdateTranslationMappingConfigBasicDetailAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="updateTransMappingConfigBasicDetail";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		UpdateTranslationMappingConfigForm translationMappingConfigForm = (UpdateTranslationMappingConfigForm)form;
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strTranslationMapConfigId = request.getParameter("translationMapConfigId");
			String translationMapConfigId = null;
			if(strTranslationMapConfigId != null){
				translationMapConfigId = strTranslationMapConfigId;
			}
			if (translationMapConfigId == null) {
				translationMapConfigId = translationMappingConfigForm.getTranslationMapConfigId();
			}
			
			if(translationMappingConfigForm.getAction()==null || translationMappingConfigForm.getAction().equals("")){
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				ScriptBLManager scriptBLManager = new ScriptBLManager();
				TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(translationMapConfigId);
				
				List<ScriptInstanceData> scriptDataList = scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.TRANSLATION_MAPPING_SCRIPT);
				 
				translationMappingConfigForm.setScriptDataList(scriptDataList);
				
				convertBeanToForm(translationMappingConfigForm,translationMappingConfData);
				request.setAttribute("translationMappingConfData",translationMappingConfData);
				return mapping.findForward(ACTION_FORWARD);
				
			}else if(translationMappingConfigForm.getAction()!=null && translationMappingConfigForm.getAction().equals("update")){
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(translationMapConfigId);
			
				translationMappingConfData = convertFormToBean(translationMappingConfigForm,translationMappingConfData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias=ACTION_ALIAS;
				
				request.setAttribute("translationMappingConfData",translationMappingConfData);
				staffData.setAuditId(translationMappingConfData.getAuditUid());
				staffData.setAuditName(translationMappingConfData.getName());
				
				blManager.updateBasicDetail(translationMappingConfData,translationMappingConfData.getTranslationMapConfigId(),staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl","/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId="+translationMappingConfData.getTranslationMapConfigId()); 
				ActionMessage message = new ActionMessage("transmapconf.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}
			
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch (DuplicateInstanceNameFoundException dpfExp) {
	        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
	        Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
	        ActionMessage message = new ActionMessage("transmapconf.duplicate.failure",translationMappingConfigForm.getName());
	        ActionMessages messages = new ActionMessages();
	        messages.add("information",message);
	        saveErrors(request,messages);
	   }catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	private TranslationMappingConfData convertFormToBean(UpdateTranslationMappingConfigForm form,TranslationMappingConfData data){
		data.setTranslationMapConfigId(form.getTranslationMapConfigId());
		data.setName(form.getName());
		data.setDescription(form.getDescription());
		data.setScript(form.getScript());
		data.setAuditUid(form.getAuditUid());
		return data;
	}
	private void convertBeanToForm(UpdateTranslationMappingConfigForm form,TranslationMappingConfData data){
		form.setName(data.getName());
		form.setDescription(data.getDescription());
		form.setTranslationMapConfigId(data.getTranslationMapConfigId());
		form.setScript(data.getScript());
		form.setAuditUid(data.getAuditUid());
	}
}
