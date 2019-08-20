package com.elitecore.corenetvertex.sm.serverinstance;

import java.io.Serializable;

public class ConfigurationDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String url;
    private String driverClassName;
    private int maxTotal;
    private int maxIdle;
    private int validationQueryTimeout;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public static ConfigurationDatabase from(ServerInstanceRegistrationRequest serverInstanceRegistrationRequest){
        ConfigurationDatabase configurationDB = new ConfigurationDatabase();
        configurationDB.setDriverClassName(serverInstanceRegistrationRequest.getDriverClassName());
        configurationDB.setPassword(serverInstanceRegistrationRequest.getPassword());
        configurationDB.setUrl(serverInstanceRegistrationRequest.getConnectionUrl());
        configurationDB.setUsername(serverInstanceRegistrationRequest.getUserName());
        configurationDB.setMaxTotal(serverInstanceRegistrationRequest.getMaxTotal());
        configurationDB.setMaxIdle(serverInstanceRegistrationRequest.getMaxIdle());
        configurationDB.setValidationQueryTimeout(serverInstanceRegistrationRequest.getQueryTimeout());
        return configurationDB;
    }

}
