package com.elitecore.core.serverx.sessionx;

import java.util.Date;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;

public interface SessionFactory {
	Session getSession();
	SessionData createSessionData(String tableName);
	DBDataSource getActiveDataSource();
	boolean isAlive();
	SessionData createSessionData(String coreSessTableName, Date creationTime, Date lastUpdaetTime);
}
