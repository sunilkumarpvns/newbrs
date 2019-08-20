package com.elitecore.nvsmx.commons.controller.staff;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.passwordutil.base64.Base64;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Map;

public class StaffCTRL extends ActionSupport implements SessionAware, ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "STAFF-CTRL";
	private static final String action = "/commons/staff/Staff/view";
	private static final int MAX_FILE_SIZE = (2*1024*1024);
	private StaffData staff = new StaffData();
	private String actionChainUrl;
	
	private HttpServletRequest request;
	private SessionMap<String, Object> session;
	
	@SkipValidation
	public String view() {
		try{
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Method called view()");
			}
			String username = (String) session.get(Attributes.STAFF_USERNAME);
			staff = StaffDAO.getStaffByUserName(username);
				String passwordValidity = CRUDOperationUtil.getProperty(PasswordPolicyConfigData.class, "passwordValidity");
				staff.setPasswordValidityPeriod(Long.parseLong(passwordValidity));
	
			request.setAttribute(Attributes.STAFF_PASSWORD, staff.getPassword());


			return Results.VIEW.getValue();
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error while viewing staff profile of Staff : '" + staff.getName() +"'. Reason: "+ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			addActionError(staff.getUserName() + getText(ActionMessageKeys.VIEW_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
	}

	@SkipValidation
	public String initUpdate() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called initUpdate()");
		}
		String username = (String) session.get(Attributes.STAFF_USERNAME);
		staff = StaffDAO.getStaffByUserName(username);
		return Results.UPDATE.getValue();
	}
	
	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called update()");
		}
		setActionChainUrl(action);
		try {
			String id = (String) session.get(Attributes.STAFF_ID);

			StaffData staffInDb = StaffDAO.getStaffById(id);
			staffInDb.setName(staff.getName());
			staffInDb.setEmailAddress(staff.getEmailAddress());
			staffInDb.setPhone(staff.getPhone());
			staffInDb.setMobile(staff.getMobile());
			staffInDb.setModifiedDateAndStaff((StaffData) session.get(Attributes.STAFF_DATA));

			//TODO provide support for staff profile picture
			String profilePicture = request.getParameter("profilePicture");
			if (Strings.isNullOrBlank(profilePicture) == false) {
				StaffProfilePictureData staffProfilePictureInDb = null;
				if(Strings.isNullOrBlank(staffInDb.getProfilePictureId()) == false){
					staffProfilePictureInDb = CRUDOperationUtil.get(StaffProfilePictureData.class,staffInDb.getProfilePictureId());//  StaffDAO.getStaffProfilePicture(id);
				}

				String[] profilePictureArray = profilePicture.split(",");
				String encodedString = profilePictureArray[1];
				byte[] decodedBytes = Base64.decode(encodedString);
				ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
				String contentType = URLConnection.guessContentTypeFromStream(bis);
				if (contentType == null || contentType.contains("image") == false) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Invalid Profile Picture size or format");
					}
					Object errorElements[] = new String[] { "Invalid profile picture format. Profile Picture must be of jpg, jpeg or png format" };
					addActionError(errorElements.toString());
					return Results.REDIRECT_ERROR.getValue();

				}
				if (decodedBytes.length > MAX_FILE_SIZE) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE,
								"Invalid Profile Picture size or format");
					}
					Object errorElements[] = new String[] { "Invalid Profile Picture Size: " + decodedBytes.length + " . Profile Picture size must not exceed 2MB's" };
					addActionError(errorElements.toString());
					return Results.REDIRECT_ERROR.getValue();
				}
				if(staffProfilePictureInDb == null){
					staffProfilePictureInDb = new StaffProfilePictureData();
					staffProfilePictureInDb.setProfilePicture(decodedBytes);
					CRUDOperationUtil.save(staffProfilePictureInDb);
         		}else{
					staffProfilePictureInDb.setProfilePicture(decodedBytes);
					CRUDOperationUtil.merge(staffProfilePictureInDb);
				}
				staffInDb.setProfilePictureId(staffProfilePictureInDb.getId());
			}
			CRUDOperationUtil.merge(staffInDb);
			addActionMessage("Profile Updated Successfully");
			return Results.REDIRECT_ACTION.getValue();
			
 		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while updating staff profile of Staff : '" + staff.getUserName() +"'. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			addActionError(staff.getUserName() + getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
		
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = (SessionMap<String, Object>) session;
		
	}
	
	public StaffData getStaff() {
		return staff;
	}

	public void setStaff(StaffData staff) {
		this.staff = staff;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	

}