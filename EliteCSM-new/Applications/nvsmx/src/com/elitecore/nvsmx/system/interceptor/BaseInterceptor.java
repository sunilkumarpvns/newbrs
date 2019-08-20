package com.elitecore.nvsmx.system.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * @author kirpalsinh.raj
 *
 */
public abstract class BaseInterceptor extends MethodFilterInterceptor{
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = BaseInterceptor.class.getSimpleName();

}
