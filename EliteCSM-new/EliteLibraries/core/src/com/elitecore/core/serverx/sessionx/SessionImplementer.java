package com.elitecore.core.serverx.sessionx;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;

public interface SessionImplementer {
	Session getSession();
	DBDataSource getActiveDataSource();
	boolean isAlive();
}
