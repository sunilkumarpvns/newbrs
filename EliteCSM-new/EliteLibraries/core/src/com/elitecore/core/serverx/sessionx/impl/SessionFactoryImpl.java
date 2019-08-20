package com.elitecore.core.serverx.sessionx.impl;

import java.util.Date;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SessionImplementer;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.db.impl.DBSessionImpl;

public class SessionFactoryImpl implements SessionFactory {
	private SessionConfiguration sessionConfiguration;
	private SessionImplementer sessionImplementer;
	private ServerContext serverContext;
	
	public SessionFactoryImpl(ServerContext serverContext, SessionConfiguration sessionConfiguration) {
		this.sessionConfiguration = sessionConfiguration;
		this.serverContext = serverContext;
	}
	
	public void init()throws InitializationFailedException{
		switch(sessionConfiguration.getSessionFactoryType()){
		case SessionConfiguration.DB_SESSION:
		case SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE:
			DBSessionImpl dbSessionImplementor = new DBSessionImpl(serverContext,sessionConfiguration);
			dbSessionImplementor.init();
			sessionImplementer = dbSessionImplementor;
			break;
		case SessionConfiguration.IN_MEMORY_SESSION:
			break;
		}
	}

	@Override
	public Session getSession() {
		if(sessionImplementer == null)
			return null;
		return sessionImplementer.getSession();
	}

	@Override
	public SessionData createSessionData(String schemaName) {
		return new SessionDataImpl(schemaName);
	}

	@Override
	public DBDataSource getActiveDataSource() {
		return sessionImplementer.getActiveDataSource();
	}

	@Override
	public boolean isAlive() {
		return sessionImplementer.isAlive();
	}

	@Override
	public SessionData createSessionData(String coreSessTableName, Date creationTime, Date lastUpdaetTime) {
		
		return new SessionDataImpl(coreSessTableName, creationTime, lastUpdaetTime);
	}
}
