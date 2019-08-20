package com.elitecore.elitesm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.UpdateStaffPasswordForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateStaffPasswordAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String CHANGE_PASSWORD = "changeStaffPassword";
    private static final String VIEW_FORWARD    = "viewStaffDetail";
    private static final String ACTION_ALIAS    = ConfigConstant.UPDATE_STAFF_PASSWORD;
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
				String staffId;
				if(Strings.isNullOrBlank(strStaffId) == true){
					staffId = updateStaffPasswordForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
                if (updateStaffPasswordForm.getAction() == null) {
                    if (Strings.isNullOrBlank(staffId) == false) {
                        IStaffData staffData = new StaffData();
                        staffData.setStaffId(staffId);
                        staffData = staffBLManager.getStaff(staffData);
                        // String decryptedPassword = PasswordEncryption.decrypt(staffData.getPassword(), Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)));
                        // staffData.setPassword(staffData.getPassword());
                        request.setAttribute("staffData", staffData);
						List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						request.setAttribute("groupDataList",groupDataList);
                        updateStaffPasswordForm = convertBeanToForm(staffData);
                        request.setAttribute("changeStaffPasswordForm", updateStaffPasswordForm);
                    }
                    return mapping.findForward(CHANGE_PASSWORD);
                } else if (updateStaffPasswordForm.getAction().equalsIgnoreCase("update")) {
                   
                	/* Encrypt User password */
    				String encryptedNewPassword = PasswordEncryption.getInstance().crypt(updateStaffPasswordForm.getNewPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
                    
                	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    				String actionAlias = ACTION_ALIAS;
                    
                    staffBLManager.changePassword(updateStaffPasswordForm.getStaffId(), updateStaffPasswordForm.getPassword(), encryptedNewPassword,actionAlias);
                    doAuditing(staffData, actionAlias);
                    return mapping.findForward(VIEW_FORWARD);
                }
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
