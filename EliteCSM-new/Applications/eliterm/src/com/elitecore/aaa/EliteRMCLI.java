package com.elitecore.aaa;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.BaseCLI;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.logger.EliteRollingFileLogger;

public class EliteRMCLI extends BaseCLI {
	
	public EliteRMCLI(String serverHome, String serverName, int defaultPort, String defaultHostname) {
		super(serverHome, serverName, defaultPort, defaultHostname);	
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		String EliteRMHome = System.getenv("ELITERM_HOME");
		if (EliteRMHome == null){
			System.out.println("set ELITERM_HOME env variable point to Elite RM home.");
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
			try {
				if(EliteRMHome.trim().equals("..")){
					File serverHome = new File(EliteRMHome);
						EliteRMHome = serverHome.getCanonicalPath();
				}
		} catch (IOException e) {
			System.out.println("ELITERM_HOME not valid : " + EliteRMHome);
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
		CLIExitStatusCodes exitStatus = CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
				String localHost;
				try{
					localHost = InetAddress.getLocalHost().getHostName();
				}catch(UnknownHostException ex){
					localHost = "unknown_host";
				}
				EliteRollingFileLogger rmCLILogger = 
					new EliteRollingFileLogger.Builder(localHost, 
							EliteRMHome + File.separator + "logs" + File.separator + "eliterm-cli")
					.build();
				LogManager.setDefaultLogger(rmCLILogger);
		exitStatus = new EliteRMCLI(EliteRMHome,"Elite RM Server", 4433, localHost).start(args);
		System.exit(exitStatus.exitStatusCode);
	}
	
	public String getServerDescription(){
		return Version.getModuleName() + " [" + Version.getVersion() +"]";
	}

	protected String getConsolePrompt(){
		return "[ELITERM]$ ";
	}
}
