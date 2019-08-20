package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.StaffConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.UpdateStaffPasswordForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateStaffPasswordAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String CHANGE_PASSWORD = "changeStaffPassword";
    private static final String VIEW_FORWARD    = "viewStaffDetail";
    private static final String ACTION_ALIAS    = ConfigConstant.UPDATE_STAFF_PASSWORD_ACTION;
    private static final String MODULE    = "UPDATE STAFF PASSWORD";
    
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        if (checkAccess(request,ACTION_ALIAS)) {
            Logger.logTrace(MODULE, "Enter execute method of :" + getClass().getName());
            StaffBLManager staffBLManager = new StaffBLManager();
          
            try {
                UpdateStaffPasswordForm updateStaffPasswordForm = (UpdateStaffPasswordForm) form;
				String strStaffId = request.getParameter("staffid");
				long staffId;
				if(strStaffId == null){
					staffId = updateStaffPasswordForm.getStaffId();
				}else{
					staffId = Long.parseLong(request.getParameter("staffid"));
				}
                if ( updateStaffPasswordForm.getAction() == null) {
                    if (staffId > 0) {
                        IStaffData staffData = new StaffData();
                        staffData.setStaffId(staffId);
                        staffData = staffBLManager.getStaff(staffData);
                        request.setAttribute("staffData", staffData);
                        List<RoleData> roleDataList  =staffBLManager.getStaffRoleRelList(staffData.getStaffId());
                        request.setAttribute("roleDataList",roleDataList);
                        updateStaffPasswordForm = convertBeanToForm(staffData);
                        request.setAttribute("changeStaffPasswordForm", updateStaffPasswordForm);
                        IStaffData loggedInUserData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
                        request.setAttribute("loggedInUserData", loggedInUserData);
                    }
                    return mapping.findForward(CHANGE_PASSWORD);
                } else if (updateStaffPasswordForm.getAction().equalsIgnoreCase("update")) {
                	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    				String actionAlias = ACTION_ALIAS;
                    staffBLManager.resetPassword(updateStaffPasswordForm.getStaffId(), updateStaffPasswordForm.getNewPassword(),staffData,actionAlias);
         
            		request.setAttribute("responseUrl","/serverGroupManagement.do?method=initSearch");
            		ActionMessage message = new ActionMessage("login.user.changepassword.success");
            		ActionMessages messages = new ActionMessages();
            		messages.add("information",message);
            		saveMessages(request,messages);
            		return mapping.findForward(SUCCESS_FORWARD);
                    /*return mapping.findForward(VIEW_FORWARD);*/
                }
            }
            catch(DataManagerException de){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,de);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(de);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("login.user.changepassword.failure");
				messages.add("information",message1);
				if(de.getMessage().contains(StaffConstant.SAME_OLD_AND_NEW_PASSWORDS)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.sameoldandnewpassword");
					messages.add("information", messageReason);
				}
				if(de.getMessage().contains(StaffConstant.SAME_NEW_AND_HISTORICAL_PASSWORD)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.samehistoricalpassword",de.getMessage().split(":")[1]);
					messages.add("information", messageReason);
				}
				if(de.getMessage().contains(StaffConstant.OLD_PASSWORD_DB_AND_GUI_DONT_MATCH)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.oldpassworddbandguidontmatch");
					messages.add("information", messageReason);
				}
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);

			}
            catch (Exception e) {
                Logger.logError(MODULE, "Exception Thrown :" + getClass().getName());
                Logger.logTrace(MODULE, e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                ActionMessage message1 = new ActionMessage("staff.updatepassword.failure");
                messages.add("information", message1);
                saveErrors(request, messages);
            }
            return mapping.findForward(FAILURE_FORWARD);
        } else {
            Logger.logWarn(MODULE, "No Access to For this opration ");
            ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
    }
    
    private UpdateStaffPasswordForm convertBeanToForm( IStaffData staffData ) {
        UpdateStaffPasswordForm updateStaffPasswordForm = null;
        if (staffData != null) {
            updateStaffPasswordForm = new UpdateStaffPasswordForm();
            updateStaffPasswordForm.setStaffId(staffData.getStaffId());
            updateStaffPasswordForm.setPassword(staffData.getPassword());
        }
        return updateStaffPasswordForm;
    }
    
    private IStaffData convertFormToBean( UpdateStaffPasswordForm updateStaffPasswordForm ) {
        IStaffData staffData = null;
        if (updateStaffPasswordForm != null) {
            staffData = new StaffData();
            staffData.setStaffId(updateStaffPasswordForm.getStaffId());
            staffData.setPassword(updateStaffPasswordForm.getPassword());
        }
        return staffData;
    }
}
