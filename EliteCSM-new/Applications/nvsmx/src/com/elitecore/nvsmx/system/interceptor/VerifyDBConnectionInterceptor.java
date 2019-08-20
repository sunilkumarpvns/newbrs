package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.nvsmx.system.constants.Results;
import org.hibernate.HibernateException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * This is used to verify database connection for all controller except LoginCTRL<br/>
 * if result is error/exception and connection not found then redirect to the login page.
 * extends MethodFilterInterceptor to provide include/exclude functionality.
 * 
 * @author dhyani.raval
 */
public class VerifyDBConnectionInterceptor extends MethodFilterInterceptor {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "VERIFY-DBCONN-INTCPTR";

    public String doIntercept(ActionInvocation invocation) throws Exception {
	invocation.addPreResultListener(new PreResultListener() {
	    @Override
	    public void beforeResult(ActionInvocation invocation, String resultCode) {
		if(Results.ERROR.getValue().equalsIgnoreCase(resultCode)){
		    try{
			HibernateSessionFactory.verifySessionFactory();
		    }catch(HibernateException e){
			redirectToLogin(invocation);
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		    }
		}
	    }
	});
	return invocation.invoke();
    }	
    private void redirectToLogin(ActionInvocation invocation){
	Object action = invocation.getAction();
	 ((ActionSupport)action).clearActionErrors();
	 ((ValidationAware)action).addActionError(((ActionSupport)action).getText("database.down.note"));
	 invocation.setResultCode(Results.LOGIN.getValue());
    }
}



