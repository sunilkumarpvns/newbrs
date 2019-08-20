package com.elitecore.elitesm.web.core.system.staff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffGroupRelData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.UpdateAccessGroupForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class UpdateAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD  = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateAccessGroup";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String MODULE = "UPDATE ACCESS GROUP ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_ACCESS_GROUP_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			StaffBLManager staffBLManager = new StaffBLManager();
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			UpdateAccessGroupForm updateAccessGroupForm = (UpdateAccessGroupForm)form;
			String strStaffId = request.getParameter("staffid");
			String staffId;
			if(strStaffId == null){
				staffId = updateAccessGroupForm.getStaffId();
			}else{
				staffId = request.getParameter("staffid");
			}
			
			Logger.logDebug(MODULE, ""+staffId);
			ActionErrors errors = new ActionErrors();
			List listAccessGroupList = new ArrayList();
			
			try {
				if(updateAccessGroupForm.getAction() == null ){
		  			if(Strings.isNullOrBlank(staffId) == false){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						staffData = staffBLManager.getStaff(staffData);
						List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						request.setAttribute("groupDataList",groupDataList);
						request.setAttribute("staffData",staffData);
						List listStaffGroupRel = staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						listAccessGroupList = accessGroupBLManager.getAccessGroupList();
						List availAccessGroupList = new ArrayList();
						availAccessGroupList.addAll(0,listAccessGroupList);
						
						if(listAccessGroupList != null && listAccessGroupList.size()>0){
							for(int i=0;i<listAccessGroupList.size();i++){
								for(int j=0;j<listStaffGroupRel.size();j++){
									if(((GroupData)listAccessGroupList.get(i)).getGroupId()==((GroupData)listStaffGroupRel.get(j)).getGroupId()){
										if(availAccessGroupList.size() >= i)
											availAccessGroupList.set(i,null);
									}
								}
							}
						}
						
						Iterator itr = availAccessGroupList.iterator();
						while(itr.hasNext()){
							if(itr.next() == null){	
								itr.remove();
							}
						}
						updateAccessGroupForm = convertBeanToForm(staffData);
	
						request.setAttribute("listAccessGroupList",availAccessGroupList);
						request.setAttribute("listSubscribedGroup",listStaffGroupRel);
						request.setAttribute("updateAccessGroupForm",updateAccessGroupForm);
					}
		  				return mapping.findForward(UPDATE_FORWARD);
		  		}else if(updateAccessGroupForm.getAction().equalsIgnoreCase("update")){
		  			Set<StaffGroupRelData> staffGroupRelSet = new HashSet<StaffGroupRelData>();
		  			IStaffData istaffData = new StaffData();
					istaffData.setStaffId(staffId);
					istaffData = staffBLManager.getStaff(istaffData);
		  			
		  			istaffData = convertFormToBean(updateAccessGroupForm,istaffData);
		  			String [] listSelected = null;
		  			listSelected = (String[]) request.getParameterValues("selected");
		  			List selectedList = null;
		  			
		  			if(listSelected != null)
		  				selectedList = Arrays.asList(listSelected);
		  			else
		  				selectedList = new ArrayList();
	
					for(int i=0;i<selectedList.size();i++){
						IStaffGroupRelData iStaffGroupRelData = new StaffGroupRelData();
						String groupId = selectedList.get(i).toString();
						iStaffGroupRelData.setGroupId(groupId);
						staffGroupRelSet.add((StaffGroupRelData) iStaffGroupRelData);
					}
					istaffData.setStaffGroupRel(staffGroupRelSet);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					staffBLManager.updateStaffAccessGroup(istaffData,actionAlias);
					List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
					request.setAttribute("groupDataList",groupDataList);
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
			updateAccessGroupForm.setAuditUId(staffData.getAuditUId());
			updateAccessGroupForm.setAuditName(staffData.getAuditName());
		}
		return updateAccessGroupForm;
	}
	
	private IStaffData convertFormToBean(UpdateAccessGroupForm updateAccessGroupForm,IStaffData staffData){
		if(updateAccessGroupForm!=null){
			staffData.setStaffId(updateAccessGroupForm.getStaffId());
		}
		return staffData;
	}
}
