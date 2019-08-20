package com.elitecore.elitesm.web.servermgr.copypacket;

import java.lang.reflect.Array;
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

import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.copypacket.forms.SearchCopyPacketMappingConfigForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.SearchTranslationMappingConfigAction;

public class SearchCopyPacketMappingConfigAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String MODULE = SearchTranslationMappingConfigAction.class.getSimpleName();
	private static final String LIST_FORWARD = "listconfig";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String actionMessage = "copypacket.search.failure";
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchCopyPacketMappingConfigForm searchCopyPacketConfigForm = (SearchCopyPacketMappingConfigForm) form;
			CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
			CopyPacketTranslationConfData copyPacketTranslationConfData = new CopyPacketTranslationConfData();

			String[] strSelectedIds = request.getParameterValues("select");
			

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchCopyPacketConfigForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Logger.logInfo(MODULE, "Action For Form is " + searchCopyPacketConfigForm.getAction());
			if(searchCopyPacketConfigForm.getAction() != null){

				if(searchCopyPacketConfigForm.getAction().equals("delete")){
					Logger.logDebug(MODULE, "Execuitng Delete Operation");
					actionMessage="copypacket.delete.failure";
					String actionAlias = ConfigConstant.DELETE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG;
					List<String> listSelectedIDs = new ArrayList<String>();
    				for(int i=0;i<strSelectedIds.length;i++){
    					Logger.logInfo(MODULE, "Selected IDS are " + strSelectedIds[i]);
						listSelectedIDs.add(strSelectedIds[i]);
					}

					checkActionPermission(request, actionAlias);
					blManager.deleteById(Arrays.asList(strSelectedIds), staffData);
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchCopyPacketConfigForm.getPageNumber(),searchCopyPacketConfigForm.getTotalPages(),searchCopyPacketConfigForm.getTotalRecords());

					searchCopyPacketConfigForm.setAction("list");

					request.setAttribute("responseUrl","/searchCopyPacketMappingconf.do?name="+searchCopyPacketConfigForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchCopyPacketConfigForm.getTotalPages()+"&totalRecords="+searchCopyPacketConfigForm.getTotalRecords()+"&resultStatus="+searchCopyPacketConfigForm.getStatus());
					ActionMessage message = new ActionMessage("copypacket.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
			}
			if(request.getParameter("resultStatus")!= null){
				searchCopyPacketConfigForm.setStatus(request.getParameter("resultStatus"));
			}	

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

	
			String strConfigName =request.getParameter("name");
			if(strConfigName != null){
				copyPacketTranslationConfData.setName(strConfigName);
			}else if(searchCopyPacketConfigForm.getName() != null){
				copyPacketTranslationConfData.setName(searchCopyPacketConfigForm.getName());
			}else{
				copyPacketTranslationConfData.setName("");
			}
			PageList pageList = blManager.search(copyPacketTranslationConfData,requiredPageNo, pageSize);

			searchCopyPacketConfigForm.setPageNumber(pageList.getCurrentPage());
			searchCopyPacketConfigForm.setTotalPages(pageList.getTotalPages());
			searchCopyPacketConfigForm.setTotalRecords(pageList.getTotalItems());
			searchCopyPacketConfigForm.setConfigList(pageList.getListData());
			searchCopyPacketConfigForm.setAction("list");
			request.setAttribute("searchConfigForm",searchCopyPacketConfigForm);
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
