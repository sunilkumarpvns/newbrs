package com.elitecore.aaa;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.BaseCLI;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.logger.EliteRollingFileLogger;

public class EliteAAACLI extends BaseCLI {
	
	public EliteAAACLI(String serverHome, String serverName, int defaultPort, String defaultHostname) {
		super(serverHome, serverName, defaultPort, defaultHostname);	
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		String EliteAAAHome = System.getenv("ELITEAAA_HOME");
		if (EliteAAAHome == null){
			System.out.println("set ELITEAAA_HOME env variable point to Elite AAA home.");
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
			try {
				if(EliteAAAHome.trim().equals("..")){
					File serverHome = new File(EliteAAAHome);
						EliteAAAHome = serverHome.getCanonicalPath();
				}
		} catch (IOException e) {
			System.out.println("ELITEAAA_HOME not valid : " + EliteAAAHome);
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
		CLIExitStatusCodes exitStatus = CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
				String localHost;
				try{
					localHost = InetAddress.getLocalHost().getHostName();
				}catch(UnknownHostException ex){
					localHost = "unknown_host";
				}
				
				ILogger cliLogger = new EliteRollingFileLogger.Builder(
						localHost, EliteAAAHome + File.separator + "logs" + File.separator 
						+ "eliteaaa-cli")
					.build();
				
				LogManager.setDefaultLogger(cliLogger);
				
		exitStatus = new EliteAAACLI(EliteAAAHome,"Elite AAA Server", 4434, localHost).start(args);
		System.exit(exitStatus.exitStatusCode);
	}
	
	public String getServerDescription(){
		return Version.getModuleName() + " [" + Version.getVersion() +"]";
	}

	protected String getConsolePrompt(){
		return "[ELITEAAA]$ ";
	}
}
