package com.elitecore.aaa.core.eap.session;

import java.util.ArrayList;
import java.util.Map;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.fsm.eap.EapStateMachine;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;

public class EAPSessionManager {
	private static final String MODULE = "EAP Session Manager";
	private Map<String, IEapStateMachine> eapStateMachineMap = new ConcurrentHashMap<String, IEapStateMachine>(1000);		
	
	public IEapStateMachine getEAPStateMachine(String eapSessionId,IEapConfigurationContext confContext){
		IEapStateMachine eapStateMachine = null;
		if(eapStateMachineMap.containsKey(eapSessionId)){
			synchronized (eapStateMachineMap) {
				eapStateMachine = (IEapStateMachine)eapStateMachineMap.get(eapSessionId);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "New Eap State Machine Created For Session Id : " + eapSessionId);
			eapStateMachine = new EapStateMachine(confContext);
			synchronized (eapStateMachineMap) {
				eapStateMachineMap.put(eapSessionId, eapStateMachine);
			}
		}		
		return eapStateMachine;
	}

	public IEapStateMachine getEAPStateMachine(String eapSessionId){				
		return (IEapStateMachine)eapStateMachineMap.get(eapSessionId);
	}

	public boolean isSessionExist(String eapSessionId){
		return eapStateMachineMap.containsKey(eapSessionId);		
	}
	
	public Map<String, IEapStateMachine> getEapSessions(){
		return this.eapStateMachineMap;
	}
	
	public IEapStateMachine removeEAPSession(String eapSessionId){
		try {
			synchronized (eapStateMachineMap) {
				return eapStateMachineMap.remove(eapSessionId);
			} 
		} catch (Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem while removing eap session for " + eapSessionId);							
			LogManager.getLogger().trace(MODULE, e);	
		}
		return null;
	}
	
	public void removeEAPSession(ArrayList<String> eapSessionIds){
		for(int i = 0 ; i < eapSessionIds.size() ; i ++){
			try{
				synchronized (eapStateMachineMap) {
					eapStateMachineMap.remove(eapSessionIds.get(i));
				}
			}catch(Exception e){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Problem while removing eap session for " + eapSessionIds.get(i));							
				LogManager.getLogger().trace(MODULE, e);		
			}
		}
	}
	
	public void addEAPSession(IEapStateMachine stateMachine, String... sessionId){
		if (sessionId != null) {
			for (int i=0 ; i<sessionId.length ; i++){
				synchronized (eapStateMachineMap) {
					eapStateMachineMap.put(sessionId[i], stateMachine);
				}
			}
		
		}
	}
}
