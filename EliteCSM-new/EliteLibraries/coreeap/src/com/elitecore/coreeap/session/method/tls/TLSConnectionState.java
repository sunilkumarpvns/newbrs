package com.elitecore.coreeap.session.method.tls;

import java.util.HashMap;

import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.session.IEAPMethodSession;

public class TLSConnectionState {
 
	//constants used as Parameter Key for the TLSConnectionState
	private final static String WAIT_FOR_NEXT_FRAGMENT = "WAIT_FOR_NEXT_FRAGMENT";
	
	/**
	 * SESSION_ID_LENGTH
	 * Type : int
	 * Defualt Value : SESSION_ID_DEFUALT_LENGTH 
	 */
	public final static String SESSION_ID_LENGTH = "SESSION_ID_LENGTH"; //int
	public final static String IS_SESSION_RESUMED= "IS_SESSION_RESUMED"; //boolean
	public final static String SESSION_RESUMPTION_COUNTER="SESSION_RESUMPTION_COUNTER"; // int value which will indcates the number of times the session can be resumed.
	public final static String MACADDRESS="MACADDRESS";
	/**
	 * SESSION_ID_DEFUALT_LENGTH
	 * Type : int
	 * Value : 32
	 */
	public final static int SESSION_ID_DEFUALT_LENGTH = 32;
	private int identifier;
	private HashMap<String, Object> parameterMap = new HashMap<String, Object>(4);
	public TLSConnectionState(){
		setParameterValue(WAIT_FOR_NEXT_FRAGMENT,false);
		setParameterValue(SESSION_ID_LENGTH,SESSION_ID_DEFUALT_LENGTH);
		setParameterValue(IS_SESSION_RESUMED,false);
		setParameterValue(SESSION_RESUMPTION_COUNTER,0);
	}
	public Object getParameterValue(String parameterKey)
	{
		return(parameterMap.get(parameterKey));
	}
	public void setParameterValue(String parameterKey,Object parameterValue)
	{
		if(parameterMap.get(parameterKey)!=null)
		{
			parameterMap.remove(parameterKey);
		}
		parameterMap.put(parameterKey,parameterValue);
	}
	public int getIdentifier() {
		return identifier;
	}
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	public void reset(){
		TLSSecurityKeys tlsSecurityKeys=(TLSSecurityKeys)parameterMap.get(IEAPMethodSession.TLS_SECURITY_KEY);
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)parameterMap.get(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		setParameterValue(TLSConnectionState.SESSION_RESUMPTION_COUNTER, 0);
		setParameterValue(WAIT_FOR_NEXT_FRAGMENT,false);
		tlsSecurityKeys.reset();
		tlsSecurityParameters.reset();
	}
}
