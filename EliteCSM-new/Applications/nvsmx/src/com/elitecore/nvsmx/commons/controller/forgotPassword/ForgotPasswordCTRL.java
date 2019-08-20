package com.elitecore.nvsmx.commons.controller.forgotPassword;

import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.commons.model.forgotPassword.ForgotPasswordData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ForgotPasswordCTRL extends ActionSupport implements SessionAware, ServletRequestAware, ModelDriven<ForgotPasswordData> {
	
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	private Map<String, Object> session;
	private ForgotPasswordData forgotPassword = new ForgotPasswordData();

	public ForgotPasswordData getForgotPassword() {
		return forgotPassword;
	}

	public void setForgotPassword(ForgotPasswordData forgotPassword) {
		this.forgotPassword = forgotPassword;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	@SkipValidation
	public String initForgotPassword(){
		return Results.FORGOT_PASSWORD.getValue();
	}
	
	@InputConfig(resultName="forgotPassword")
	public String forgotPassword(){
		StaffData staffData = StaffDAO.getStaffByUserName(forgotPassword.getUsername());
	
		if(staffData == null){
			incrementFailureCount();
			addActionError(getText("invalid.username"));
			return Results.FORGOT_PASSWORD.getValue();
		}else{
			addActionMessage(getText("forgotpassword.success"));
			return Results.FORGOT_PASSWORD.getValue();
		}
	}
	
	private void incrementFailureCount(){
		Integer captchaFailureCount = (Integer) session.get(Attributes.CAPTCHA_FAILURE_COUNT);
		if(captchaFailureCount == null || captchaFailureCount == 0){
			captchaFailureCount = 1;
		}else{
			captchaFailureCount += 1;
		}
		session.put(Attributes.CAPTCHA_FAILURE_COUNT, captchaFailureCount);
	}

	@Override
	public ForgotPasswordData getModel() {
		return forgotPassword;
	}
}
