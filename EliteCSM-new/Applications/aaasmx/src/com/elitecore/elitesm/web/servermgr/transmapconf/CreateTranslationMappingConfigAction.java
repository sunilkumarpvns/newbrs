package com.elitecore.elitesm.web.servermgr.transmapconf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.CreateTranslationMappingConfigForm;

public class CreateTranslationMappingConfigAction  extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_TRANSLATION_MAPPING_CONFIG;
	private static final String CRESTEL_RATING_TRANSLATION_MAPPING = "createCrestelRatingTransMapAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		CreateTranslationMappingConfigForm translationMappingConfigForm = (CreateTranslationMappingConfigForm) form; 
		try{
			checkActionPermission(request, ACTION_ALIAS);

			TranslationMappingConfBLManager blManager= new TranslationMappingConfBLManager();
			TranslatorTypeData translatorTo = blManager.getTranslatorTypeData(translationMappingConfigForm.getSelectedToTranslatorType());
			TranslatorTypeData translatorFrom = blManager.getTranslatorTypeData(translationMappingConfigForm.getSelectedFromTranslatorType());
			translationMappingConfigForm.setSelectedToTranslatorTypeData(translatorTo);
			translationMappingConfigForm.setSelectedFromTranslatorTypeData(translatorFrom);
			
			List<TranslationMappingConfData> baseTranslationMappingConfDataList = blManager.getTranslationMappingConfigList(translationMappingConfigForm.getSelectedToTranslatorType(), translationMappingConfigForm.getSelectedFromTranslatorType());
			translationMappingConfigForm.setBaseTranslationMappingConfDataList(baseTranslationMappingConfDataList);
			
			request.getSession().setAttribute("translationMappingConfigForm",translationMappingConfigForm);
			return mapping.findForward(CRESTEL_RATING_TRANSLATION_MAPPING);
		
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
