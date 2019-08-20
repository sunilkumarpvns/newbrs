package com.elitecore.core.serverx.sessionx.conf.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.db.impl.MySQLDialectImpl;
import com.elitecore.core.serverx.sessionx.db.impl.OracleDialectImpl;
import com.elitecore.core.serverx.sessionx.db.impl.PostgresDialectImpl;
import com.elitecore.core.serverx.sessionx.impl.SessionFactoryImpl;

public class SessionConfigurationImpl implements SessionConfiguration {
	private int sessionFactoryType;
	private long maxBatchSize = 1000;
	private long batchUpdateInterval = 5;	
	private int highQueryResponseTimeMS = 10;
	private boolean showSQL;
	private ServerContext serverContext;
	private List<SchemaMapping> schemaList;
	private List<DBDataSource> dbDataSources;
	private int interval;
	private SQLDialectFactory dialectFactory;
	private boolean saveOperation = false;
	private boolean updateOperation = false;
	private boolean deleteOperation = false;
	private SystemPropertiesProvider systemPropertiesProvider = new SystemPropertiesProvider() {
		
		@Override
		public boolean isNoWaitEnabled() {
			return true;
		}
		
		@Override
		public boolean isBatchEnabled() {
			return true;
		}

		@Override
		public int getQueryTimeout() {
			return CommonConstants.NO_QUERY_TIMEOUT;
		}

		@Override
		public int getBatchQueryTimeout() {
			return 2;
		}
	};
	
	public SQLDialectFactory getDialectFactory() {
		return dialectFactory;
	}

	public SessionConfigurationImpl(ServerContext serverContext){
		this.serverContext = serverContext;
		this.schemaList = new ArrayList<SchemaMapping>();
		this.dbDataSources = new ArrayList<DBDataSource>();
		this.dialectFactory = new DefaultDialectFactory();
	}
	
	public SessionConfigurationImpl(ServerContext serverContext, SQLDialectFactory sqlDialectFactory){
		this(serverContext);
		this.dialectFactory  = sqlDialectFactory;
	}
	
	
	@Override
	public SessionFactory createSessionFactory()throws InitializationFailedException {
		SessionFactoryImpl sessionFactory = null;
		sessionFactory = new SessionFactoryImpl(serverContext,this);
		sessionFactory.init();
		return sessionFactory;
	}

	@Override
	public int getSessionFactoryType() {
		return sessionFactoryType;
	}
	public void setSessionFactoryType(int sessionFactoryType) {
		this.sessionFactoryType = sessionFactoryType;
	}
	
	@Override
	public boolean isShowSQL() {
		return showSQL;
	}
	public void setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
	}

	@Override
	public List<SchemaMapping> getSchemaList() {
		return schemaList;
	}
	public void setSchemaList(List<SchemaMapping> schemaList) {
		this.schemaList = schemaList;
	}
	
	public void addSchema(SchemaMapping schema) {
		this.schemaList.add(schema);
	}
	
	public long getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(long maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public long getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	public void setBatchUpdateInterval(long batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}

	/**
	 * setInterval set interval for Data Source status scanner interval duration
	 * @param intervalSecForDSStatusScanner
	 */
	public void setInterval(int intervalSecForDSStatusScanner) {
			this.interval = intervalSecForDSStatusScanner;
	}

	/**
	 * setInterval set interval for Data Source status scanner interval duration
	 * @param interval
	 */
	@Override
	public int getInterval() {
		return interval;
	}

	@Override
	public List<DBDataSource> getDataSources() {
		return dbDataSources;
	}
	
	public void setDataSources(List<DBDataSource> datasources){
		if(datasources != null)
			this.dbDataSources = datasources;
	}
	
	public void addDataSource(DBDataSource datasource){
		if(datasource != null)
			this.dbDataSources.add(datasource);
	}

	@Override
	public int getHighQueryResponseTime() {
		return highQueryResponseTimeMS;
	}

	public void setHighQueryResponseTime(int highQueryResponseTimeMS) {
		this.highQueryResponseTimeMS = highQueryResponseTimeMS;
		
	}

	public static class DefaultDialectFactory implements SQLDialectFactory {
		public SQLDialect newDialect(List<SchemaMapping> schemaMappingList, DBVendors dbVendors) {
			switch (dbVendors) {
			case MYSQL:
				return new MySQLDialectImpl(schemaMappingList);
			case POSTGRESQL:
				return new PostgresDialectImpl(schemaMappingList);
			case ORACLE:
			default:
				return new OracleDialectImpl(schemaMappingList);
			}
		}
	}
	
	public static interface SQLDialectFactory {
		public SQLDialect newDialect(List<SchemaMapping> schemaMappingList, DBVendors dbVendors);
	}
	
	@Override
	public boolean isSaveBatched() {
		return saveOperation;
	}
	public void setSaveBatched(boolean saveOperation) {
		this.saveOperation = saveOperation;
	}
	
	@Override
	public boolean isUpdateBatched() {
		return updateOperation;
	}
	public void setUpdateBatched(boolean updateOperation) {
		this.updateOperation = updateOperation;
	}
	
	@Override
	public boolean isDeleteBatched() {
		return deleteOperation;
	}
	public void setDeleteBatched(boolean deleteOperation) {
		this.deleteOperation = deleteOperation;
	}

	@Override
	public SystemPropertiesProvider getSystemPropertiesProvider() {
		return systemPropertiesProvider;
	}

	public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
		this.systemPropertiesProvider = systemPropertiesProvider;
	}
}
