package com.elitecore.nvsmx.commons.controller.login;

import com.elitecore.corenetvertex.constants.CommonStatus;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.corenetvertex.sm.acl.AuthenticationMode;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.nvsmx.commons.model.login.ChangePasswordData;
import com.elitecore.nvsmx.commons.model.login.LoginData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.staff.StaffLDAPProfile;
import com.elitecore.nvsmx.system.constants.*;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.filter.SSOAuthenticator;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.policy.PasswordPolicyDAO;
import com.elitecore.nvsmx.system.util.DataInitializer;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.keycloak.KeycloakSecurityContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.commons.model.staff.StaffDAO.prepareStaffDataFromSSO;
import static org.apache.struts2.ServletActionContext.getRequest;

/**
 * @author kirpalsinh.raj
 */
public class LoginCTRL extends ActionSupport implements SessionAware, ServletRequestAware {

	private static final String SM_SSOURL = "smSSOURL";
	private static final long serialVersionUID = 1L;
	private static final String MODULE = LoginCTRL.class.getSimpleName();
	private HttpServletRequest request;

	private SessionMap<String, Object> session;
	private String message = "";

	private LoginData login = new LoginData();
	private StaffData staffData;
	private ChangePasswordData changePasswordData = new ChangePasswordData();
	private String actionChainUrl;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = (SessionMap<String, Object>) session;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@SkipValidation
	public String initLogin() throws Exception {
		if (isSsoEnable()) {
			return login();
		}
		return Results.LOGIN.getValue();

	}

	private boolean isSsoEnable() {
		return SystemParameterDAO.isSSOEnable();
	}


	@SkipValidation
	public String welcome() {
		request.getSession().setAttribute(Attributes.MENU_TYPE, ModuleConstants.POLICYDESIGNER.getVal());
		return Results.WELCOME.getValue();
	}

	@SkipValidation
	public String partnerRncWelcome() {
		request.getSession().setAttribute(Attributes.MENU_TYPE, ModuleConstants.PARTNERRNC.getVal());
		return Results.WELCOME.getValue();
	}

	@SkipValidation
	public String serverManagerWelcome() {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Method called serverManagerWelcome");
		}
		setActionChainUrl("sm/servergroup/server-group");

