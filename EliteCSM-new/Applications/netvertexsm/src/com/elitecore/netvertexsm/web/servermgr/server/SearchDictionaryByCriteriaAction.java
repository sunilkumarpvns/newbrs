package com.elitecore.netvertexsm.web.servermgr.server;

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

import com.elitecore.netvertexsm.blmanager.servermgr.dictionary.radius.RadiusDictionaryBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.SearchDictionaryPopUpForm;

public class SearchDictionaryByCriteriaAction extends BaseWebAction {
	
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
			
			
			
			long dictionaryId = searchDicPopUpform.getDictionaryId();
			String searchByName = searchDicPopUpform.getSearchByName();
			List listDictionaryByCriteria = new ArrayList();
			
			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			IRadiusDictionaryData dictionarySearchData = new RadiusDictionaryData();
			IRadiusDictionaryParamDetailData dictionaryParameterSearchData = new RadiusDictionaryParamDetailData(); 
			
			if(dictionaryId >0)
				dictionaryParameterSearchData.setDictionaryId(dictionaryId);
			else
				dictionaryParameterSearchData.setDictionaryId(0L);
			
			if(searchByName !=null){
				dictionaryParameterSearchData.setName(searchByName);
			searchDicPopUpform.setSearchByName(searchByName);
			}
			else
				dictionaryParameterSearchData.setName("");
			
			if(dictionaryId>0 && searchByName.equalsIgnoreCase("")){
				listDictionaryByCriteria = (ArrayList)dictionaryBLManager.getDictionaryParameterDetailAllList();
				
			}
			else if(dictionaryId>0 && !(searchByName.equalsIgnoreCase(""))){
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
