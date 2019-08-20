package com.elitecore.corenetvertex.sm.serverinstance;

import com.elitecore.corenetvertex.sm.ResourceData;

public class ServerInstanceRegistrationRequest extends ResourceData {

    private String serverName;
    private String serverInstanceId;
    private String serverGroupName;
    private String originHost;
    private String originRealm;
    private String databaseName;
    private String connectionUrl;
    private String userName;
    private String password;
    private String driverClassName;
    private Integer maxIdle;
    private Integer maxTotal;
    private Integer queryTimeout;
    private Integer statusCheckDuration;
    private String jmxPort;
    private String serverHome;
    private String javaHome;
    private String message;
    private int messageCode;


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerInstanceId() {
        return serverInstanceId;
    }

    public void setServerInstanceId(String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }

    public String getServerGroupName() {
        return serverGroupName;
    }

    public void setServerGroupName(String serverGroupName) {
        this.serverGroupName = serverGroupName;
    }

    public String getOriginHost() {
        return originHost;
    }

    public void setOriginHost(String originHost) {
        this.originHost = originHost;
    }

    public String getOriginRealm() {
        return originRealm;
    }

    public void setOriginRealm(String originRealm) {
        this.originRealm = originRealm;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Integer getStatusCheckDuration() {
        return statusCheckDuration;
    }

    public void setStatusCheckDuration(Integer statusCheckDuration) {
        this.statusCheckDuration = statusCheckDuration;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJmxPort() { return jmxPort; }

    public void setJmxPort(String jmxPort) { this.jmxPort = jmxPort; }

    public String getServerHome() { return serverHome; }

    public void setServerHome(String serverHome) { this.serverHome = serverHome; }

    public String getJavaHome() { return javaHome; }

    public void setJavaHome(String javaHome) { this.javaHome = javaHome; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    @Override
    public String getResourceName() {
        return getServerName();
    }


}

