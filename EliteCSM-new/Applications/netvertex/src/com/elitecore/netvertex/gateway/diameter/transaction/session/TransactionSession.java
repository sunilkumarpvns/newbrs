package com.elitecore.netvertex.gateway.diameter.transaction.session;

import java.util.HashMap;
import java.util.Map;

public class TransactionSession {

	private Map<String, Object>map;
	
	public TransactionSession(){
		map = new HashMap<String, Object>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		return (T) map.get(key);
	}
	
	public void put(String key,Object obj){
		map.put(key, obj);
	}
}
