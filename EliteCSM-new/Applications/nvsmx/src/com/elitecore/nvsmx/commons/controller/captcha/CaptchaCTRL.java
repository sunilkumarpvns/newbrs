package com.elitecore.nvsmx.commons.controller.captcha;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.google.code.jcaptcha4struts2.common.actions.JCaptchaBaseAction;
import com.google.code.jcaptcha4struts2.core.PluginConstants;
import com.google.code.jcaptcha4struts2.core.validation.JCaptchaValidator;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class CaptchaCTRL extends JCaptchaBaseAction implements ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "CAPTCHA-CTRL";
	private static final String LOGIN = "login";
	private static final String UNSUBSCRIBE_ADDON = "unsubscribeAddOn";
	private HttpServletRequest request;

	@Override
	protected void doValidateCaptcha() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Validating  : " + getJCaptchaResponse());
		}
		
		if(checkForLoginFailureCount()){
			try{
				if (JCaptchaValidator.validate() == false) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "validation failed (input string : '" + getJCaptchaResponse() + "'), field error added");
					}
					addFieldError(PluginConstants.J_CAPTCHA_RESPONSE, getValidationErrorMessage());
				}
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
	private boolean checkForLoginFailureCount(){
		Integer loginFailureCount = (Integer) request.getSession().getAttribute(Attributes.CAPTCHA_FAILURE_COUNT);
		String method = getMethod(); 
		//Do not intercept login if failure count is below 3
		return LOGIN.equalsIgnoreCase(method) == false || (loginFailureCount != null && loginFailureCount >= 3);
	}
	
	private String getMethod() {
		String requestPath = request.getRequestURI();
		//Eg. requestPath will be captcha/policydesigner/subscriber/Subscriber/unsubscribeAddOn.action 
		//where method name is to be extracted i.e. "unsubscribeAddOn"
		int beginIndex = requestPath.lastIndexOf(CommonConstants.FORWARD_SLASH);
		int endIndex = requestPath.indexOf(CommonConstants.DOT);
		return  requestPath.substring(beginIndex + 1, endIndex);
	}
	
	
	/*
	 * Incase of captcha failure this method will be called, the flow incase of captcha failure will be defined here. 
	 * Default behavior will be to return the result as method name.
	 * If any other behavior or processing is required, it should be added here.
	 * 
	 * */
	@SkipValidation
	public String captchaRedirect() {
		String method = getMethod();
		int failureCount = getAndIncrementFailureCount(Attributes.CAPTCHA_FAILURE_COUNT);
		int startCaptchaValidation = 0;
		if (UNSUBSCRIBE_ADDON.equalsIgnoreCase(method) == false) {
			startCaptchaValidation = failureCount - 3;
		}else{
			startCaptchaValidation = failureCount;
		}
		handleCaptchaFailure(startCaptchaValidation);
		addFieldError(PluginConstants.J_CAPTCHA_RESPONSE, getValidationErrorMessage());
		//Add module specific captcha failure mechanism here, if required
		if (UNSUBSCRIBE_ADDON.equalsIgnoreCase(method)) {
			handleUnsubscribeAddOnFailure();
			return Results.VIEW_DELETE_ADDON.getValue();
		}else {
			return method;
		}
	}

	private void handleCaptchaFailure(int startCaptchaValidation){
		int maxFailureCount = ConfigurationProvider.getInstance().getMaxFailureCount();
		int blockingPeriod = ConfigurationProvider.getInstance().getBlockingPeriod();

		if(startCaptchaValidation >= maxFailureCount){
			if(LogManager.getLogger().isWarnLogLevel()){
				LogManager.getLogger().warn(MODULE, "Blocking IP for Session-Id: " + request.getSession().getId() +
						" Reason: More than allowed failed attempts to login from the same IP");
			}
			blockIP();
			if(blockingPeriod == 0 ) {
				return;
			}
			addActionError(getText("blocked.ip"));

		}
	}

	private void handleUnsubscribeAddOnFailure() {
		String addOnId =  request.getParameter("addonId");
		String addonSubscriptionId = request.getParameter("addonSubscriptionId");
		request.getSession().setAttribute(Attributes.ADDON_ID,addOnId);
		request.getSession().setAttribute(Attributes.ADDON_SUBSCRIPTION_ID, addonSubscriptionId);
		getAndIncrementFailureCount(Attributes.UNSUBSCRIBE_FAILURE_COUNT);
	}

	private int getAndIncrementFailureCount(String failureCount ){
		Integer count = (Integer) request.getSession().getAttribute(failureCount);
		if(count == null || count ==  0){
			count = 1;
		}else{
			count += 1;
		}
		request.getSession().setAttribute(failureCount, count);
		return count;
	}

	private void blockIP() {
		int blockingUnit = ConfigurationProvider.getInstance().getBlockingPeriod();
		long blockingPeriod = TimeUnit.MILLISECONDS.convert(blockingUnit, TimeUnit.MINUTES);
		long releaseTime = System.currentTimeMillis() + blockingPeriod;
		String ip = request.getRemoteAddr();
		getBlockedIPreleaseTimeMap().putIfAbsent(ip,releaseTime);
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	private Map<String, Long> getBlockedIPreleaseTimeMap(){
		return (Map<String, Long>) request.getServletContext().getAttribute(Attributes.BLOCKED_IP_RELEASE_TIME_MAP);
	}
}
