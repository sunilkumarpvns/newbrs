package com.elitecore.netvertexsm.web.core.system.staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.staff.forms.CreateStaffForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class InitCreateStaffAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initCreateStaff";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_STAFF_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
		    CreateStaffForm createStaffForm = (CreateStaffForm)form;
		    createStaffForm.setStatus("1");
		    AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
		    List listSubscribedGroup = new ArrayList(); 
		    List listAccessGroupList = accessGroupBLManager.getAccessGroupList();
		    
			GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
			List<GroupData> staffAccessibleGroupDatas = groupDataBLManager.getStaffAccessibleGroups(ACTION_ALIAS,getUserName(request),getGroupIdVsStaffGroupRoleRelMap(request));

		    createStaffForm.setGroupDatas(staffAccessibleGroupDatas);
		    request.setAttribute("listAccessGroupList",listAccessGroupList);
		    request.setAttribute("listSubscribedGroup",listSubscribedGroup);
		    request.setAttribute("createStaffForm", createStaffForm);
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
