package com.elitecore.elitesm.web.core.system.staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffGroupRelData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.CreateStaffForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateStaffDetailAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD_DETAIL = "createStaffDetail";
	private static final String MODULE = "Create Staff Detail Action";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_STAFF_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			IStaffData staffData = null;
			IStaffData iStaffData = null;
			try {
				CreateStaffForm staffForm = (CreateStaffForm)form;	
				AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				Set<StaffGroupRelData> staffGroupRelSet = new HashSet<StaffGroupRelData>();
				StaffBLManager staffBLManager = new StaffBLManager();
				
				
				Date currentDate = new Date();
				String strActive   = "CST01";
				String strInActive = "CST02";
				iStaffData = new StaffData();
				iStaffData.setName(staffForm.getName());
				iStaffData.setUsername(staffForm.getUsername());
				iStaffData.setCreateDate(new Timestamp(currentDate.getTime()));
				iStaffData.setPassword(staffForm.getPassword());
				iStaffData.setBirthDate((staffForm.getBirthDate()));
				iStaffData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
				iStaffData.setEmailAddress(staffForm.getEmailAddress());
				iStaffData.setAddress1(staffForm.getAddress1());
				iStaffData.setAddress2(staffForm.getAddress2());
				iStaffData.setZip(staffForm.getZip());
				iStaffData.setCity(staffForm.getCity());
				iStaffData.setState(staffForm.getState());
				iStaffData.setCountry(staffForm.getCountry());
				iStaffData.setLastLoginTime(null);
				iStaffData.setStatusChangeDate(null);
				if(staffForm.getPhone()!=null){
					iStaffData.setPhone(staffForm.getPhone());
				}else{
					iStaffData.setPhone(null);
				}
				if(staffForm.getMobile() !=null){
					iStaffData.setMobile(staffForm.getMobile());
				}else{
					iStaffData.setMobile(null);
				}
				if(staffForm.getStatus()!=null && staffForm.getStatus().equalsIgnoreCase("1")){
						iStaffData.setCommonStatusId(strActive);
				}else{
						iStaffData.setCommonStatusId(strInActive);
				}
				iStaffData.setCreatedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
				iStaffData.setLastModifiedByStaffId(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId());
				iStaffData.setSystemGenerated("N");
				
				/* Encrypt User password */
				String encryptedPassword = PasswordEncryption.getInstance().crypt(iStaffData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				iStaffData.setPassword(encryptedPassword);
				
				String[] listSelected = (String[]) request.getParameterValues("selected");
				List selectedList = new ArrayList();
				selectedList = Arrays.asList(listSelected);
				
				for(int i=0;i<selectedList.size();i++){
					IStaffGroupRelData iStaffGroupRelData = new StaffGroupRelData();
					iStaffGroupRelData.setGroupId(selectedList.get(i).toString());
					staffGroupRelSet.add((StaffGroupRelData) iStaffGroupRelData);
				}
				
				iStaffData.setStaffGroupRel(staffGroupRelSet);
	
				staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				staffBLManager.create(iStaffData);
				doAuditing(iStaffData, actionAlias);
				List listAccessGroupList = accessGroupBLManager.getAccessGroupList();
				request.setAttribute("listAccessGroupList",listAccessGroupList);
				request.setAttribute("responseUrl","/initSearchStaff");
				ActionMessage message = new ActionMessage("staff.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}catch (DuplicateParameterFoundExcpetion dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,dpfExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.create.duplicate.failure",iStaffData.getUsername());
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
           } catch (Exception managerExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,managerExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.create.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
           }
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
            ActionMessages messages = new ActionMessages();
            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
