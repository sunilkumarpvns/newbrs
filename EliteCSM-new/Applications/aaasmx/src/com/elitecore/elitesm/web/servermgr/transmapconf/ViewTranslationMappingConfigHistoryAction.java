package com.elitecore.elitesm.web.servermgr.transmapconf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.UpdateTranslationMappingConfigForm;

public class ViewTranslationMappingConfigHistoryAction  extends BaseWebAction{
	
	private static final String MODULE = ViewTranslationMappingConfigHistoryAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_TRANSLATION_MAPPING_CONFIG;
	private static final String ACTION_FORWARD="viewTransMappingConfigHistoryDetail";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				UpdateTranslationMappingConfigForm translationMappingConfigForm = (UpdateTranslationMappingConfigForm)form;
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				
				String strTranslationMappingConfId = request.getParameter("translationMapConfigId");
				TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(strTranslationMappingConfId);
				
				HistoryBLManager historyBlManager= new HistoryBLManager();
			
				String translationMappingConfigId = strTranslationMappingConfId;
				if(translationMappingConfigId == null){
					translationMappingConfigId = translationMappingConfigForm.getTranslationMapConfigId();
				}

				String strAuditUid = request.getParameter("auditUid");
				String strSytemAuditId=request.getParameter("systemAuditId");
				String name=request.getParameter("name");
				
				if(strSytemAuditId != null){
					request.setAttribute("systemAuditId", strSytemAuditId);
				}
				
				if(translationMappingConfigId != null && Strings.isNullOrBlank(strAuditUid) == false){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					staffData.setAuditName(translationMappingConfData.getName());
					staffData.setAuditId(translationMappingConfData.getAuditUid());
					
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
					request.setAttribute("name", name);
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				}
				request.setAttribute("translationMappingConfData",translationMappingConfData);
				return mapping.findForward(ACTION_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("databaseds.viewdatasource.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
}
