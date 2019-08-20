package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.commons.controller.login.LoginCTRL;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.hibernate.HibernateException;

/**
 * This is used to verify database connection for LoginCTRL
 * extends MethodFilterInterceptor to provide include/exclude functionality.
 * 
 * @author dhyani.raval
 */

public class VerifyLoginDBConnectionInterceptor extends MethodFilterInterceptor{
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "VERIFY-LOGIN-DBCONN-INTCPTR";
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {

		try{
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"VerifyLoginDBConnectionInterceptor");
			}

			if(HibernateSessionFactory.isInitialized() == false){
				throw new HibernateException("SessionFactory is not initialized");
			}
			HibernateSessionFactory.verifySessionFactory();
			return invocation.invoke();
		}catch(HibernateException e){
			addPreResultListener(invocation);
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			return Results.REDIRECT_ACTION.getValue();
		}
    }
    private void addPreResultListener(ActionInvocation invocation){
		invocation.addPreResultListener((actionInvocation, resultCode) -> {
			LoginCTRL loginCTRL = (LoginCTRL) actionInvocation.getAction();
			loginCTRL.setActionChainUrl("commons/databasesetup/DatabaseSetUp/initConfigureDatabaseSetUp");
			loginCTRL.addActionError(loginCTRL.getText("database.down.note"));
		});
    }
}
