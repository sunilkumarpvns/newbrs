package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.util.ReflectionUtil;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.filter.SSOAuthenticator;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author kirpalsinh.raj
 *
 */
public class AuthenticationInterceptor extends MethodFilterInterceptor  {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "AUTHENTICATION-INTCPTR";
    HttpServletRequest request;
    
    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
		final ActionContext context = actionInvocation.getInvocationContext();
		HttpServletResponse response = (HttpServletResponse) context.get(StrutsStatics.HTTP_RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);
		Object action = actionInvocation.getAction();
		if (response != null) {
			setCacheParameters(response);
		}

		if (SystemParameterDAO.isSSOEnable()) {
			SSOAuthenticator ssoAuthenticator = new SSOAuthenticator(request);
			if (ssoAuthenticator.authenticate()) {
				return actionInvocation.invoke();
			}else{

				if (action instanceof EliteGenericCTRL) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Redirecting user to login page. Reason: sso authentication failed");
					}
					List<Method> setActionChainUrlMethodList = ReflectionUtil.getDeclaredMethodsAnnotatedWith(action.getClass(), ActionChain.class);
					for (Method method : setActionChainUrlMethodList) {
						method.invoke(action, "commons/login/Login/initLogin");
						return Results.SSO_LOGIN.getValue();
					}
				}
			}
		}
		if (isAuthenticatedUser(actionInvocation, request)) {
			return actionInvocation.invoke();
		}
		return Action.LOGIN;
	}

	private void setCacheParameters(HttpServletResponse response) {
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");
	}

	private boolean isAuthenticatedUser(ActionInvocation actionInvocation, HttpServletRequest request){


		Map<String, Object> session = actionInvocation.getInvocationContext().getSession();
		String userName = (String) session.get(Attributes.STAFF_USERNAME);
		if (Strings.isNullOrBlank(userName)) {
			getLogger().info(MODULE, "User is not authenticated");
			return false;
		}
		return true;
	}
}
