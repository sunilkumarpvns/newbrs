package com.elitecore.elitesm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.database.forms.SearchDatabaseDSForm;



public class InitSearchDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initsearchDatabaseDS";
	private static final String MODULE = "INIT_SEARCH_DATABASE_DS_ACTION";
	private static final String FAILURE_FORWARD = "failure";     
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DATABASE_DATASOURCE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchDatabaseDSForm searchDatabaseDSForm=(SearchDatabaseDSForm)form;
			searchDatabaseDSForm.setName("");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.peer.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return mapping.findForward(FAILURE_FORWARD); 
    }
}
