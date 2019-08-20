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
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.SearchDictionaryPopUpForm;

public class SearchDictionaryPopUpAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchDictionaryPopUp";
	private static final String FAILURE_FORWARD = "failure";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		try{
		
			
			SearchDictionaryPopUpForm searchDicPopUpform = (SearchDictionaryPopUpForm)form;
			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			
			/*IDictionaryData dictionarySearchData = new DictionaryData();*/
			
			List dictionaryList = new ArrayList();
			dictionaryList = dictionaryBLManager.getDictionaryList();
			
			List newL = new ArrayList();
			
			/*for(Iterator iter = dictionaryList.iterator(); iter.hasNext();){
				IDictionaryData s1 = (IDictionaryData) iter.next();
				
				newL.add(Integer.parseInt(s1.getDictionaryId()), s1.getName());
				
			}*/
			HttpSession session = request.getSession(true);
			
			session.setAttribute("dl", dictionaryList);
			
			System.out.println("Field Name : "+request.getParameter("fieldName"));
			searchDicPopUpform.setFieldValue(request.getParameter("fieldName"));
			searchDicPopUpform.setDictionaryListInCombo(dictionaryList);
			
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
