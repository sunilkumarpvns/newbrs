package com.elitecore.diameterapi.core.common.session;

/**
 * 
 * @author pulindani
 *
 */
public interface SessionEventListener {

	public boolean removeSession(Session session);

	public void update(Session session);
}
