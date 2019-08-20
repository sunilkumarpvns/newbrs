/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.diameterapi.core.common.session.AppSession;

/**
 * @author pulindani
 *
 */
public abstract class DiameterAppSession implements AppSession {
	private String sessionId;
	public DiameterAppSession(String sessionId){
		this.sessionId =sessionId;
	}
	
	public String getSessionId(){
		return sessionId;
	}
}
