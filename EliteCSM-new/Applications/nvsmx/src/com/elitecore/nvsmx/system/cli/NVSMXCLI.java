package com.elitecore.nvsmx.system.cli;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.BaseCLI;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.nvsmx.Version;

public class NVSMXCLI extends BaseCLI {

	public NVSMXCLI(String serverHome, String serverName, int defaultPort, String defaultHostname) {
		super(serverHome, serverName, defaultPort, defaultHostname);	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String nvSmxHome = System.getenv("NVSMX_HOME");
		if (nvSmxHome == null) {
			System.out.println("set NVSMX_HOME env variable point to NVSMX server.");
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
		try {
			if (nvSmxHome.trim().equals("..")) {
				File serverHome = new File(nvSmxHome);
				nvSmxHome = serverHome.getCanonicalPath();
			}
		} catch (IOException e) {
			System.out.println("NVSMX_HOME not valid : " + nvSmxHome);
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
		CLIExitStatusCodes exitStatus = CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
		String localHost;
		try {
			localHost = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			localHost = "unknown_host";
		}
		EliteRollingFileLogger netVertexCLILogger =
				new EliteRollingFileLogger.Builder(localHost,
						nvSmxHome + File.separator + "logs" + File.separator + "nvsmx-cli")
						.build();

		LogManager.setDefaultLogger(netVertexCLILogger);

		exitStatus = new NVSMXCLI(nvSmxHome, "NVSMX Server", 3435, localHost).start(args);
		System.exit(exitStatus.exitStatusCode);
	}
	
	public String getServerDescription(){
		return Version.getModuleName() + " [" + Version.getVersion() +"]";
	}

	protected String getConsolePrompt(){
		return "NVSMX> ";
	}

}
