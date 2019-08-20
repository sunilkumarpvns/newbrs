package com.elitecore.netvertex.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.netvertex.core.conf.DatabaseDSConfiguration;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class DatabaseDSConfigurationImpl extends BaseConfigurationImpl implements DatabaseDSConfiguration {

	private static final String MODULE = "DB-DSCNF";
	private final SessionFactory sessionFactory;

	private Map<String, DBDataSourceImpl> datasourceMap;
	private Map<String,DBDataSourceImpl> datasourceNameMap;

	public DatabaseDSConfigurationImpl(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.sessionFactory = sessionFactory;
		datasourceMap= new HashMap<>();
		datasourceNameMap = new HashMap<>();
	}
	
	
	@Override
	public DBDataSource getDatasource(String dsID) {
		return datasourceMap.get(dsID);
	}

	@Override
	public Map<String, DBDataSourceImpl> getDatasourceMap() {		
		return datasourceMap;
	}


	@Override
	public void reloadConfiguration() throws LoadConfigurationException {
		LogManager.getLogger().info(MODULE,"Reload configuration for DB datasource started");

		Map<String, DBDataSourceImpl> tempDatasourceMap = new HashMap<String, DBDataSourceImpl>(this.datasourceMap);
		Map<String, DBDataSourceImpl> tempDatasourceNameMap = new HashMap<String, DBDataSourceImpl>(this.datasourceNameMap);


		Session session = null;

		try{

			session = sessionFactory.openSession();
			List<DatabaseData> databaseDatas = HibernateReader.readAll(DatabaseData.class, session);

			for (DatabaseData databaseData : databaseDatas) {
				DBDataSourceImpl dbDataSource = createDBDataSourceImpls(databaseData);
				tempDatasourceMap.put(databaseData.getId(), dbDataSource);
				tempDatasourceNameMap.put(databaseData.getName(), dbDataSource);
			}

			this.datasourceMap = tempDatasourceMap;
			this.datasourceNameMap = tempDatasourceNameMap;

			ConfigLogger.getInstance().info(MODULE, this.toString());
			LogManager.getLogger().info(MODULE,"Reload configuration for DB datasource successfully completed");
		} catch (Exception ex) {
			throw new LoadConfigurationException("Error while reloading DB datasource. Reason: " + ex.getMessage(), ex);
		} finally {
			closeQuietly(session);
		}
	}

	@Override
	/**
	 * Read the configuration information from the DB
	 */
	public void readConfiguration() throws LoadConfigurationException {
		reloadConfiguration();
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- DB Datasource Configuration -- ");
		if (datasourceMap == null || datasourceMap.isEmpty()) {
			out.println("      No configuration found");
		} else {
			for(Entry<String,DBDataSourceImpl> entry:datasourceMap.entrySet()){
				out.println(entry.getValue());
				out.println();
			}
		}
		out.close();
		return stringBuffer.toString();
	}


	@Override
	public DBDataSource getDatasourceByName(String datasourceName) {
		return datasourceNameMap.get(datasourceName);
	}


	@Override
	public Map<String, DBDataSourceImpl> getDatasourceNameMap() {
		return this.datasourceNameMap;
	}

	private DBDataSourceImpl createDBDataSourceImpls(DatabaseData databaseData) {
        FailReason failReason = new FailReason("DB DS");
		return DBDataSourceImpl.create(databaseData, failReason);
    }


}
