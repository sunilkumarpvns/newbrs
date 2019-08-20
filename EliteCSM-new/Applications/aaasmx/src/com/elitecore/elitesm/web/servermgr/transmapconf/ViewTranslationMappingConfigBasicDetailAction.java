package com.elitecore.elitesm.web.servermgr.transmapconf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.ViewTranslationMappingConfigForm;

public class ViewTranslationMappingConfigBasicDetailAction  extends BaseWebAction{
	
	private static final String MODULE = ViewTranslationMappingConfigBasicDetailAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="viewTransMappingConfigBasicDetail";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		ViewTranslationMappingConfigForm translationMappingConfigForm = (ViewTranslationMappingConfigForm)form;
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strTranslationMapConfigId = request.getParameter("translationMapConfigId");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String translationMapConfigId = null;
			if(strTranslationMapConfigId != null){
				translationMapConfigId = strTranslationMapConfigId;
			}
			if (translationMapConfigId == null) {
				translationMapConfigId = translationMappingConfigForm.getTranslationMapConfigId();
			}
			
			if(translationMappingConfigForm.getAction()==null || translationMappingConfigForm.getAction().equals("")){
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(translationMapConfigId);
				convertBeanToForm(translationMappingConfigForm,translationMappingConfData);
				request.setAttribute("translationMappingConfData", translationMappingConfData);
				doAuditing(staffData, ACTION_ALIAS);
				return mapping.findForward(ACTION_FORWARD);
				
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
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	private void convertBeanToForm(ViewTranslationMappingConfigForm form,TranslationMappingConfData data){
		form.setName(data.getName());
		form.setDescription(data.getDescription());
		form.setTranslationMapConfigId(data.getTranslationMapConfigId());
		
	}
}
