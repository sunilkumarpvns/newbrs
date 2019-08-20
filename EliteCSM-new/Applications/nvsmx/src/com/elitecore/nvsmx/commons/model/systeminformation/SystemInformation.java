package com.elitecore.nvsmx.commons.model.systeminformation;


public class SystemInformation {
	
	private String id;
	private String version;
	private String operatingSystem;
	private String apacheTomcat;
	private String java;
	private String ram;
	private String contextPath;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getApacheTomcat() {
		return apacheTomcat;
	}

	public void setApacheTomcat(String apacheTomcat) {
		this.apacheTomcat = apacheTomcat;
	}

	public String getJava() {
		return java;
	}

	public void setJava(String java) {
		this.java = java;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContextPath() {
	    return contextPath;
	}

	public void setContextPath(String contextPath) {
	    this.contextPath = contextPath;
	}
	
}
