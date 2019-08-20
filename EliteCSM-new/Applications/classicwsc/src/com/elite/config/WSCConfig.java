package com.elite.config;

public class WSCConfig {
	private String ssss_path = "";
	private String auth_type = "";

	public String getAuth_type() {
		return auth_type;
	}

	public void setAuth_type(String auth_type) {
		this.auth_type = auth_type;
	}

	public String getSsss_path() {
		return ssss_path;
	}

	public void setSsss_path(String ssss_path) {
		this.ssss_path = ssss_path;
	}
	@Override
	public String toString() {
		String result = "===============WSCConfig=================";
		result = result +"\r\n ssss_path = "+ ssss_path;
		result = result +"\r\n auth_type = "+ auth_type;
		result = result +"\r\n====================================";
		return result;
	}
}
