package com.elitecore.nvsmx.commons.model.databaseSetup;

public class DatabaseSetupData {
	private String connectionUrl;
	private String netvertexUsername;
	private String netvertexPassword;
	private int maxIdle;
	private int maxActive;
	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getNetvertexUsername() {
		return netvertexUsername;
	}
	public void setNetvertexUsername(String netvertexUsername) {
		this.netvertexUsername = netvertexUsername;
	}
	public String getNetvertexPassword() {
		return netvertexPassword;
	}
	public void setNetvertexPassword(String netvertexPassword) {
		this.netvertexPassword = netvertexPassword;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
}
