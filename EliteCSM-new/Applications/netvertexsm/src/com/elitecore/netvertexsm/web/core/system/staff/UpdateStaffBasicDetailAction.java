package com.elitecore.netvertexsm.web.core.system.staff;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.UpdateStaffBasicDetailForm;
import com.elitecore.passwordutil.base64.Base64;

public class UpdateStaffBasicDetailAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateStaffBasicDetail";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String RADIUS_LOGIN_FORM = "radiusLoginForm";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_STAFF_BASIC_DETAIL_ACTION;
	private static final int MAX_FILE_SIZE = (2*1024*1024);
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			
			StaffBLManager staffBLManager = new StaffBLManager();
			
			ActionErrors errors = new ActionErrors();
	
			try{
				UpdateStaffBasicDetailForm updateStaffBasicDetailForm = (UpdateStaffBasicDetailForm)form;
				String strStaffId = request.getParameter("staffid");
				long staffId;
				if(strStaffId == null){
					staffId = updateStaffBasicDetailForm.getStaffId();
				}else{
					staffId = Long.parseLong(request.getParameter("staffid"));
				}
				request.setAttribute("staffId", staffId);
				if(updateStaffBasicDetailForm.getAction() == null){
					if(staffId  > 0){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						staffData = staffBLManager.getStaff(staffData);
						List<StaffGroupRoleRelData> staffGroupRoleRelDatas = staffBLManager.getStaffGroupRoleRelList(staffId);
						staffData.setStaffGroupRoleRelationList(staffGroupRoleRelDatas);
						request.setAttribute("staffData",staffData);
						request.setAttribute("staffGroupRoleRelDatas", staffGroupRoleRelDatas);
						updateStaffBasicDetailForm = convertBeanToForm(staffData);
						request.setAttribute("updateStaffBasicDetailForm",updateStaffBasicDetailForm);
						IStaffData loggedInUserData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						request.setAttribute("loggedInUserData", loggedInUserData);
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateStaffBasicDetailForm.getAction().equalsIgnoreCase("update")) {
					IStaffData istaffData = convertFormToBean(updateStaffBasicDetailForm);
					StaffProfilePictureData staffProfilePicture = staffBLManager.getStaffProfilePicture(staffId);
					if(staffProfilePicture ==null){
						staffProfilePicture = new StaffProfilePictureData();
						staffProfilePicture.setId(staffId+"");
					}
					if (Strings.isNullOrBlank(updateStaffBasicDetailForm.getProfilePicture()) == false) {
						String[] profilePictureArray = updateStaffBasicDetailForm.getProfilePicture().split(",");
						String encodedString = profilePictureArray[1];
						byte[] decodedBytes = Base64.decode(encodedString);
						ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
						String contentType = URLConnection.guessContentTypeFromStream(bis);
						if (contentType == null || contentType.contains("image") == false) {
							Logger.logWarn(MODULE, "Invalid Profile Picture size or format");
							Object errorElements[] = new String[]{"Invalid profile picture format. Profile Picture must be of jpg, jpeg or png format"};
							request.setAttribute("errorDetails", errorElements);
							ActionMessages messages = new ActionMessages();
							ActionMessage message = new ActionMessage("staff.profile.picture.error.reason");
							messages.add("information", message);
							saveErrors(request, messages);

							ActionMessages errorHeadingMessage = new ActionMessages();
							message = new ActionMessage("staff.error.heading", "updating");
							errorHeadingMessage.add("errorHeading", message);
							saveMessages(request, errorHeadingMessage);
							return mapping.findForward(FAILURE_FORWARD);

						}
						if (decodedBytes.length > MAX_FILE_SIZE) {
							Logger.logWarn(MODULE, "Invalid Profile Picture size or format");
							Object errorElements[] = new String[]{"Invalid Profile Picture Size: " + EliteUtility.convertBytesToSuitableUnit(decodedBytes.length) + " . Profile Picture size must not exceed 2MB's"};
							request.setAttribute("errorDetails", errorElements);
							ActionMessages messages = new ActionMessages();
							ActionMessage message = new ActionMessage("staff.profile.picture.error.reason");
							messages.add("information", message);
							saveErrors(request, messages);

							ActionMessages errorHeadingMessage = new ActionMessages();
							message = new ActionMessage("staff.error.heading", "updating");
							errorHeadingMessage.add("errorHeading", message);
							saveMessages(request, errorHeadingMessage);
							return mapping.findForward(FAILURE_FORWARD);
						}
						staffProfilePicture.setProfilePicture(decodedBytes);

					}

					istaffData.setLastModifiedDate(new Timestamp((new Date()).getTime()));
					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute(RADIUS_LOGIN_FORM)));
					String actionAlias = ACTION_ALIAS;
					staffBLManager.updateBasicDetail(istaffData, staffData, actionAlias,staffProfilePicture);

					return mapping.findForward(VIEW_FORWARD);
				}
			}catch(Exception e){
				e.printStackTrace();
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("staff.updatebasicdetail.failure")); 
	        saveErrors(request,errors);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("staff.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("staff.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private UpdateStaffBasicDetailForm  convertBeanToForm(IStaffData staffData){
		UpdateStaffBasicDetailForm updateStaffBasicDetailForm = null;
		if(staffData!=null){
			updateStaffBasicDetailForm = new UpdateStaffBasicDetailForm();
			updateStaffBasicDetailForm.setStaffId(staffData.getStaffId());
			updateStaffBasicDetailForm.setName(staffData.getName());
			updateStaffBasicDetailForm.setUserName(staffData.getUserName().trim());
			updateStaffBasicDetailForm.setEmailAddress(staffData.getEmailAddress());
			updateStaffBasicDetailForm.setPhone(staffData.getPhone());
			updateStaffBasicDetailForm.setMobile(staffData.getMobile());
		}
		return updateStaffBasicDetailForm;
	}
	
	private IStaffData convertFormToBean(UpdateStaffBasicDetailForm updateStaffBasicDetailForm){
		IStaffData staffData  = null;
		if(updateStaffBasicDetailForm!=null){
			staffData = new StaffData();
			staffData.setStaffId(updateStaffBasicDetailForm.getStaffId());
			staffData.setName(updateStaffBasicDetailForm.getName().trim());
			staffData.setUserName(updateStaffBasicDetailForm.getUserName());
			staffData.setEmailAddress(updateStaffBasicDetailForm.getEmailAddress());
			staffData.setPhone(updateStaffBasicDetailForm.getPhone());
			staffData.setMobile(updateStaffBasicDetailForm.getMobile());
		}
		return staffData;
	}
	
}
