package com.elitecore.elitesm.ws;

import java.io.IOException;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.log4j.MDC;
import org.apache.ws.security.WSPasswordCallback;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.ws.logger.Logger;


public class PasswordCallback  implements CallbackHandler{
	private static String MODULE ="PasswordCallback";
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		setRequestInfo();
		Logger.logInfo(MODULE, "handle() function of the PasswordCallback is called.");

		StaffBLManager staffBLManager = new StaffBLManager();
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof WSPasswordCallback) {

				WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];

				String wsPassword = pc.getPassword();
				String wsUserName = pc.getIdentifier();

				
				// set the password given a username
				IStaffData staffData = new StaffData();
				staffData.setUsername(wsUserName);

				
				Logger.logInfo(MODULE ,"Received login request for Username: "+wsUserName);
				String passwordType = pc.getPasswordType();
				Logger.logDebug(MODULE ,"Password Type: "+passwordType);
				List list=null;
				try{
					list = staffBLManager.getList(staffData);
				}catch(DataManagerException e){
					Logger.logTrace(MODULE ," Error in authencating the client user.");
					Logger.logTrace(MODULE ,e);
				}

				if(list == null || list.size() != 1){
					Logger.logError(MODULE,"Multiple or NO entries found for Username: \""+wsUserName+"\"");
					throw new UnsupportedCallbackException(callbacks[i],"Unknown username or invalid password");
				}

				IStaffData user = (IStaffData)list.get(0);
				MDC.put("userName", wsUserName);
				if(passwordType.contains("PasswordText")){
					if(user.getPassword().equals(wsPassword)){
						
						return;
					}
					Logger.logWarn(MODULE, "Invalid Password: \""+wsPassword+"\"");
					throw new UnsupportedCallbackException(callbacks[i],"The security token could not be authenticated or authorized");

				}else{ //if(passwordType.contains("PasswordDigest")){
					pc.setPassword(user.getPassword());	
				//}else{
					//throw new UnsupportedCallbackException(callbacks[i],"Unsupported Authentication Method");
				}

			} else {
				throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
			}
		}

	}
	private void setRequestInfo(){
		MessageContext messageContext = MessageContext.getCurrentContext(); 
		String clientAddress = messageContext.getStrProp(Constants.MC_REMOTE_ADDR); 
		MDC.put("clientAddress", clientAddress);
		MDC.put("passwordCallback", "true");
	}

}
