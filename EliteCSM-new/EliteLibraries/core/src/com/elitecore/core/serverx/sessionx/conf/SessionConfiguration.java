package com.elitecore.core.serverx.sessionx.conf;

import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl.SQLDialectFactory;

public interface SessionConfiguration {
	int IN_MEMORY_SESSION=0;
	int DB_SESSION=1;
	int DB_SESSION_WITH_BATCH_UPDATE = 2;
	
	SessionFactory createSessionFactory() throws InitializationFailedException;
	
	public SQLDialectFactory getDialectFactory();
	
	int getSessionFactoryType();
	List<DBDataSource> getDataSources();
	boolean isShowSQL();
	
	List<SchemaMapping> getSchemaList();
	public long getMaxBatchSize();
	public long getBatchUpdateInterval();
	
	public int getHighQueryResponseTime();
	public int getInterval();
	
	SystemPropertiesProvider getSystemPropertiesProvider();
	
	boolean isSaveBatched();
	boolean isUpdateBatched();
	boolean isDeleteBatched();
}
