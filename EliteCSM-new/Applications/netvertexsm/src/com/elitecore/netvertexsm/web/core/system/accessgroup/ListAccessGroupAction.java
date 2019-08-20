package com.elitecore.netvertexsm.web.core.system.accessgroup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ListAccessGroupForm;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class ListAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "listAccessGroup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE  = "LIST ACCESS GROUP ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.LIST_ROLE_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				ListAccessGroupForm listAccessGroupForm = (ListAccessGroupForm)form;


				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				List accessGroupList = accessGroupBLManager.getAccessGroupList(staffData,actionAlias);
				listAccessGroupForm.setListAccessGroup(accessGroupList);

				return mapping.findForward(SUCCESS_FORWARD);
			} catch (DataManagerException exp) {
				Logger.logError(MODULE,"Error during data Manager operation, reason : "+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);

			}
			Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
			ActionMessage message = new ActionMessage("accessgroup.list.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
