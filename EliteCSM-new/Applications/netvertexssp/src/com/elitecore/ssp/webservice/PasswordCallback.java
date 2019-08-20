package com.elitecore.ssp.webservice;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.MDC;
import org.apache.ws.security.WSPasswordCallback;

import com.elitecore.ssp.util.logger.Logger;


public class PasswordCallback  implements CallbackHandler{
	private static String MODULE ="PasswordCallback";
	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		setRequestInfo();
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof WSPasswordCallback) {

				WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
				String wsPassword = pc.getPassword();
				String wsUserName = pc.getIdentifier();
				Logger.logInfo(MODULE ,"Received login request for Username: "+wsUserName);
				String passwordType = pc.getPasswordType();
				Logger.logDebug(MODULE ,"Password Type: "+passwordType);
				MDC.put("userName", wsUserName);
				//pc.setPassword(WebServiceManager.getInstance().getPassword());
			} else {
				throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
			}
		}

	}
	private void setRequestInfo(){
		MDC.put("passwordCallback", "true");
	}

}
