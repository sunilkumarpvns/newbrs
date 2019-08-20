package com.elitecore.elitesm.web.radius.dictionary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.dictionary.forms.CreateDictionaryForm;

public class InitCreateDictionaryAction extends BaseDictionaryAction {
	private static final String CREATE_FORWARD = "createDictionary";
	private static final String ACTION_ALIAS = "CREATE_DICTIONARY_ACTION";
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
	        Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
	        CreateDictionaryForm addForm = (CreateDictionaryForm)form;
	        String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
	        addForm.setCommonStatusId(DictionaryConstant.SHOW_STATUS_ID);
	        addForm.setDescription(getDefaultDescription(userName));
	        Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());        
	        return mapping.findForward(CREATE_FORWARD);            		
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
