package com.elitecore.nvsmx.system.interceptor;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * @author kirpalsinh.raj
 *
 */
public class AuditInterceptor extends BaseInterceptor {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = AuditInterceptor.class.getSimpleName();
	
	@Override
	public String doIntercept(ActionInvocation actionInvocation) throws Exception {
		return actionInvocation.invoke();
	}

}
