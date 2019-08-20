package com.elitecore.netvertexsm.web.datasource.database;

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
import com.elitecore.netvertexsm.web.datasource.database.form.CreateDatabaseDSForm;

public class InitCreateDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "createDatabaseDB";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DATABASE_DATASOURCE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			CreateDatabaseDSForm createDatabaseDSForm=(CreateDatabaseDSForm)form;
			createDatabaseDSForm.setConnectionUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
			return mapping.findForward(SUCCESS_FORWARD);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}			
    }
}
