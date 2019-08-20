package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.SearchDictionaryPopUpForm;

public class SearchDictionaryByCriteriaAction extends BaseDictionaryAction {
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchDictionaryPopUp";
	private static final String FAILURE_FORWARD = "failure";
	private static final String INSERT_FORWARD = "/updateNetDriverConfiguration.do";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		try{
		
			SearchDictionaryPopUpForm searchDicPopUpform = (SearchDictionaryPopUpForm)form;
			
			
			HttpSession session = request.getSession(true);
			//List check = (ArrayList)session.getAttribute("dl");
			
			
			if(searchDicPopUpform.getFieldValue() == null)
				searchDicPopUpform.setFieldValue(request.getParameter("fieldName"));
			
			
			
			String dictionaryId = searchDicPopUpform.getDictionaryId();
			String searchByName = searchDicPopUpform.getSearchByName();
			List listDictionaryByCriteria = new ArrayList();
			
			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			IDictionaryData dictionarySearchData = new DictionaryData();
			IDictionaryParameterDetailData dictionaryParameterSearchData = new DictionaryParameterDetailData(); 
			
			if( Strings.isNullOrEmpty(dictionaryId) == false && dictionaryId.equals("0") == false)
				dictionaryParameterSearchData.setDictionaryId(dictionaryId);
			else
				dictionaryParameterSearchData.setDictionaryId("0");
			
			if(searchByName !=null){
				dictionaryParameterSearchData.setName(searchByName);
			searchDicPopUpform.setSearchByName(searchByName);
			}
			else
				dictionaryParameterSearchData.setName("");
			
			if(Strings.isNullOrEmpty(dictionaryId) == false && dictionaryId.equals("0") == false && searchByName.equalsIgnoreCase("")){
				listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryParameterDetailAllList();
				
			}
			else if(Strings.isNullOrEmpty(dictionaryId) == false && dictionaryId.equals("0") == false && !(searchByName.equalsIgnoreCase(""))){
				listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryParameterDetailAllList(searchByName);
			}
			else
			listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryParameterDetailList(dictionaryParameterSearchData,searchByName);
			
			
			searchDicPopUpform.setDictionaryListByCriteria(listDictionaryByCriteria);
			
			searchDicPopUpform.setDictionaryListInCombo((ArrayList)session.getAttribute("dl"));
			return mapping.findForward(LIST_FORWARD);
			
			
		}
		
		catch(Exception managerExp){
			Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.dictionary.operation.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            saveErrors(request,messages);
		}
		Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.server.dictionary.operation.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}

}
