package com.elite.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.elite.action.LoginAction;
import com.elite.user.Userbean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class LoginInterceptor implements Interceptor{
	Logger logger = Logger.getLogger("wsc");
	public void destroy() {
		
		logger.info("LoginInterceptor intrecepter ended");
		
	}
	public void init() {
		logger.info("LoginInterceptor intrecepter stated");
		
	}
	
	public String intercept(ActionInvocation invocation) throws Exception {
		final ActionContext context = invocation.getInvocationContext();
	    Map session =  context.getSession();
	    Userbean user = (Userbean)session.get("user");
	    if (user == null) {
	    	LoginAction action = null;
	    	try
	    	{
	    		action = (LoginAction)invocation.getAction();
	    	}
	    	catch(ClassCastException e)
	    	{
	    		return "SessionExpire";
	    	}

	    	if (action != null) 
	    	{
	    		return invocation.invoke();
	        }
	    } 
        return invocation.invoke();
	}

}
