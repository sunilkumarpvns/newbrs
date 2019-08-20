package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.SearchDictionaryPopUpForm;


public class SearchDictionaryPopUpAction extends BaseDictionaryAction {
        
        private static final String SUCCESS_FORWARD = "success";
        private static final String LIST_FORWARD = "searchDictionaryPopUp";
        private static final String FAILURE_FORWARD = "failure";

        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
                
        	Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());

        	try{

        		SearchDictionaryPopUpForm searchDicPopUpform = (SearchDictionaryPopUpForm)form;
        		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();

        		String str = request.getParameter("dicIdList");
        		str = str.substring(1, str.length()-1);
        		String[] strArray = str.split(",");

        		Collection colDictionaryId = new ArrayList();

        		for(int i = 0;i < strArray.length; i++) {
        			colDictionaryId.add(Long.parseLong(strArray[i].trim()));
        		}

        		List dictionaryList = new ArrayList();
        		dictionaryList = dictionaryBLManager.getAllDictionaryById(colDictionaryId);

        		HttpSession session = request.getSession(true);

        		session.setAttribute("dl", dictionaryList);
        		session.setAttribute("dIdList", colDictionaryId);

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

