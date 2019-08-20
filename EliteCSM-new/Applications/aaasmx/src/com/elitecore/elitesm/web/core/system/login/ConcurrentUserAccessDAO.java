package com.elitecore.elitesm.web.core.system.login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class ConcurrentUserAccessDAO {
	private static final String MODULE = "ConcurrentUserAccessDAO" ;
	
	static ConcurrentHashMap<String, Long> concurrentUsersMap =new ConcurrentHashMap<String, Long>();

	public static boolean getActiveSession(String systemUserName, HttpServletRequest request) {
		Logger.logInfo(MODULE, "Enter getActiveSession method of ConcurrentUserAccessDAO ");
		
		Long concurrentLoginLimit = Long.parseLong(ConfigManager.get(ConfigConstant.CONCURRENT_LOGIN_LIMIT));
		Logger.logInfo(MODULE, "Current Login limit set to - " + concurrentLoginLimit);
		boolean allowUsertoAccessSM = true;
		
		if( concurrentUsersMap != null && concurrentUsersMap.size() > 0){
			Long isUserIsActive = concurrentUsersMap.get(systemUserName);
			
			if( isUserIsActive != null && isUserIsActive > 0 ){
				Long currentLimit = concurrentUsersMap.get(systemUserName);
				if( currentLimit < concurrentLoginLimit ){
					currentLimit++;
					concurrentUsersMap.put(systemUserName, currentLimit);
					allowUsertoAccessSM = true;
				}else{
					allowUsertoAccessSM = false;
				}
			}else{
				Long initialAttempt = 1L;
				concurrentUsersMap.put(systemUserName, initialAttempt);
				allowUsertoAccessSM = true;
			}
		}else{
			Long initialAttempt = 1L;
			concurrentUsersMap.put(systemUserName, initialAttempt);
			allowUsertoAccessSM = true;
		}
		
		Logger.logInfo(MODULE, "Current Active Session of ELITEAAA-SM :");
		if( concurrentUsersMap != null ){
			for (Map.Entry<String, Long> entry : concurrentUsersMap.entrySet()) {
			    Logger.logInfo(MODULE, entry.getKey() + "-" + entry.getValue());
			}	
		}
		
		return allowUsertoAccessSM;
	}
	
	public static void destroySession(String systemUserName) {
		Logger.logInfo(MODULE, "Enter destroySession method of ConcurrentUserAccessDAO ");
		Long currentLoginInMap = concurrentUsersMap.get(systemUserName);
		
		if( currentLoginInMap != null ){
			if( currentLoginInMap <= 1){
				concurrentUsersMap.remove(systemUserName);
				Logger.logInfo(MODULE, "Successfully removed session from concurrent users map - " + systemUserName);
			}else{
				currentLoginInMap--;
				concurrentUsersMap.put(systemUserName, currentLoginInMap);
			}
		}
		
		Logger.logInfo(MODULE, "Current Active Session of ELITEAAA-SM :");
		if( concurrentUsersMap != null ){
			for (Map.Entry<String, Long> entry : concurrentUsersMap.entrySet()) {
				 Logger.logInfo(MODULE, entry.getKey() + "-" + entry.getValue());
			}	
		}
	}

	public static void destroyAllSession(String systemUserName) {
		Logger.logInfo(MODULE, "Enter destroySession method of ConcurrentUserAccessDAO ");
		Long currentLoginInMap = concurrentUsersMap.get(systemUserName);
		
		if( currentLoginInMap != null ){
			concurrentUsersMap.remove(systemUserName);
		}
		Logger.logInfo(MODULE, "Successfully removed all sessions from map");
	}
}
