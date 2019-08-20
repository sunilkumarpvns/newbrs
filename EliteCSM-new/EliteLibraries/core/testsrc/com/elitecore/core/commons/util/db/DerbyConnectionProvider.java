package com.elitecore.core.commons.util.db;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.systemx.esix.TaskScheduler;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DerbyConnectionProvider {
	private static final String DERBY_URL = "jdbc:derby:memory:TestingDB;create=true";
	private static final String DATASOURCE_NAME = "FakeDatasource";
	private static final String DATASOURCE_ID = "1";
	private DBConnectionManagerFactory dbConnectionManagerFactory = new DBConnectionManagerFactory();
	private DBConnectionManager derbyConnectionManager;
	private final DBDatasourceDetail dataSource;

	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public DerbyConnectionProvider(TaskScheduler taskScheduler) throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		dataSource = new DBDatasourceDetail();
		dataSource.setConnectionURL(DERBY_URL);
		dataSource.setDataSourceName(DATASOURCE_NAME);
		dataSource.setDatasourceID(DATASOURCE_ID);
		dataSource.setUsername("");
		dataSource.setPassword("");
		derbyConnectionManager = dbConnectionManagerFactory.newInstance(DATASOURCE_NAME);
		derbyConnectionManager.init(dataSource, taskScheduler);
	}
	
	public DBDataSource getDataSource() {
		return dataSource;
	}
	
	public DBConnectionManager getConnectionManager() {
		return derbyConnectionManager;
	}
	
	private class DBDatasourceDetail implements DBDataSource {
		private String dsID;
		private  String dsName;
		private  String connURL ="jdbc:oracle:thin:@ipaddress:port:sid";;
		private  String userName ="admin";
		private int minPoolSize = 2;
		private int maxPoolSize = 5;
		private int statusCheckDuration;
		private String password;
		
		public void setDatasourceID(String dsID) {
			this.dsID = dsID;
		}
		public void setDataSourceName(String dsName) {
			this.dsName = dsName;
		}
		public void setConnectionURL(String connURL) {
			this.connURL = connURL;
		}
		public void setUsername(String userName) {
			this.userName = userName;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
		public int getStatusCheckDuration() {
			return statusCheckDuration;
		}
		
		public String getDatasourceID() {
			return dsID;
		}

		public String getConnectionURL() {
			return connURL;
		}
		public String getUsername() {
			return userName;
		}
		
		public String getPassword() {
			return password;
		}
		
		@Override
		public String getPlainTextPassword(){
			return password;
		}

		public String getDataSourceName() {
			return dsName;
		}
		public int getMinimumPoolSize() {
			return minPoolSize;
		}
		public int getMaximumPoolSize() {
			return maxPoolSize;
		}
		public int getNetworkReadTimeout() {
			return 0;
		}
	}
}