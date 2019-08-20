package com.elitecore.elitesm.web.systemstartup.defaultsetup.util;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;

public enum DefaultSetupClassNameAndProperty {
	RADIUSSERVICEPOLICY(RadServicePolicyData.class,"def_RadiusServicePolicy"),
	TRANSACTIONLOGGERPLUGIN(PluginInstData.class,"def_RadiusTransactionLogger"),
	CONCURRENTLOGINPOLICY(ConcurrentLoginPolicyData.class,"def_ConcurrentLoginPolicy"),
	RADIUSSESSIONMANAGER(SessionManagerInstanceData.class,"def_SessionManager"), 
	RADIUSCLASSICCSVDRIVER(DriverInstanceData.class,"def_RadiusClassicCSVDriver"), 
	RADIUSDBAUTHDRIVER(DriverInstanceData.class,"def_RadiusDBAuthDriver"), 
	DATABASEDATASOURCE(DatabaseDSData.class,"def_DatabaseDatasource"); 
	
	public String propertyName;
	public Class<?> className;
	
	private static final Map<Class<?>,DefaultSetupClassNameAndProperty> map;
	public static final DefaultSetupClassNameAndProperty[] VALUES = values();
	
	static {
		map = new HashMap<Class<?>,DefaultSetupClassNameAndProperty>();
		for (DefaultSetupClassNameAndProperty type : VALUES) {
			map.put(type.className, type);
		}
	}
	
	private DefaultSetupClassNameAndProperty(Class<?> className, String name) {
		this.className = className;
		this.propertyName = name;
	}
	
	public static DefaultSetupClassNameAndProperty fromModuleName(Class<?> className) {
		return map.get(className);
	}
}
