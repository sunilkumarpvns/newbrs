package com.elitecore.netvertex.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlElement;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.passwordutil.PasswordEncryption;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class DBDataSourceImpl implements DBDataSource, com.elitecore.corenetvertex.core.db.DBDataSource {

    private static final String MODULE = "DB-Datasource-Impl";

	private String dsID;
	private String dsName;
	private String connURL = "jdbc:oracle:thin:@ipaddress:port:sid";
	private String userName = "admin";
	private String password = "admin"; //NOSONAR - Reason: Credentials should not be hard-coded
	private int timeout;
	private int minPoolSize = 2;
	private int maxPoolSize = 5;
	private int statusCheckDuration;
	public static final int DEFAULT_STATUS_CHECK_DURATION = 120;
	private int networkReadTimeoutInMs = EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue;

	public DBDataSourceImpl() {
		// required by Jaxb.
	}

	public DBDataSourceImpl(String dsID, String dsName, String connURL,
			String userName, String password, int minPoolSize, int maxPoolSize,
			int statusCheckDuration, int networkReadTimeout, int timeout) {
		this.dsID = dsID;
		this.dsName = dsName;
		this.connURL = connURL;
		this.userName = userName;
		this.password = password;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.statusCheckDuration = statusCheckDuration;
		this.networkReadTimeoutInMs = networkReadTimeout;
		this.timeout = timeout;
	}

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

	public void setMinimumPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public void setMaximumPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
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
	public String getPlainTextPassword() {

		try {
			return PasswordEncryption.getInstance().decrypt(password, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		} catch (Exception e) { //NOSONAR
			//this should not occur in normal scenarios, so returning th
			return password;
		}
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

	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}

	@XmlElement(name = "network-read-timeout", type = int.class, defaultValue = "3000")
	public int getNetworkReadTimeout() {
		return networkReadTimeoutInMs;
	}

	public void setNetworkReadTimeout(int networkReadTimeout) {
		this.networkReadTimeoutInMs = networkReadTimeout;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    Datasource Id = " + dsID);
		out.println("    Datasource Name = " + dsName);
		out.println("    Connection URL = " + connURL);
		out.println("    User Name = " + userName);
		out.println("    Password = *****");
		out.println("    Timeout = " + timeout + " ms");
		out.println("    Minimum Pool Size = " + minPoolSize);
		out.println("    Maximum Pool Size = " + maxPoolSize);
		out.println("    Status Check Duration = " + statusCheckDuration
				+ " sec");
		out.println("    Network Read Timeout = " + networkReadTimeoutInMs);
		out.close();
		return stringBuffer.toString();
	}

	public int getTimeout() {
		return timeout;
	}

	public static DBDataSourceImpl create(DatabaseData databaseData, FailReason failReason) {

        if(failReason.isEmpty() == false) {
            return null;
        }

        String userName = databaseData.getUserName();
        if (userName == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Username is not specified in " + databaseData.getName() + ".");
            }
            userName = "";
        }

        String password = databaseData.getPassword();
        if (password == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "password is not specified in " + databaseData.getName() + ".");
            }
            password = "";
        }

        int timeout = (int) (TimeUnit.MILLISECONDS.toSeconds(databaseData.getQueryTimeout().intValue()));
        if (timeout < 1 || timeout > 10) {
            getLogger().warn(MODULE, "Considering default value " + CommonConstants.QUERY_TIMEOUT_SEC + " for timeout for DB datasource(" + databaseData.getName() + "). Reason: Invalid configured value " + timeout);
            timeout = CommonConstants.QUERY_TIMEOUT_SEC;
        }

        int minPoolSize = databaseData.getMinimumPool().intValue();
        int maxPoolSize = databaseData.getMaximumPool().intValue();
        if (minPoolSize > maxPoolSize) {
            getLogger().warn(MODULE, "Considering minimum pool size: " + maxPoolSize + ". Reason: Minimum pool size(" + minPoolSize + ") cannot be greater than maximum pool size(" + maxPoolSize + ")");
            minPoolSize = maxPoolSize;
        }

        int statusCheckDuration = 0;
        if (databaseData.getStatusCheckDuration() != null) {
            statusCheckDuration = databaseData.getStatusCheckDuration().intValue();
        }

        return new DBDataSourceImpl(databaseData.getId(),
                databaseData.getName(),
                databaseData.getConnectionUrl(),
                userName,
                password,
                minPoolSize,
                maxPoolSize,
                statusCheckDuration,
                EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue,
                timeout);
	}
}
