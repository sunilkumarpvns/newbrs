package com.elitecore.core.serverx.sessionx.db.impl;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;

interface UpdateOperation {
	int execute(SessionData sessionData);
	int execute(SessionData sessionData,Criteria criteria);
}
