package com.elitecore.elitesm.web.core.system.moduledetails;

public class NestedObjectDetails {
	
	private String key;
	private String moduleName;
	private String moduleInstanceName;
	private String moduleId;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleInstanceName() {
		return moduleInstanceName;
	}
	public void setModuleInstanceName(String moduleInstanceName) {
		this.moduleInstanceName = moduleInstanceName;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("*************NestedObjectDetails*************");
		builder.append("Key                  : "+key);
		builder.append("Module Name          : "+moduleName);
		builder.append("Module Instance Name : "+moduleInstanceName);
		builder.append("Module Id            : "+moduleId);
		return builder.toString();
	}
	
	
}
