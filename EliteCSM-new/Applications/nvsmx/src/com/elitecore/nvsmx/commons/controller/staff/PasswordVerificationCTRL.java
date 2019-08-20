package com.elitecore.nvsmx.commons.controller.staff;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PasswordVerificationCTRL extends ActionSupport implements ServletRequestAware, SessionAware{


	private static final long serialVersionUID = 1L;
	private static final String MODULE = "PASS-VERIFICATION-CTRL";
	private StaffData staff = new StaffData();
	private HttpServletRequest request;
	private SessionMap<String, Object> session;
	private String actionChainUrl;

	@SkipValidation
	public String verifyPassword(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called verifyPassword()");
		}
		try {
			staff = (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
			String password = request.getParameter("password");
			if(staff != null && Strings.isNullOrBlank(password) == false) {
				if(PasswordUtility.getEncryptedPassword(password).equals(staff.getPassword())){
					return SUCCESS;
				}
			}
			actionChainUrl = request.getParameter("failoverUrl");
			addActionError("Incorrect Password! Please enter correct password");
		} catch (Exception e) {
			getLogger().error(MODULE,"Failed to verify password. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		}

		return Results.REDIRECT_ACTION.getValue();
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = (SessionMap<String, Object>) session;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

}
