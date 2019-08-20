package com.elitecore.elitesm.web.servermgr.transmapconf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.ViewCrestelRatingTransMapConfigForm;

public class ViewCrestelRatingTransMapConfAction  extends BaseWebAction{
	
	private static final String MODULE = ViewCrestelRatingTransMapConfAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_TRANSLATION_MAPPING_CONFIG;
	private static final String VIEW_FORWARD = "viewCrestelRatingTransMapConf";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		ViewCrestelRatingTransMapConfigForm translationMappingConfigForm = (ViewCrestelRatingTransMapConfigForm)form;
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strTranslationMapConfigId = request.getParameter("translationMapConfigId");
			String translationMapConfigId = null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(strTranslationMapConfigId != null){
				translationMapConfigId = strTranslationMapConfigId;
			}
			if (translationMapConfigId == null) {
				translationMapConfigId = translationMappingConfigForm.getTranslationMapConfigId();
			}
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfDataById(translationMapConfigId);
			if(Strings.isNullOrBlank(translationMappingConfData.getBaseTranslationMapConfigId()) == false) {
				TranslationMappingConfData baseTranslationMappingConfData = blManager.getTranslationMappingConfDataById(translationMappingConfData.getBaseTranslationMapConfigId());
				translationMappingConfigForm.setBaseTranslationMappingConfData(baseTranslationMappingConfData);
			}
			request.setAttribute("translationMappingConfData",translationMappingConfData);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD);
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
			ActionMessage message = new ActionMessage("transmapconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	
}
