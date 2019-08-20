package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.data.ScriptData;

import java.util.List;
import java.util.Map;


public class NetvertexServerInstanceConfigurationImpl implements ToStringable{

	private String name;
    private String logLevel;

    private RollingType rollingType;
    private String rollingUnit;
    private int maxRollingUnit;
    private boolean compRollingUnit;
    private String snmpAddress;
    private int snmpPort;
    private int httpPort;
	private String restIpAddress;
	private int restPort;
	private Map<String, Boolean> configuredServiceList;
	private List<ScriptData> scriptDatas;

	public NetvertexServerInstanceConfigurationImpl(String name,
													String logLevel,
													RollingType rollingType,
													String rollingUnit,
													int maxRollingUnit,
													boolean compRollingUnit,
													String snmpAddress,
													int snmpPort,
													int httpPort,
													String restIpAddress,
													int restPort,
													Map<String, Boolean> configuredServiceList,
													List<ScriptData> scriptDatas) {
		
		super();
		this.name = name;
		this.logLevel = logLevel;
		this.rollingType = rollingType;
		this.rollingUnit = rollingUnit;
		this.maxRollingUnit = maxRollingUnit;
		this.compRollingUnit = compRollingUnit;
		this.snmpAddress = snmpAddress;
		this.snmpPort = snmpPort;
		this.httpPort = httpPort;
		this.restIpAddress = restIpAddress;
		this.restPort = restPort;
		this.configuredServiceList = configuredServiceList;
		this.scriptDatas = scriptDatas;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public RollingType getRollingType() {
		return rollingType;
	}

	public String getRollingUnit() {
		return rollingUnit;
	}

	public int getMaxRollingUnit() {
		return maxRollingUnit;
	}

	public boolean isCompRollingUnit() {
		return compRollingUnit;
	}

	public String getSnmpAddress() {
		return snmpAddress;
	}

	public int getSnmpPort() {
		return snmpPort;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public Map<String, Boolean> getConfiguredServiceList() {
		return configuredServiceList;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public void setRollingType(RollingType rollingType) {
		this.rollingType = rollingType;
	}

	public void setRollingUnit(String rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	public void setCompRollingUnit(boolean compRollingUnit) {
		this.compRollingUnit = compRollingUnit;
	}


	public String getName() {
		return name;
	}

	public String getRestIpAddress() {
		return restIpAddress;
	}

	public int getRestPort() {
		return restPort;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMaxRollingUnit(int maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}

	public void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}

	public void setSnmpPort(int snmpPort) {
		this.snmpPort = snmpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public void setRestIpAddress(String restIpAddress) {
		this.restIpAddress = restIpAddress;
	}

	public void setRestPort(int restPort) {
		this.restPort = restPort;
	}

	public void setConfiguredServiceList(Map<String, Boolean> configuredServiceList) {
		this.configuredServiceList = configuredServiceList;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Server instance Configuration-- ");
		toString(builder);
		return builder.toString();
	}



	@Override
	public void toString(IndentingToStringBuilder builder) {
        builder.append("Name", name);
        builder.append("Log Level", logLevel);
        builder.append("Rolling Type", rollingType.label);
        builder.append("Max Rolling Unit", maxRollingUnit + " " + rollingUnit);
        builder.append("Compress Log File", compRollingUnit);
        builder.append("SNMP Address", snmpAddress + ":" + snmpPort);
        builder.append("HTTP Port", httpPort);
        builder.append("REST Address", restIpAddress + ":" + restPort);
        if (Maps.isNullOrEmpty(configuredServiceList) == false) {
            configuredServiceList.forEach((s, enabled) -> builder.append(s, enabled ? "Enabled" : "Disabled"));
        }
		if (Collectionz.isNullOrEmpty(scriptDatas) == false) {
			scriptDatas.forEach(scriptData -> {
				builder.append("Groovy Script Name", scriptData.getScriptName());
				builder.append("Groovy Script Arguemnt", scriptData.getScriptArgumet());
			});
		}
	}

	public List<ScriptData> getScriptDatas() {
		return scriptDatas;
	}

	public void setScriptDatas(List<ScriptData> scriptDatas) {
		this.scriptDatas = scriptDatas;
	}
}
