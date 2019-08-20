package com.elitecore.core.serverx.sessionx.db.impl;

import com.elitecore.core.serverx.sessionx.SessionData;

/**
 * 
 * @author malav.desai
 *
 */
interface SaveOperation {
	int execute(SessionData sessionData);
}
