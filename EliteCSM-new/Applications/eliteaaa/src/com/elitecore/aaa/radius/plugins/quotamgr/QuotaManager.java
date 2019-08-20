package com.elitecore.aaa.radius.plugins.quotamgr;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.radius.plugins.quotamgr.QuotaManagerPlugin.RequestSession;


public class QuotaManager {
	
	private Map<String,RequestSession> sessionMap;
	private static QuotaManager manger = null;
	private QuotaManager(){
		sessionMap = new HashMap<String, RequestSession>();	
	}

	public static QuotaManager getInstance(){
		if(manger == null){
			manger = new QuotaManager();
		}
		return manger;
	}
	
	public RequestSession get(String sessionId){
		return sessionMap.get(sessionId);
	}
	public void put(String sessionId,RequestSession session){
		if(session != null)
			sessionMap.put(sessionId,session);
	}
	public RequestSession remove(String sessionId){
		return sessionMap.remove(sessionId);
	}
	
}
