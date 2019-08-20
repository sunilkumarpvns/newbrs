package com.elitecore.netvertex.core.util;

import java.io.File;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.netvertex.EliteNetVertexServer;

public class ConfigLogger {
    
	private static final String MODULE = "Config-Logger";
	private static ConfigLogger configLogger;
	private EliteRollingFileLogger serverConfigLogger;


	static{
		configLogger = new ConfigLogger();
	}

	private ConfigLogger() {
		String serverHome=System.getenv(EliteNetVertexServer.SERVER_HOME);
		String logFileName=null;
		if(serverHome!=null){
			logFileName=serverHome+ File.separator+ "logs" + File.separator + "configurationLogs"+ File.separator + "netvertex-configuration";
		}else{
			logFileName="netvertex-configuration";
		}
		try {
			serverConfigLogger =
					new EliteRollingFileLogger.Builder("NetvertexServer",
							logFileName)
							.rollingType(EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE)
							.rollingUnit(EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY)
							.maxRolledUnits(1)
							.compressRolledUnits(true)
							.build();
			serverConfigLogger.setLogLevel(LogLevel.INFO.name());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to initialize config logger "+e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	public void init(String serverHome) throws InitializationFailedException{
				String logfilename = serverHome + File.separator + "logs"+ File.separator + "configurationLogs" + File.separator+ "netvertex-configuration";
				serverConfigLogger =
					new EliteRollingFileLogger.Builder("NetvertexServer",
							logfilename)
					.rollingType(EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE)
					.rollingUnit(EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY)
					.maxRolledUnits(1)
					.compressRolledUnits(true)
					.build();
				serverConfigLogger.setLogLevel(LogLevel.INFO.name());
	}

	public static ConfigLogger getInstance(){
		return configLogger;
	}
	
	public EliteRollingFileLogger getServerConfigLogger() {
		return serverConfigLogger;
	}
	
	public void info(String module, String strMessage) {
    	serverConfigLogger.info(module, strMessage);
    	        
    }
}
