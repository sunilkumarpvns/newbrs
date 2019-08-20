package com.elitecore.coreeap.session.method.tls;

import java.util.HashMap;

import com.elitecore.coreeap.session.BaseEAPMethodSession;

public class TLSSessionState extends BaseEAPMethodSession {
 
	private HashMap<String, Object> parameterMap = new HashMap<String, Object>(3);
	
	public TLSSessionState(){ 
		parameterMap.put(TLS_CONNECTION_STATE,new TLSConnectionState());
		parameterMap.put(CHANGE_CIPHER_SPEC_RECEIVED,false);
		parameterMap.put(ALERT_GENERATED,false);
	}
	
	public Object getParameterValue(String parameterKey)
	{	
		return(parameterMap.get(parameterKey));
	}
	
	public void setParameterValue(String parameterKey,Object parameterValue)
	{
		parameterMap.put(parameterKey,parameterValue);
	}
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[ TLSSession : ");
		strBuilder.append(" ]");
		return(strBuilder.toString());
	}
	
	public Object clone() throws CloneNotSupportedException {
//		TLSSessionState sessionState = new TLSSessionState();
		TLSSessionState sessionState = null;
	
		sessionState = (TLSSessionState)super.clone();
		sessionState.parameterMap = new HashMap<String, Object>(3);
		sessionState.setParameterValue(TLS_CONNECTION_STATE,new TLSConnectionState());
		sessionState.setParameterValue(CHANGE_CIPHER_SPEC_RECEIVED,false);
	
		return sessionState;
	}
}

