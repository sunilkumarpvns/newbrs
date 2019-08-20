package com.elitecore.elitesm.web.servermgr.transmapconf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.forms.SearchTranslationMappingConfigForm;

public class SearchTranslationMappingConfigAction  extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "success";
	private static final String MODULE = SearchTranslationMappingConfigAction.class.getSimpleName();
	private static final String LIST_FORWARD = "listconfig";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_TRANSLATION_MAPPING_CONFIG;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String actionMessage="transmapconf.search.failure";
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchTranslationMappingConfigForm searchTranslationMappingConfigForm =(SearchTranslationMappingConfigForm)form;
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfigData = new TranslationMappingConfData();

			String[] strSelectedIds = request.getParameterValues("select");
			

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchTranslationMappingConfigForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchTranslationMappingConfigForm.getAction() != null){

				if(searchTranslationMappingConfigForm.getAction().equals("delete")){
					actionMessage="transmapconf.delete.failure";
					String actionAlias = ConfigConstant.DELETE_TRANSLATION_MAPPING_CONFIG;
					List<String> listSelectedIDs = new ArrayList<String>();
    				for(int i=0;i<strSelectedIds.length;i++){
						listSelectedIDs.add(strSelectedIds[i]);
					}

					checkActionPermission(request, actionAlias);
					blManager.deleteById(Arrays.asList(strSelectedIds), staffData);
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchTranslationMappingConfigForm.getPageNumber(),searchTranslationMappingConfigForm.getTotalPages(),searchTranslationMappingConfigForm.getTotalRecords());

					searchTranslationMappingConfigForm.setAction("list");

					request.setAttribute("responseUrl","/searchTranslationMappingConfig.do?name="+searchTranslationMappingConfigForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchTranslationMappingConfigForm.getTotalPages()+"&totalRecords="+searchTranslationMappingConfigForm.getTotalRecords()+"&resultStatus="+searchTranslationMappingConfigForm.getStatus());
					ActionMessage message = new ActionMessage("transmapconf.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);

				}
			}
			if(request.getParameter("resultStatus")!= null){
				searchTranslationMappingConfigForm.setStatus(request.getParameter("resultStatus"));
			}	

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

	
			String strConfigName =request.getParameter("name");
			if(strConfigName != null){
				translationMappingConfigData.setName(strConfigName);
			}else if(searchTranslationMappingConfigForm.getName() != null){
				translationMappingConfigData.setName(searchTranslationMappingConfigForm.getName());
			}else{
				translationMappingConfigData.setName("");
			}

			PageList pageList = blManager.search(translationMappingConfigData,requiredPageNo, pageSize);

			searchTranslationMappingConfigForm.setPageNumber(pageList.getCurrentPage());
			searchTranslationMappingConfigForm.setTotalPages(pageList.getTotalPages());
			searchTranslationMappingConfigForm.setTotalRecords(pageList.getTotalItems());
			searchTranslationMappingConfigForm.setConfigList(pageList.getListData());

			
			searchTranslationMappingConfigForm.setAction("list");
			request.setAttribute("configForm",searchTranslationMappingConfigForm);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(LIST_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage(actionMessage);
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE);
	}

}
