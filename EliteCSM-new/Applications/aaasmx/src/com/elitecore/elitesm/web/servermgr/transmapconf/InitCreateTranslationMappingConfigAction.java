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
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.CreateTranslationMappingConfigForm;

public class InitCreateTranslationMappingConfigAction  extends BaseWebAction{
	private static String TRANSLATION_MAPPING_FORWARD = "createTranslationMapConf";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_TRANSLATION_MAPPING_CONFIG;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		CreateTranslationMappingConfigForm translationMappingConfigForm = (CreateTranslationMappingConfigForm) form; 
		try{
			checkActionPermission(request,ACTION_ALIAS);
			request.getSession().removeAttribute("translationMappingConfigForm");
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			
			translationMappingConfigForm.setFromTranslatorTypeList(blManager.getFromTranslatorTypeDataList());
			translationMappingConfigForm.setToTranslatorTypeList(blManager.getToTranslatorTypeDataList());
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			translationMappingConfigForm.setDescription(getDefaultDescription(userName));
			List<ScriptInstanceData> scriptDataList = scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.TRANSLATION_MAPPING_SCRIPT);
			 
			translationMappingConfigForm.setScriptDataList(scriptDataList);
			
			return mapping.findForward(TRANSLATION_MAPPING_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE);
	}
}
