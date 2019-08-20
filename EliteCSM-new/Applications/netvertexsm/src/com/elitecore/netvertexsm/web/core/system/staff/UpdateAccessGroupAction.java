package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.EliteAssert;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.accessgroup.forms.UpdateAccessGroupForm;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class UpdateAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD  = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateAccessGroup";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String MODULE = "UPDATE ACCESS GROUP ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_STAFF_ACCESS_GROUP_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			UpdateAccessGroupForm updateAccessGroupForm = (UpdateAccessGroupForm)form;
			String strStaffId = request.getParameter("staffid");
			long staffId;
			if(strStaffId == null){
				staffId = updateAccessGroupForm.getStaffId();
			}else{
				staffId = Long.parseLong(request.getParameter("staffid"));
			}
			if (getStaffId(request).equals(strStaffId)) {
				Logger.logError(MODULE, "User is not allowed to update own roles");
				ActionMessage message = new ActionMessage("update.roles.group.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
			request.setAttribute("staffId", staffId);
			Logger.logDebug(MODULE, ""+staffId);
			ActionErrors errors = new ActionErrors();
			StaffBLManager staffBLManager = new StaffBLManager();
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			try {
				if(updateAccessGroupForm.getAction() == null ){
		  			if(staffId > 0){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						staffData = staffBLManager.getStaff(staffData);
						List<StaffGroupRoleRelData> staffGroupRoleRelList = staffBLManager.getStaffGroupRoleRelList(staffData.getStaffId());
						GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
						List<GroupData> staffAccessibleGroupDatas = groupDataBLManager.getStaffAccessibleGroups(ACTION_ALIAS,getUserName(request),getGroupIdVsStaffGroupRoleRelMap(request));

						request.setAttribute("staffData",staffData);
						updateAccessGroupForm = convertBeanToForm(staffData);
						updateAccessGroupForm.setStaffGroupRoleRelDatas(staffGroupRoleRelList);
						updateAccessGroupForm.setGroupDatas(staffAccessibleGroupDatas);
						request.setAttribute("listAccessGroupList",accessGroupBLManager.getAccessGroupList());
						
						request.setAttribute("updateAccessGroupForm",updateAccessGroupForm);
						IStaffData loggedInUserData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						request.setAttribute("loggedInUserData", loggedInUserData);
					}
		  				return mapping.findForward(UPDATE_FORWARD);
		  		}else if(updateAccessGroupForm.getAction().equalsIgnoreCase("update")){
		  			IStaffData iStaffData = convertFormToBean(updateAccessGroupForm);
		  			
		  			String[] roles = request.getParameterValues("accessGroupData");
					String[] groups = request.getParameterValues("groupData");
					int length = roles.length;
					for(int i=0; i<length; i++){
							String roleIdStr = roles[i];
							long roleId = Long.parseLong(roleIdStr);
							RoleData roleData = new RoleData();
							roleData.setRoleId(roleId);
							GroupData groupData = new GroupData();
							groupData.setId(groups[i]);
							iStaffData.addRolesWithGroup(roleData, groupData);
					}
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					staffBLManager.updateStaffGroupRoleRelation(iStaffData,staffData,actionAlias);
					return mapping.findForward(VIEW_FORWARD);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
    			request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("staff.updateaccessgroup.failure")); 
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
	
	private UpdateAccessGroupForm convertBeanToForm(IStaffData staffData){
		UpdateAccessGroupForm updateAccessGroupForm = null;
		if(staffData!=null){
			updateAccessGroupForm = new UpdateAccessGroupForm();
			updateAccessGroupForm.setStaffId(staffData.getStaffId());
		}
		return updateAccessGroupForm;
	}
	
	private IStaffData convertFormToBean(UpdateAccessGroupForm updateAccessGroupForm){
		IStaffData staffData  = null;
		if(updateAccessGroupForm!=null){
			staffData = new StaffData();
			staffData.setStaffId(updateAccessGroupForm.getStaffId());
		}
		return staffData;
	}

	private String getStaffId(HttpServletRequest request){
		SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
		return radiusLoginForm.getUserId();
}
}
