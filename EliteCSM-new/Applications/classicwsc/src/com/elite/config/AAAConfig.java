package com.elite.config;

public class AAAConfig {
	private String ip  = null;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getShared_secret() {
		return shared_secret;
	}
	public void setShared_secret(String shared_secret) {
		this.shared_secret = shared_secret;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	private int port = 0;
	private String shared_secret = null;
	private int timeout = 0;
	@Override
	public String toString() {
		String result = "===============AAAConfig=================";
		result = result +"\r\n ip = "+ ip;
		result = result +"\r\n port = "+ port;
		result = result +"\r\n shared_secret = "+ shared_secret;
		result = result +"\r\n timeout = "+ timeout;
		result = result +"\r\n====================================";
		return result;
	}
}
