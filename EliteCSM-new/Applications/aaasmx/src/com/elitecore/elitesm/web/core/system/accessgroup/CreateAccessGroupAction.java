package com.elitecore.elitesm.web.core.system.accessgroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.CreateAccessGroupForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class CreateAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "createAccessGroup";
	private static final String MODULE = "CREATE ACCESS GROUP ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_ACCESS_GROUP_ACTION;
	
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
		ActionErrors errors = new ActionErrors();
		
		try {
			CreateAccessGroupForm createAccessGroupForm = (CreateAccessGroupForm)form;
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			Date currentDate = new Date();
			String strActive   = "CST01";
			String strInActive = "CST02";
			
			IGroupData igroupData = new GroupData();
			igroupData.setName(createAccessGroupForm.getName());
			igroupData.setDescription(createAccessGroupForm.getDescription());
			igroupData.setSystemGenerated("N");
			igroupData.setStatusChangeDate(null);
			igroupData.setCommonStatusId(null);
			igroupData.setCreateDate(new Timestamp(currentDate.getTime()));
			igroupData.setCreatedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
			igroupData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
			igroupData.setLastModifiedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
			//igroupData.set
			
			
			
			String checkedAction = createAccessGroupForm.getC_strCheckedIDs();
			StringTokenizer stringToken = new StringTokenizer(checkedAction,"$");
			List lstActionlist = new ArrayList(stringToken.countTokens());
			while(stringToken.hasMoreElements()){
				String token = stringToken.nextToken();
				if(!token.equals("")){
					lstActionlist.add(token);
				}
			}
			
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			igroupData = accessGroupBLManager.create(igroupData, lstActionlist, staffData);
			doAuditing(staffData, actionAlias);
			//accessGroupBLManager.updateGroupAction(lstActionlist,igroupData.getGroupId());

			request.setAttribute("responseUrl","/initCreateAccessGroup");  
			ActionMessage message = new ActionMessage("accessgroup.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (Exception managerExp) {
			Logger.logError(MODULE,"Error during data Manager operation,reason : "+managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);

		}
		Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());	
		errors.add("fatal", new ActionError("accessgroup.create.failure")); 
        saveErrors(request,errors);
		return mapping.findForward(FAILURE_FORWARD);
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
