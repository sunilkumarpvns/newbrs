package com.elitecore.netvertexsm.web.servermgr.spinterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.CreateSPInterfaceForm;

public class CreateSPInterfaceAction extends BaseWebAction {
	private static final String MODULE = "CREATE_DRIVER_ACTION";
	private static final String DB_FORWARD = "initCreateDBSPInterface";
	private static final String LDAP_FORWARD = "initCreateLDAPSPInterface";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		CreateSPInterfaceForm driverForm = (CreateSPInterfaceForm) form;
		Long driverType = driverForm.getDriverTypeId();
		if(driverType != null){
			HttpSession session = request.getSession(true);
			session.setAttribute("createSPInterfaceForm",driverForm);
			if(driverType==1){
				return mapping.findForward(DB_FORWARD);
			}else if(driverType==2){
				return mapping.findForward(LDAP_FORWARD);
			}
		}
		ActionMessages messages = new ActionMessages();
		messages.add("information", new ActionMessage("spinterface.unknown"));
		saveErrors(request, messages);
		
		ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("spinterface.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);
		return mapping.findForward(FAILURE);

	}
}
