package com.elitecore.corenetvertex.spr;


public class DummyDBDataSource {

    private String dsId;
    private String dsName;
    private String connectionURL;
    private String userName;
    private String password;
    private int minPoolSize;
    private int maxPoolSize;
    private int statusCheckDuration;

    public DummyDBDataSource(String dsId, String dsName, String connectionURL, String userName, String password, int minPoolSize, int maxPoolSize,
	    int statusCheckDuration) {
		this.dsId = dsId;
		this.dsName = dsName;
		this.connectionURL = connectionURL;
		this.userName = userName;
		this.password = password;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.statusCheckDuration = statusCheckDuration;
    }

    public int getStatusCheckDuration() {
	return statusCheckDuration;
    }

    public String getDatasourceID() {
	return dsId;
    }

    public String getConnectionURL() {
	return connectionURL;
    }

    public String getUsername() {
	return userName;
    }

    public String getPassword() {
	return password;
    }

    public String getPlainTextPassword() {
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
	return 3000;
    }

}