		return Results.REDIRECT_ACTION.getValue();
	}

	@InputConfig(resultName = InputConfigConstants.LOGIN)
	public String login() throws Exception {

		getLogger().info(MODULE, "Method called login()");
		if (HibernateSessionFactory.isInitialized() == false) {
			getLogger().info(MODULE, "Database connection not found");
			addActionError(getText("database.down.note"));
			return Results.LOGIN.getValue();
		}

		try {

			if (isSsoEnable()) {
				return verificationWithSSO();
			}

			staffData = StaffDAO.getStaffByUserName(login.getUsername());
			getLogger().info(MODULE, "User: " + login.getUsername() + " logging in.");

			if (staffData == null) {
				StaffLDAPProfile staffLDAPProfile = getProfileFromLdap(login.getUsername());

				if (staffLDAPProfile == null) {
					return handleInvalidError("invalid.username");
				}

				staffData = StaffLDAPProfile.buildStaffData(staffLDAPProfile);
				CRUDOperationUtil.save(staffData);
				setGroupsForOtherUser(staffData);
				updateLastLoginInfo(staffData);
				return Results.WELCOME.getValue();
			}

			//TODO required to check for LDAP also -- Dhyani
			if (CommonStatus.ACTIVE.name().equals(staffData.getStatus()) == false) {
				getLogger().error(MODULE, "Account is Inactive for user: " + staffData.getUserName());
				return handleInvalidError("general.login.failed");
			}

			if (AuthenticationMode.LDAP.name().equals(staffData.getAuthenticationMode())) {
				StaffLDAPProfile staffLDAPProfile = getProfileFromLdap(login.getUsername());
				if (staffLDAPProfile == null) {
					return handleInvalidError("invalid.username");
				}
				if (isValidLDAPAuthentication(staffLDAPProfile) == false) {
					return handleInvalidError("invalid.password");
				}
				setGroupsForOtherUser(staffData);
				return Results.WELCOME.getValue();
			}

			if (checkForInvalidPassword()) {
				return handleInvalidError("invalid.password");
			}

			PasswordPolicyConfigData passwordPolicyConfigData = PasswordPolicyDAO.getPasswordSelectionPolicy();
			String result = checkPasswordValidityAndChangePasswordOnFirstLogin(staffData, passwordPolicyConfigData);

			if (Results.COMPULSARY_CHANGE.getValue().equals(result)) {
				changePasswordData.setStaffList(Arrays.asList(staffData));
				changePasswordData.setPasswordPolicyConfigData(passwordPolicyConfigData);
				request.getSession().setAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE, false);
				return Results.COMPULSARY_CHANGE.getValue();
			}


			if (login.getUsername().equals(NVSMXCommonConstants.ADMIN)) {
				setGroupsForSuperAdmin();
			} else {
				setGroupsForOtherUser(staffData);
			}
			staffData.setACLChecker(StaffDAO.getStaffAclCheckerForSPROperations(staffData));
			updateLastLoginInfo(staffData);
			setStaffInfoInSession(staffData);

			if (Results.MANUAL_CHANGE.getValue().equals(result)) {
				changePasswordData.setStaffList(Arrays.asList(staffData));
				changePasswordData.setPasswordPolicyConfigData(passwordPolicyConfigData);
				request.getSession().setAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE, true);
				setActionChainUrl(NVSMXCommonConstants.MANUAL_PASSWORD_PAGE_URL);
				return Results.REDIRECT_ACTION.getValue();
			}


			if (SystemParameterDAO.isMandatorySystemParamaterConfigured() == false) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "System country & operator is not configured. So redirecting to system parameter configuration page.");
				}
				setActionChainUrl("/sm/systemparameter/system-parameter/*/edit");
				return Results.REDIRECT_ACTION.getValue();
			}

			return Results.WELCOME.getValue();

		} catch (HibernateDataException e) {
			getLogger().error(MODULE, e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText("contact.administrator"));
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while login with user: " + login.getUsername() + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText("contact.administrator"));
		}
		return Results.LOGIN.getValue();
	}

	private boolean isValidLDAPAuthentication(StaffLDAPProfile staffLDAPProfile) {
		return login.getPassword().equals(staffLDAPProfile.getPassword());
	}

	private String handleInvalidError(String invalidField) {
		addActionError(getText(invalidField));
		incrementFailureCount();
		return Results.LOGIN.getValue();
	}

	private void setGroupsForSuperAdmin() {

		List<GroupData> allGroups = CRUDOperationUtil.findAll(GroupData.class);
		request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getGroupIds(allGroups));
		request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP, allGroups);

	}

	private void setGroupsForOtherUser(StaffData staffData) {

		request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP, new ArrayList<>(staffData.getGroupDatas()));
		request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getStaffBelongingsGroup(staffData));
		request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, staffData.getGroupIdRoleDataMap());
		request.getSession().setAttribute(Attributes.STAFF_ROLES_SET, staffData.getRoleDatas());

	}

	private void setStaffInfoInSession(StaffData staffData) {

		session.put(Attributes.STAFF_USERNAME, staffData.getUserName());
		session.put(Attributes.STAFF_NAME, staffData.getName());
		session.put(Attributes.STAFF_DATA, staffData);
		session.put(Attributes.STAFF_ID, staffData.getId());
		session.put(Attributes.LOGGED_IN_STAFF_PROFILE_PICTURE_ID, staffData.getProfilePictureId());
		session.put(Attributes.MENU_TYPE, ModuleConstants.POLICYDESIGNER.getVal());
		resetFailureCount();
	}

	private void updateLastLoginInfo(StaffData staffData) {
		staffData.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
		CRUDOperationUtil.merge(staffData);
	}

	/**
	 * Change Password Date Is Null And Not Match With Encrypted And PainText Password
	 * OR
	 * Change Password Date Is Not Null And Not Match With Encrypted Password
	 *
	 * @return boolean
	 */
	public boolean checkForInvalidPassword() throws NoSuchEncryptionException, EncryptionFailedException {
		return ((staffData.getPasswordChangeDate() == null && staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(login.getPassword())) == false && staffData.getPassword().equals(login.getPassword()) == false)
				|| staffData.getPasswordChangeDate() != null && staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(login.getPassword())) == false);
	}

	private String checkPasswordValidityAndChangePasswordOnFirstLogin(StaffData staffData, PasswordPolicyConfigData passwordPolicyConfigData) {

		boolean changePwdOnFirstLogin = passwordPolicyConfigData.getChangePwdOnFirstLogin();
		Date lastPasswordChangeDate = staffData.getPasswordChangeDate();
		Date lastLoginTime = staffData.getLastLoginTime();


		if (changePwdOnFirstLogin) {
			if (lastPasswordChangeDate == null) {
				getLogger().info(MODULE, "User is logging first time. Its good to change password on first login.");
				addActionMessage("You are required to change your password on your first login!!!");
				return Results.COMPULSARY_CHANGE.getValue();
			}
		}

		if (lastPasswordChangeDate == null && lastLoginTime == null) {
			getLogger().info(MODULE, "Last password changed date is Null. User should change password.");
			addActionMessage("User should change password.");
			return Results.MANUAL_CHANGE.getValue();
		}

		if (lastPasswordChangeDate != null) {
			Integer validityDays = passwordPolicyConfigData.getPasswordValidity();
			long modifiedDate = lastPasswordChangeDate.getTime();
			Date currentDate = new Date();
			long curDate = currentDate.getTime();
			long day = java.util.concurrent.TimeUnit.DAYS.toMillis(1);
			int remainingDays = (int) ((curDate - modifiedDate) / day);

			if (validityDays != null && validityDays != 0) {
				if ((validityDays) < remainingDays) {
					getLogger().info(MODULE, "Password Expired, User Required to change Password.");
					addActionMessage("Password Expired, User Required to change Password.");
					return Results.COMPULSARY_CHANGE.getValue();

				}
			}
		}
		return Results.WELCOME.getValue();
	}

	@SkipValidation
	public String logout() throws ServletException {
		if (session != null) {

			String userName = (String) request.getSession().getAttribute(Attributes.STAFF_USERNAME);

			staffData = StaffDAO.getStaffByUserName(userName);

			if (staffData != null) {
				Timestamp lastLoginTime = staffData.getLastLoginTime();
				if (lastLoginTime != null) {
					staffData.setLastLoginDuration((System.currentTimeMillis() - lastLoginTime.getTime()) / 1000);
					CRUDOperationUtil.save(staffData);
				}
			}

			ServletContext pdServletContext = request.getSession().getServletContext();
			if (pdServletContext != null) {
				pdServletContext.removeAttribute(SM_SSOURL);
				pdServletContext.removeAttribute("target");
				pdServletContext.removeAttribute(Attributes.SSO_USERNAME);
				pdServletContext.removeAttribute(Attributes.SSO_PASSWORD);
				getLogger().debug(MODULE, "SSO attributes removed from pd context");
			}

            getRequest().logout();
            session.invalidate();
            getLogger().debug(MODULE, "Session Invalidated successfully");
        }
        if (isSsoEnable()) {

            return Results.SSO_LOGIN.getValue();
        }

        return Results.LOGIN.getValue();
    }


	public LoginData getLogin() {
		return login;
	}

	public void setLogin(LoginData login) {
		this.login = login;
	}

	private void incrementFailureCount() {

		Integer loginFailureCount = (Integer) session.get(Attributes.CAPTCHA_FAILURE_COUNT);
		if (loginFailureCount == null || loginFailureCount == 0) {
			loginFailureCount = 1;
		} else {
			loginFailureCount += 1;
		}
		session.put(Attributes.CAPTCHA_FAILURE_COUNT, loginFailureCount);
	}



	private void resetFailureCount() {
		session.put(Attributes.CAPTCHA_FAILURE_COUNT, 0);
	}

	public ChangePasswordData getChangePasswordData() {
		return changePasswordData;
	}

	public void setChangePasswordData(ChangePasswordData changePasswordData) {
		this.changePasswordData = changePasswordData;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	public StaffLDAPProfile getProfileFromLdap(String userName) throws OperationFailedException {
		if(Objects.isNull(DataInitializer.getInstance().getStaffLDAPInterface())){
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "LDAP authentication can't be performed. Reason: Staff LDAP SP interface is not initialized/configured.");
			}
			return null;
		}
		return DataInitializer.getInstance().getStaffLDAPInterface().getProfile(userName);
	}


	private String verificationWithSSO() {

        SSOAuthenticator ssoAuthenticator = new SSOAuthenticator(getRequest());

        if (ssoAuthenticator.authenticate() == false) {
            return Results.SSO_LOGIN.getValue();
        }
        KeycloakSecurityContext ssoSession = ssoAuthenticator.getSsoSession();

        String loginUserName = ssoSession.getToken().getPreferredUsername();

        StaffData staffByUserName = StaffDAO.getStaffByUserName(loginUserName);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Staff with user name: "+loginUserName+" doesn't exist in SM. So provisioning the staff");
		}

        if (staffByUserName == null) {
            StaffData staffData = prepareStaffDataFromSSO(ssoSession, loginUserName);
            CRUDOperationUtil.save(staffData);
            setGroupsForOtherUser(staffData);
            setStaffInfoInSession(staffData);
            updateLastLoginInfo(staffData);
            return Results.WELCOME.getValue();
        }
        staffData = staffByUserName;
        if (loginUserName.equals(NVSMXCommonConstants.ADMIN)) {
            setGroupsForSuperAdmin();
            setStaffInfoInSession(staffData);
            return Results.WELCOME.getValue();
        } else {
            setGroupsForOtherUser(staffData);
            setStaffInfoInSession(staffData);
            updateLastLoginInfo(staffData);
            return Results.WELCOME.getValue();
        }
    }
}
