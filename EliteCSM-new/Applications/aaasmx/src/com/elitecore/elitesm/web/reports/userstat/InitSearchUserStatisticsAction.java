package com.elitecore.elitesm.web.reports.userstat; 
  
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
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.reports.userstat.forms.SearchUserStatisticsForm;

                                                                               
public class InitSearchUserStatisticsAction extends BaseDictionaryAction{
	private static final String MODULE="INIT SEARCH USER STATISTICS";                                                                       
	private static final String SEARCH_FORWARD = "searchUserStatistics";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS =ConfigConstant.SEARCH_USER_STATISTICS_ACTION;
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		
		//if(checkAccess(request, ACTION_ALIAS)){
		if(true){
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			SearchUserStatisticsForm searchUserStatisticsForm = (SearchUserStatisticsForm) form;
			searchUserStatisticsForm.setStatus("All");
			request.setAttribute("searchUserStatisticsForm",searchUserStatisticsForm);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SEARCH_FORWARD);
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
		 