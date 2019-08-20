package com.elitecore.nvsmx.system.db;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

import java.io.PrintWriter;
import java.io.StringWriter;


public class DBDatasourceImpl implements DBDataSource, com.elitecore.corenetvertex.core.db.DBDataSource {
	private String datasourceID;
	private String dataSourceName;
	private String connectionURL;
	private String driverClass;
	private String username;
	private String password;
	private int minimumPoolSize = 5;
	private int maximumPoolSize = 70;
	private int statusCheckDuration = 120;
	private int networkReadTimeout = 10*1000;
	private int timeout;

	public DBDatasourceImpl(String datasourceName, String connectionUrl, String username,
			String password) {
		this.dataSourceName = datasourceName;
		this.connectionURL = connectionUrl;
		this.username = username;
		this.password = password;
	}

	public DBDatasourceImpl(String dataSourceName, String connectionUrl, String username,
			String password, int minPoolSize, int maxPoolSize) {
		this.dataSourceName = dataSourceName;
		this.connectionURL = connectionUrl;
		this.username = username;
		this.password = password;
		this.minimumPoolSize = minPoolSize;
		this.maximumPoolSize = maxPoolSize;
	}

	public void setDatasourceID(String datasourceID) {
		this.datasourceID = datasourceID;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMinimumPoolSize(int minimumPoolSize) {
		this.minimumPoolSize = minimumPoolSize;
	}
	
	public void setMaximumPoolSize(int maxPoolSize) {
		this.maximumPoolSize = maxPoolSize;
	}

	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}

	public void setNetworkReadTimeout(int networkReadTimeout) {
		this.networkReadTimeout = networkReadTimeout;
	}

	@Override
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}

	@Override
	public String getDatasourceID() {
		return datasourceID;
	}

	@Override
	public String getConnectionURL() {
		return connectionURL;
	}

	public String getDriverClass() {
		return driverClass;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public String getPlainTextPassword() {
		return password;
	}

	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}

	@Override
	public int getMinimumPoolSize() {
		return minimumPoolSize;
	}

	@Override
	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	@Override
	public int getNetworkReadTimeout() {
		return networkReadTimeout;
	}

	@Override
	public int getTimeout() { return timeout; }

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {StringWriter stringBuffer = new StringWriter();
	PrintWriter out = new PrintWriter(stringBuffer);
	out.println();
	out.println("    Datasource Name = " + dataSourceName);
	out.println("    Connection URL = " + connectionURL);
	out.println("    User Name = " + username);
	out.println("    Password = *****");
	out.println("    Minimum Pool Size = " + minimumPoolSize);
	out.println("    Maximum Pool Size = " + maximumPoolSize);
	out.println("    Status Check Duration = " + statusCheckDuration);
	out.println("    Network Read Timeout = " + networkReadTimeout);
	out.close();
	return stringBuffer.toString();
	}


	public static DBDatasourceImpl create(DatabaseData databaseData, FailReason failReason) {

		String decryptedPassword = null;
		try {
			decryptedPassword = PasswordUtility.getDecryptedPassword(databaseData.getPassword());
		} catch (DecryptionNotSupportedException | DecryptionFailedException | NoSuchEncryptionException e ) {
			failReason.add(e.getMessage());
			LogManager.ignoreTrace(e);
		}

		if(failReason.isEmpty() == false) {
			return null;
		}
		return new DBDatasourceImpl(databaseData.getName(),
				databaseData.getConnectionUrl(),
				databaseData.getUserName(),
				decryptedPassword);
	}
}
