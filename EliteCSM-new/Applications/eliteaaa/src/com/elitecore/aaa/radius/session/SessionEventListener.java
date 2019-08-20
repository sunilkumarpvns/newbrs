package com.elitecore.aaa.radius.session;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface SessionEventListener {

	public boolean removeSession(ISession session);

	public void update(ISession session);
}
