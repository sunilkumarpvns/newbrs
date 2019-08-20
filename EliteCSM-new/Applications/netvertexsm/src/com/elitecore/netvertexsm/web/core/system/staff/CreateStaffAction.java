package com.elitecore.netvertexsm.web.core.system.staff;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.netvertexsm.util.PasswordUtility;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.CreateStaffForm;
import com.elitecore.passwordutil.base64.Base64;

public class CreateStaffAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "CREATE STAFF ACTION"; //NOSONAR
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_STAFF_ACTION;
	private static final String RADIUS_LOGIN_FORM = "radiusLoginForm";
	private static final String ACTIVE = "CST01";
	private static final String INACTIVE = "CST02";
	//2 MB's max size of the uploaded file
	private static final int MAX_FILE_SIZE = (2*1024*1024);

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			IStaffData staffData = null;
			IStaffData iStaffData = null;
			StaffProfilePictureData staffProfilePicture=new StaffProfilePictureData();
			try{
				CreateStaffForm staffForm = (CreateStaffForm)form;
				AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				StaffBLManager staffBLManager = new StaffBLManager();


				iStaffData = new StaffData();
				iStaffData.setName(staffForm.getName());
				iStaffData.setUserName(staffForm.getUserName());

				iStaffData.setPassword(PasswordUtility.getEncryptedPassword(staffForm.getPassword()));
				iStaffData.setPhone(staffForm.getPhone());
				iStaffData.setMobile(staffForm.getMobile());
				iStaffData.setEmailAddress(staffForm.getEmailAddress());

				if(Strings.isNullOrBlank(staffForm.getProfilePicture()) == false ){
					String[] profilePictureArray = staffForm.getProfilePicture().split(",");
					String encodedString = profilePictureArray[1];
					byte[] decodedBytes = Base64.decode(encodedString);
					ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
					String contentType = URLConnection.guessContentTypeFromStream(bis);
					if(contentType == null || contentType.contains("image") == false ){
						Logger.logWarn(MODULE,"Invalid Profile Picture size or format");
						Object errorElements[] = new String[]{"Invalid profile picture format. Profile Picture must be of jpg, jpeg or png format"};
						request.setAttribute("errorDetails", errorElements);
						ActionMessages messages = new ActionMessages();
						ActionMessage message = new ActionMessage("staff.profile.picture.error.reason");
						messages.add("information", message);
						saveErrors(request, messages);

						ActionMessages errorHeadingMessage = new ActionMessages();
						message = new ActionMessage("staff.error.heading","creating");
						errorHeadingMessage.add("errorHeading",message);
						saveMessages(request,errorHeadingMessage);	
						return mapping.findForward(FAILURE_FORWARD);

					}
					if(decodedBytes.length > MAX_FILE_SIZE){
						Logger.logWarn(MODULE,"Invalid Profile Picture size or format");
						Object errorElements[] = new String[]{"Invalid Profile Picture Size: " + EliteUtility.convertBytesToSuitableUnit(decodedBytes.length) +" . Profile Picture size must not exceed 2MB's"};
						request.setAttribute("errorDetails", errorElements);
						ActionMessages messages = new ActionMessages();
						ActionMessage message = new ActionMessage("staff.profile.picture.error.reason");
						messages.add("information", message);
						saveErrors(request, messages);

						ActionMessages errorHeadingMessage = new ActionMessages();
						message = new ActionMessage("staff.error.heading","creating");
						errorHeadingMessage.add("errorHeading",message);
						saveMessages(request,errorHeadingMessage);	
						return mapping.findForward(FAILURE_FORWARD);
					}
					staffProfilePicture.setProfilePicture(decodedBytes);
				}
			    long currentDate = new Date().getTime();
				iStaffData.setCreateDate(new Timestamp(currentDate));
				String userId = ((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)).getUserId();
				iStaffData.setCreatedByStaffId(userId);
				iStaffData.setStatusChangeDate(null);
				if(staffForm.getStatus()!= null && staffForm.getStatus().equals("1")){
					iStaffData.setCommonStatusId(ACTIVE);
				}else{
					iStaffData.setCommonStatusId(INACTIVE);
				}
				iStaffData.setLastLoginTime(null);
				iStaffData.setLastModifiedDate(new Timestamp(currentDate));
				iStaffData.setLastModifiedByStaffId(userId);
				iStaffData.setSystemGenerated("N");
				
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
				
				staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)));
				String actionAlias = ACTION_ALIAS;

				staffBLManager.create(iStaffData,staffData,actionAlias,staffProfilePicture);

				List listAccessGroupList = accessGroupBLManager.getAccessGroupList();
				request.setAttribute("listAccessGroupList",listAccessGroupList);
				request.setAttribute("responseUrl","/searchStaff");
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
				ActionMessage message = new ActionMessage("staff.create.duplicate.failure",iStaffData.getUserName());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
			}catch(Exception managerExp){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message = new ActionMessage("staff.create.failure");
				messages.add("information", message);
				saveErrors(request, messages);
			}

			ActionMessages errorHeadingMessage = new ActionMessages();
			ActionMessage message = new ActionMessage("staff.error.heading","creating");
			errorHeadingMessage.add("errorHeading",message);
			saveMessages(request,errorHeadingMessage);	            

			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessages messages = new ActionMessages();
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
			saveErrors(request, messages);

			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("staff.error.heading","creating");
			errorHeadingMessage.add("errorHeading",message);
			saveMessages(request,errorHeadingMessage);		        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}

