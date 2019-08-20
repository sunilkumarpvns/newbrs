package com.elitecore.netvertexsm.web.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class InitCreateLDAPDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "CreateLDAPDS";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_LDAP_DATASOURCE;
	private static final String MODULE = "CREATE_LDAP_ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
			List baseDnDetailList = new ArrayList();
			request.getSession().setAttribute("baseDnDetailList", baseDnDetailList);			
			return mapping.findForward(SUCCESS_FORWARD);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("datasource.ldap.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}