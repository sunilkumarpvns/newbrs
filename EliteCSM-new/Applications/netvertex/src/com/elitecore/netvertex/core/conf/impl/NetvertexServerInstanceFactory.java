package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;
import com.elitecore.corenetvertex.pm.pkg.factory.FactoryUtils;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NetvertexServerInstanceFactory {

	private static final String MODULE = "NVSVR-FCTRY";
	private static final int MIN_PORT = 1025;
	private static final int MAX_PORT = 65535;

	public NetvertexServerInstanceConfigurationImpl create(ServerInstanceData serverInstanceData, ServerProfileData serverProfileData,ScriptDataFactory scriptDataFactory) {
		return create(serverInstanceData, new LogDetailProvider() {

			@Override
			public String getLogLevel() {
				return serverProfileData.getLogLevel();
			}

			@Override
			public Integer getRollingType() {
				return serverProfileData.getRollingType();
			}

			@Override
			public Integer getRollingUnits() {
				return serverProfileData.getRollingUnits();
			}

			@Override
			public Integer getMaxRolledUnits() {
				return serverProfileData.getMaxRolledUnits();
			}
		},scriptDataFactory);
	}

	public NetvertexServerInstanceConfigurationImpl create(ServerInstanceData serverInstanceData,
			OfflineRncServerProfileData offlineRncServerProfileData, ScriptDataFactory scriptDataFactory) {
		
		return create(serverInstanceData, new LogDetailProvider() {
			
			@Override
			public Integer getRollingUnits() {
				return offlineRncServerProfileData.getRollingUnits();
			}
			
			@Override
			public Integer getRollingType() {
				return offlineRncServerProfileData.getRollingType();
			}
			
			@Override
			public Integer getMaxRolledUnits() {
				return offlineRncServerProfileData.getMaxRolledUnits();
			}
			
			@Override
			public String getLogLevel() {
				return offlineRncServerProfileData.getLogLevel();
			}
		},scriptDataFactory);
	}
	
	private interface LogDetailProvider {
		String getLogLevel();
		Integer getRollingType();
		Integer getRollingUnits();
		Integer getMaxRolledUnits();
	}
	
	private NetvertexServerInstanceConfigurationImpl create(ServerInstanceData serverInstanceData, 
			LogDetailProvider logDetails,ScriptDataFactory scriptDataFactory) {
		//NVSVR

		String logLevel = logDetails.getLogLevel();
		if (logLevel == null || getLogLevel(logLevel) == null) {
			logLevel = LogLevel.WARN.name();
		} else if(LogLevel.valueOf(logLevel.trim()) == null){
			logLevel = "ERROR";
		}

		RollingType rollingType;
		if (logDetails.getRollingType() == null) {
			rollingType = RollingType.TIME_BASED;
		} else {
			rollingType = RollingType.fromValue(logDetails.getRollingType(), RollingType.TIME_BASED);
		}

		String rollingUnit = getRollingUnit(logDetails, rollingType);

		Integer maxRollingUnit = logDetails.getMaxRolledUnits();
		if (maxRollingUnit == null) {
			maxRollingUnit = 10;
		}

		String strSnmpAddress = serverInstanceData.getSnmpUrl();
		String snmpAddress = "0.0.0.0";
		int snmpPort = 1161;
		if (Strings.isNullOrBlank(strSnmpAddress)) {
			getLogger().warn(MODULE, "Using default SNMP address " + snmpAddress + ":" + snmpPort + ". Reason: SNMP address not configured");
		} else {
			try {
				URLData urlData = URLParser.parse(strSnmpAddress.trim());
				snmpAddress = urlData.getHost();
				if (FactoryUtils.isValidPort(urlData.getPort())) {
					snmpPort = urlData.getPort();
				} else {
					getLogger().warn(MODULE, "Considering default value " + snmpPort + " for SNMP port. Reason: invalid value: "
							+ urlData.getPort() + ", port must be between [" + MIN_PORT + " to " + MAX_PORT + "]");
				}
			} catch (InvalidURLException e) {
				getLogger().warn(MODULE, "Using default SNMP address " + snmpAddress + ":" + snmpPort + ". Reason: error while parsing URL: "
						+ strSnmpAddress);
				getLogger().trace(e);
			}
		}

		String strRestWSAddress = serverInstanceData.getRestApiUrl();

		String restWsAddress = null;
		int restWsPort = 0;
		if (Strings.isNullOrBlank(strRestWSAddress)) {
			getLogger().warn(MODULE, "Rest WS address not configured. Rest WS Server will not start.");
			restWsAddress = null;
			restWsPort = 0;
		} else {
			try {
				URLData urlData = URLParser.parse(strRestWSAddress.trim());
				restWsAddress = urlData.getHost();
				restWsPort = urlData.getPort();

			} catch (InvalidURLException e) {
				getLogger().warn(MODULE, "Using default Rest WS address " + restWsAddress + ":" + restWsPort
						+ ". Reason: Error while parsing URL: " + strRestWSAddress);
				getLogger().trace(MODULE, e);
			}
		}


		return new NetvertexServerInstanceConfigurationImpl(serverInstanceData.getName(),
				logLevel,
				rollingType,
				rollingUnit,
				maxRollingUnit,  /*compRollingUnit*/ false ,
				snmpAddress,
				snmpPort,
				/*httpPort*/8080,
				restWsAddress,
				restWsPort,
				/*configuredServiceList*/null,scriptDataFactory.createServerInstanceGroovyData(serverInstanceData.getGroovyScriptDatas()));
	}

	private String getRollingUnit(LogDetailProvider logDetails, RollingType rollingType) {
		String rollingUnit = null;
		if(rollingType == RollingType.TIME_BASED) {
			rollingUnit = TimeBasedRollingUnit.DAILY.unit;
			if (logDetails.getRollingUnits() != null) {

				TimeBasedRollingUnit timeBasedRollingUnit = TimeBasedRollingUnit.fromValue(logDetails.getRollingUnits().toString());
				if(timeBasedRollingUnit != null) {
					rollingUnit = timeBasedRollingUnit.unit;
				}
			}
		} else {
			if (logDetails.getRollingUnits() != null) {
				rollingUnit = logDetails.getRollingUnits().toString();
			} else {
				rollingUnit = String.valueOf(DataUnit.GB.toKB(1));
			}
		}
		return rollingUnit;
	}

	private LogLevel getLogLevel(String logLevel) {
		try {
			return LogLevel.valueOf(logLevel.trim());
		} catch (Exception ex) {
			getLogger().trace(MODULE, ex);
			getLogger().error(MODULE, "Invalid value of log level " +  logLevel);
		}

		return null;
	}
}
