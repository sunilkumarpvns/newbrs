package com.elitecore.elitesm.web.core.system.login;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.elitecore.elitesm.util.logger.Logger;

public class ActiveSessionListener implements HttpSessionListener {
		 
		public static String MODULE = "ActiveSessionListener";
	
		@Override
		public void sessionCreated(HttpSessionEvent event) {}

		@Override
		public void sessionDestroyed(HttpSessionEvent event) {
			Logger.logInfo(MODULE, "Session Destroyed called.");
			try{
				String userName = (String)event.getSession().getAttribute("systemUserName");
				if( userName != null ){
					event.getSession().removeAttribute("userLoggedIn");
					ConcurrentUserAccessDAO.destroySession(userName);
					event.getSession().invalidate();
					Logger.logInfo(MODULE,"Session Destroyed Successfully");
				}
			}catch(Exception  e){
				e.printStackTrace();
			}
		}	
}
