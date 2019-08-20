package com.elitecore.core.serverx.sessionx.db.impl;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;

/**
 * 
 * @author malav.desai
 *
 */
interface DeleteOperation {
	int execute(SessionData sessionData);
	int execute(Criteria criteria);
}
